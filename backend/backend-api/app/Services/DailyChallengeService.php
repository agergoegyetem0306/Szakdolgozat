<?php

namespace App\Services;

use App\Models\Challenge;
use App\Models\User;
use App\Models\UserDailyChallenge;
use Carbon\Carbon;

class DailyChallengeService
{
    public function getOrAssignTodayChallenge(User $user): UserDailyChallenge
    {
        $today = Carbon::today()->toDateString();

        $existing = UserDailyChallenge::with('challenge')
            ->where('user_id', $user->id)
            ->where('challenge_date', $today)
            ->first();

        if ($existing) {
            return $existing;
        }

        $recentChallengeIds = UserDailyChallenge::where('user_id', $user->id)
            ->where('challenge_date', '>=', Carbon::today()->subDays(7)->toDateString())
            ->pluck('challenge_id')
            ->toArray();

        $query = Challenge::where('is_active', true);

        if (!empty($recentChallengeIds)) {
            $query->whereNotIn('id', $recentChallengeIds);
        }

        $challenge = $query->inRandomOrder()->first();

        if (!$challenge) {
            $challenge = Challenge::where('is_active', true)
                ->inRandomOrder()
                ->first();
        }

        return UserDailyChallenge::create([
            'user_id' => $user->id,
            'challenge_id' => $challenge->id,
            'challenge_date' => $today,
            'is_completed' => false,
            'completed_at' => null,
            'reward_granted' => false,
        ])->load('challenge');
    }
}
