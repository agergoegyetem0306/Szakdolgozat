<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Services\ChallengeProgressService;
use App\Services\DailyChallengeService;
use Illuminate\Http\Request;

class DailyChallengeController extends Controller
{
    public function today(
        Request $request,
        DailyChallengeService $dailyChallengeService,
        ChallengeProgressService $challengeProgressService
    ) {
        $user = $request->get('auth_user');

        $dailyChallenge = $dailyChallengeService->getOrAssignTodayChallenge($user);
        $dailyChallenge = $challengeProgressService->checkTodayChallenge($user) ?? $dailyChallenge->fresh('challenge');

        return response()->json([
            'id' => $dailyChallenge->id,
            'challenge_date' => $dailyChallenge->challenge_date,
            'is_completed' => $dailyChallenge->is_completed,
            'completed_at' => $dailyChallenge->completed_at,
            'reward_granted' => $dailyChallenge->reward_granted,
            'challenge' => [
                'id' => $dailyChallenge->challenge->id,
                'title' => $dailyChallenge->challenge->title,
                'description' => $dailyChallenge->challenge->description,
                'challenge_type' => $dailyChallenge->challenge->challenge_type,
                'category' => $dailyChallenge->challenge->category,
                'activity_type' => $dailyChallenge->challenge->activity_type,
                'target_value' => $dailyChallenge->challenge->target_value,
                'reward_xp' => $dailyChallenge->challenge->reward_xp,
            ],
            'progress' => [
                'current' => $dailyChallenge->progress['current_value'] ?? 0,
                'target' => $dailyChallenge->progress['target_value'] ?? 0,
                'ratio' => $dailyChallenge->progress['progress_ratio'] ?? 0,
            ],
        ]);
    }
}
