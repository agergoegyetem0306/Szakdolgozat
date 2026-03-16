<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Food;
use App\Models\FoodLog;
use App\Services\StreakService;
use App\Services\XpService;
use Carbon\Carbon;
use Illuminate\Http\Request;

class FoodController extends Controller
{
    public function index(Request $request)
    {
        $user = $request->get('auth_user');
        $search = trim((string) $request->query('search', ''));

        $foods = Food::query()
            ->where(function ($query) use ($user) {
                $query->where('is_custom', false)
                    ->orWhere(function ($subQuery) use ($user) {
                        $subQuery->where('is_custom', true)
                            ->where('user_id', $user->id);
                    });
            })
            ->when($search !== '', function ($query) use ($search) {
                $query->where('name', 'like', '%' . $search . '%');
            })
            ->orderBy('name')
            ->get();

        return response()->json($foods);
    }

    public function storeCustomFood(Request $request)
    {
        $user = $request->get('auth_user');

        $validated = $request->validate([
            'name' => 'required|string|max:255',
            'calories_per_100g' => 'required|numeric|min:0|max:2000',
            'protein_per_100g' => 'required|numeric|min:0|max:1000',
            'carbs_per_100g' => 'required|numeric|min:0|max:1000',
            'fat_per_100g' => 'required|numeric|min:0|max:1000',
        ]);

        $food = Food::create([
            'name' => $validated['name'],
            'calories_per_100g' => $validated['calories_per_100g'],
            'protein_per_100g' => $validated['protein_per_100g'],
            'carbs_per_100g' => $validated['carbs_per_100g'],
            'fat_per_100g' => $validated['fat_per_100g'],
            'is_custom' => true,
            'user_id' => $user->id,
        ]);

        return response()->json($food, 201);
    }

    public function storeLog(Request $request, XpService $xpService, StreakService $streakService)
    {
        $user = $request->get('auth_user');

        $validated = $request->validate([
            'food_id' => 'required|integer',
            'quantity_grams' => 'required|numeric|min:1|max:5000',
        ]);

        $food = Food::find($validated['food_id']);

        if (!$food) {
            return response()->json([
                'message' => 'A kiválasztott étel nem található.',
            ], 404);
        }

        if ($food->is_custom && $food->user_id !== $user->id) {
            return response()->json([
                'message' => 'Ehhez az egyéni ételhez nincs hozzáférésed.',
            ], 403);
        }

        $multiplier = $validated['quantity_grams'] / 100;

        $calories = round($food->calories_per_100g * $multiplier, 1);
        $protein = round($food->protein_per_100g * $multiplier, 1);
        $carbs = round($food->carbs_per_100g * $multiplier, 1);
        $fat = round($food->fat_per_100g * $multiplier, 1);

        $foodLog = FoodLog::create([
            'user_id' => $user->id,
            'food_id' => $food->id,
            'quantity_grams' => $validated['quantity_grams'],
            'calories' => $calories,
            'protein' => $protein,
            'carbs' => $carbs,
            'fat' => $fat,
            'consumed_date' => Carbon::today()->toDateString(),
        ]);

        $freshUser = $user->fresh();

        $dailyXpLog = $xpService->recalculateToday($freshUser);
        $streakData = $streakService->recalculate($freshUser->fresh());

        return response()->json([
            'food_log' => $foodLog->load('food'),
            'xp' => [
                'activity_xp' => $dailyXpLog->activity_xp,
                'nutrition_xp' => $dailyXpLog->nutrition_xp,
                'total_xp_today' => $dailyXpLog->total_xp,
                'user_total_xp' => $freshUser->fresh()->xp,
            ],
            'streak' => $streakData,
        ], 201);
    }

    public function today(Request $request)
    {
        $user = $request->get('auth_user');
        $today = Carbon::today()->toDateString();

        $logs = FoodLog::with('food')
            ->where('user_id', $user->id)
            ->where('consumed_date', $today)
            ->orderBy('created_at', 'desc')
            ->get();

        return response()->json($logs);
    }

    public function todaySummary(Request $request)
    {
        $user = $request->get('auth_user');
        $today = Carbon::today()->toDateString();

        $logs = FoodLog::where('user_id', $user->id)
            ->where('consumed_date', $today)
            ->get();

        return response()->json([
            'total_calories' => round($logs->sum('calories'), 1),
            'total_protein' => round($logs->sum('protein'), 1),
            'total_carbs' => round($logs->sum('carbs'), 1),
            'total_fat' => round($logs->sum('fat'), 1),
            'daily_calorie_goal' => $user->calorie_target ?? 2200,
            'log_count' => $logs->count(),
        ]);
    }
}
