<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\ActivityLog;
use App\Models\DailyXpLog;
use App\Models\FoodLog;
use App\Services\StreakService;
use Carbon\Carbon;
use Illuminate\Http\Request;

class WeeklyStatsController extends Controller
{
    public function summary(Request $request, StreakService $streakService)
    {
        $user = $request->get('auth_user');

        $days = [];
        $weeklyActivityPoints = 0;
        $weeklyCalories = 0;
        $weeklyXp = 0;
        $successfulDays = 0;

        for ($i = 6; $i >= 0; $i--) {
            $date = Carbon::today()->subDays($i)->toDateString();

            $activityPoints = round(
                (float) ActivityLog::where('user_id', $user->id)
                    ->where('activity_date', $date)
                    ->sum('points'),
                1
            );

            $calories = round(
                (float) FoodLog::where('user_id', $user->id)
                    ->where('consumed_date', $date)
                    ->sum('calories'),
                1
            );

            $dailyXp = DailyXpLog::where('user_id', $user->id)
                ->where('xp_date', $date)
                ->first();

            $xpTotal = $dailyXp?->total_xp ?? 0;
            $xpActivity = $dailyXp?->activity_xp ?? 0;
            $xpNutrition = $dailyXp?->nutrition_xp ?? 0;

            $successful = $streakService->isSuccessfulDay($user, $date);

            if ($successful) {
                $successfulDays++;
            }

            $weeklyActivityPoints += $activityPoints;
            $weeklyCalories += $calories;
            $weeklyXp += $xpTotal;

            $days[] = [
                'date' => $date,
                'activity_points' => $activityPoints,
                'calories' => $calories,
                'xp_total' => $xpTotal,
                'xp_activity' => $xpActivity,
                'xp_nutrition' => $xpNutrition,
                'successful' => $successful,
            ];
        }

        return response()->json([
            'days' => $days,
            'weekly_activity_points' => round($weeklyActivityPoints, 1),
            'weekly_calories' => round($weeklyCalories, 1),
            'weekly_xp' => $weeklyXp,
            'successful_days' => $successfulDays,
        ]);
    }
}
