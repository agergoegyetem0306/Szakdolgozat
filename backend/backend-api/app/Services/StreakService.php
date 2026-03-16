<?php

namespace App\Services;

use App\Models\ActivityLog;
use App\Models\FoodLog;
use App\Models\User;
use Carbon\Carbon;

class StreakService
{
    public function recalculate(User $user): array
    {
        $today = Carbon::today();

        $currentStreak = 0;
        $cursor = $today->copy();

        while ($this->isSuccessfulDay($user, $cursor->toDateString())) {
            $currentStreak++;
            $cursor->subDay();
        }

        $user->current_streak = $currentStreak;

        if ($currentStreak > $user->best_streak) {
            $user->best_streak = $currentStreak;
        }

        $user->save();

        return [
            'current_streak' => $user->current_streak,
            'best_streak' => $user->best_streak,
        ];
    }

    public function isSuccessfulDay(User $user, string $date): bool
    {
        $activityPoints = (float) ActivityLog::where('user_id', $user->id)
            ->where('activity_date', $date)
            ->sum('points');

        $consumedCalories = (float) FoodLog::where('user_id', $user->id)
            ->where('consumed_date', $date)
            ->sum('calories');

        $activityGoal = 200.0;
        $calorieTarget = (float) ($user->calorie_target ?? 2200);
        $goalType = $user->goal_type ?? 'maintain';

        if ($activityPoints < $activityGoal || $consumedCalories <= 0) {
            return false;
        }

        if (in_array($goalType, ['modest_bulk', 'aggressive_bulk'])) {
            return $consumedCalories >= $calorieTarget;
        }

        return $consumedCalories <= $calorieTarget;
    }
}
