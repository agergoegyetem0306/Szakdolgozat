<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\DailyXpLog;
use App\Services\XpService;
use Carbon\Carbon;
use Illuminate\Http\Request;

class XpController extends Controller
{
    public function summary(Request $request, XpService $xpService)
    {
        $user = $request->get('auth_user');
        $today = Carbon::today()->toDateString();

        $dailyXpLog = DailyXpLog::where('user_id', $user->id)
            ->where('xp_date', $today)
            ->first();

        $level = $xpService->calculateLevelFromXp($user->xp);
        $progress = $xpService->getCurrentLevelProgress($user->xp);

        return response()->json([
            'xp' => $user->xp,
            'level' => $level,
            'current_level_xp_min' => $progress['current_level_xp_min'],
            'next_level_xp' => $progress['next_level_xp'],
            'xp_in_level' => $progress['xp_in_level'],
            'xp_needed_for_next_level' => $progress['xp_needed_for_next_level'],
            'today_activity_xp' => $dailyXpLog?->activity_xp ?? 0,
            'today_nutrition_xp' => $dailyXpLog?->nutrition_xp ?? 0,
            'today_total_xp' => $dailyXpLog?->total_xp ?? 0,
            'current_streak' => $user->current_streak ?? 0,
            'best_streak' => $user->best_streak ?? 0,
        ]);
    }
}
