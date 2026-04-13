<?php

namespace App\Services;

use App\Models\ActivityLog;
use App\Models\FoodLog;
use App\Models\User;
use App\Models\UserDailyChallenge;
use Carbon\Carbon;

class ChallengeProgressService
{
    public function checkTodayChallenge(User $user): ?UserDailyChallenge
    {
        $today = Carbon::today()->toDateString();

        $dailyChallenge = UserDailyChallenge::with('challenge')
            ->where('user_id', $user->id)
            ->where('challenge_date', $today)
            ->first();

        if (!$dailyChallenge) {
            return null;
        }

        $progress = $this->evaluateChallengeWithProgress($user, $dailyChallenge);
        $isCompletedNow = $progress['is_completed'];

        if ($isCompletedNow && !$dailyChallenge->is_completed) {
            $dailyChallenge->is_completed = true;
            $dailyChallenge->completed_at = now();
        }

        if ($isCompletedNow && !$dailyChallenge->reward_granted) {
            $user->xp += $dailyChallenge->challenge->reward_xp;
            $user->save();

            $dailyChallenge->reward_granted = true;
        }

        if (!$isCompletedNow) {
            $dailyChallenge->is_completed = false;
            $dailyChallenge->completed_at = null;
        }

        $dailyChallenge->save();

        $dailyChallenge = $dailyChallenge->fresh('challenge');
        $dailyChallenge->progress = $progress;

        return $dailyChallenge;
    }

    private function evaluateChallengeWithProgress(User $user, UserDailyChallenge $dailyChallenge): array
    {
        $challenge = $dailyChallenge->challenge;
        $today = Carbon::today()->toDateString();

        $current = 0;
        $target = $challenge->target_value;

        switch ($challenge->challenge_type) {

            case 'activity_duration':
                $current = (int) ActivityLog::where('user_id', $user->id)
                    ->where('activity_date', $today)
                    ->where('activity_type', $challenge->activity_type)
                    ->sum('duration_minutes');
                break;

            case 'activity_points':
                $current = (float) ActivityLog::where('user_id', $user->id)
                    ->where('activity_date', $today)
                    ->where('category', $challenge->category)
                    ->sum('points');
                break;

            case 'activity_points_total':
                $current = (float) ActivityLog::where('user_id', $user->id)
                    ->where('activity_date', $today)
                    ->sum('points');
                break;

            case 'protein_min':
                $current = (float) FoodLog::where('user_id', $user->id)
                    ->where('consumed_date', $today)
                    ->sum('protein');
                break;

            case 'food_log_count':
                $current = (int) FoodLog::where('user_id', $user->id)
                    ->where('consumed_date', $today)
                    ->count();
                break;
        }

        $isCompleted = $current >= $target;

        return [
            'is_completed' => $isCompleted,
            'current_value' => round($current, 1),
            'target_value' => $target,
            'progress_ratio' => $target > 0 ? min($current / $target, 1) : 0,
        ];
    }

    private function checkActivityDuration(User $user, string $date, ?string $activityType, int $targetValue): bool
    {
        if (!$activityType) {
            return false;
        }

        $duration = (int) ActivityLog::where('user_id', $user->id)
            ->where('activity_date', $date)
            ->where('activity_type', $activityType)
            ->sum('duration_minutes');

        return $duration >= $targetValue;
    }

    private function checkCategoryActivityPoints(User $user, string $date, ?string $category, int $targetValue): bool
    {
        if (!$category) {
            return false;
        }

        $points = (float) ActivityLog::where('user_id', $user->id)
            ->where('activity_date', $date)
            ->where('category', $category)
            ->sum('points');

        return $points >= $targetValue;
    }

    private function checkTotalActivityPoints(User $user, string $date, int $targetValue): bool
    {
        $points = (float) ActivityLog::where('user_id', $user->id)
            ->where('activity_date', $date)
            ->sum('points');

        return $points >= $targetValue;
    }

    private function checkProteinMin(User $user, string $date, int $targetValue): bool
    {
        $protein = (float) FoodLog::where('user_id', $user->id)
            ->where('consumed_date', $date)
            ->sum('protein');

        return $protein >= $targetValue;
    }

    private function checkFoodLogCount(User $user, string $date, int $targetValue): bool
    {
        $count = (int) FoodLog::where('user_id', $user->id)
            ->where('consumed_date', $date)
            ->count();

        return $count >= $targetValue;
    }
}
