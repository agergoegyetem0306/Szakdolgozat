<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Services\StreakService;
use App\Services\XpService;
use Illuminate\Http\Request;

class NutritionGoalController extends Controller
{
    public function options(Request $request)
    {
        $user = $request->get('auth_user');

        if (
            !$user->gender ||
            !$user->age ||
            !$user->height ||
            !$user->weight
        ) {
            return response()->json([
                'message' => 'A kalóriacél kiszámításához előbb töltsd ki a profilodat.',
            ], 422);
        }

        $maintenanceCalories = $this->calculateMaintenanceCalories(
            $user->gender,
            $user->age,
            $user->height,
            $user->weight
        );

        $options = $this->buildGoalOptions($maintenanceCalories);

        return response()->json([
            'maintenance_calories' => $maintenanceCalories,
            'current_goal_type' => $user->goal_type,
            'current_calorie_target' => $user->calorie_target,
            'options' => $options,
        ]);
    }

    public function update(Request $request, XpService $xpService, StreakService $streakService)
    {
        $user = $request->get('auth_user');

        if (
            !$user->gender ||
            !$user->age ||
            !$user->height ||
            !$user->weight
        ) {
            return response()->json([
                'message' => 'A kalóriacél beállításához előbb töltsd ki a profilodat.',
            ], 422);
        }

        $validated = $request->validate([
            'goal_type' => 'required|in:aggressive_cut,modest_cut,maintain,modest_bulk,aggressive_bulk',
        ]);

        $maintenanceCalories = $this->calculateMaintenanceCalories(
            $user->gender,
            $user->age,
            $user->height,
            $user->weight
        );

        $options = collect($this->buildGoalOptions($maintenanceCalories));
        $selectedOption = $options->firstWhere('goal_type', $validated['goal_type']);

        if (!$selectedOption) {
            return response()->json([
                'message' => 'Érvénytelen cél típus.',
            ], 422);
        }

        $user->goal_type = $selectedOption['goal_type'];
        $user->calorie_target = $selectedOption['calorie_target'];
        $user->save();

        $freshUser = $user->fresh();

        $dailyXpLog = $xpService->recalculateToday($freshUser);
        $streakData = $streakService->recalculate($freshUser->fresh());

        return response()->json([
            'message' => 'Kalóriacél sikeresen mentve.',
            'goal_type' => $freshUser->goal_type,
            'calorie_target' => $freshUser->calorie_target,
            'maintenance_calories' => $maintenanceCalories,
            'xp' => [
                'activity_xp' => $dailyXpLog->activity_xp,
                'nutrition_xp' => $dailyXpLog->nutrition_xp,
                'total_xp_today' => $dailyXpLog->total_xp,
                'user_total_xp' => $freshUser->fresh()->xp,
            ],
            'streak' => $streakData,
        ]);
    }

    private function calculateMaintenanceCalories(string $gender, int $age, int $height, float $weight): int
    {
        if ($gender === 'male') {
            $bmr = (10 * $weight) + (6.25 * $height) - (5 * $age) + 5;
        } else {
            $bmr = (10 * $weight) + (6.25 * $height) - (5 * $age) - 161;
        }

        $maintenance = $bmr * 1.2;

        return max(1200, (int) round($maintenance));
    }

    private function buildGoalOptions(int $maintenanceCalories): array
    {
        return [
            [
                'goal_type' => 'aggressive_cut',
                'label' => 'Aggressive Cut',
                'calorie_target' => max(1200, $maintenanceCalories - 800),
            ],
            [
                'goal_type' => 'modest_cut',
                'label' => 'Modest Cut',
                'calorie_target' => max(1200, $maintenanceCalories - 500),
            ],
            [
                'goal_type' => 'maintain',
                'label' => 'Maintain Weight',
                'calorie_target' => $maintenanceCalories,
            ],
            [
                'goal_type' => 'modest_bulk',
                'label' => 'Modest Bulk',
                'calorie_target' => $maintenanceCalories + 500,
            ],
            [
                'goal_type' => 'aggressive_bulk',
                'label' => 'Aggressive Bulk',
                'calorie_target' => $maintenanceCalories + 800,
            ],
        ];
    }
}
