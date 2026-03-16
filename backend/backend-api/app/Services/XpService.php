<?php

namespace App\Services;

use App\Models\ActivityLog;
use App\Models\DailyXpLog;
use App\Models\FoodLog;
use App\Models\User;
use Carbon\Carbon;

class XpService
{
    public function recalculateToday(User $user): DailyXpLog
    {
        $today = Carbon::today()->toDateString();

        $activityXp = $this->calculateTodayActivityXp($user, $today);
        $nutritionXp = $this->calculateTodayNutritionXp($user, $today);
        $totalXp = $activityXp + $nutritionXp;

        $dailyXpLog = DailyXpLog::firstOrNew([
            'user_id' => $user->id,
            'xp_date' => $today,
        ]);

        $previousTotalXp = $dailyXpLog->exists ? $dailyXpLog->total_xp : 0;
        $xpDifference = $totalXp - $previousTotalXp;

        $dailyXpLog->activity_xp = $activityXp;
        $dailyXpLog->nutrition_xp = $nutritionXp;
        $dailyXpLog->total_xp = $totalXp;
        $dailyXpLog->save();

        $user->xp = max(0, $user->xp + $xpDifference);
        $user->save();

        return $dailyXpLog;
    }

    private function calculateTodayActivityXp(User $user, string $date): int
    {
        $activities = ActivityLog::where('user_id', $user->id)
            ->where('activity_date', $date)
            ->get();

        $totalPoints = (float) $activities->sum('points');
        $dailyGoal = 200.0;

        if ($dailyGoal <= 0) {
            return 0;
        }

        $ratio = min($totalPoints / $dailyGoal, 1.0);

        return (int) round(100 * $ratio);
    }

    private function calculateTodayNutritionXp(User $user, string $date): int
    {
        $logs = FoodLog::where('user_id', $user->id)
            ->where('consumed_date', $date)
            ->get();

        $consumedCalories = (float) $logs->sum('calories');
        $targetCalories = (float) ($user->calorie_target ?? 2200);
        $goalType = $user->goal_type ?? 'maintain';

        if ($targetCalories <= 0 || $consumedCalories <= 0) {
            return 0;
        }

        if (in_array($goalType, ['modest_bulk', 'aggressive_bulk'])) {
            if ($consumedCalories >= $targetCalories) {
                return 100;
            }

            $underRatio = ($targetCalories - $consumedCalories) / $targetCalories;
            $xp = 100 * max(0, 1 - $underRatio);

            return (int) round($xp);
        }

        if ($consumedCalories <= $targetCalories) {
            return 100;
        }

        $overRatio = ($consumedCalories - $targetCalories) / $targetCalories;
        $xp = 100 * max(0, 1 - $overRatio);

        return (int) round($xp);
    }

    public function calculateLevelFromXp(int $xp): int
    {
        if ($xp < 100) {
            return 1;
        }

        $level = 2;
        $threshold = 100;

        if ($xp < 250) {
            return 2;
        }

        $level = 3;
        $threshold = 250;

        while ($xp >= $threshold) {
            $nextThreshold = (int) ceil($threshold * 1.5);

            if ($xp < $nextThreshold) {
                return $level;
            }

            $threshold = $nextThreshold;
            $level++;
        }

        return $level;
    }

    public function getCurrentLevelProgress(int $xp): array
    {
        if ($xp < 100) {
            return [
                'level' => 1,
                'current_level_xp_min' => 0,
                'next_level_xp' => 100,
                'xp_in_level' => $xp,
                'xp_needed_for_next_level' => 100 - $xp,
            ];
        }

        if ($xp < 250) {
            return [
                'level' => 2,
                'current_level_xp_min' => 100,
                'next_level_xp' => 250,
                'xp_in_level' => $xp - 100,
                'xp_needed_for_next_level' => 250 - $xp,
            ];
        }

        $level = 3;
        $currentThreshold = 250;

        while (true) {
            $nextThreshold = (int) ceil($currentThreshold * 1.5);

            if ($xp < $nextThreshold) {
                return [
                    'level' => $level,
                    'current_level_xp_min' => $currentThreshold,
                    'next_level_xp' => $nextThreshold,
                    'xp_in_level' => $xp - $currentThreshold,
                    'xp_needed_for_next_level' => $nextThreshold - $xp,
                ];
            }

            $currentThreshold = $nextThreshold;
            $level++;
        }
    }
}
