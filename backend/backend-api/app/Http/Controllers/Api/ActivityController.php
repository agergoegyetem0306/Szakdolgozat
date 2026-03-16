<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\ActivityLog;
use App\Services\StreakService;
use App\Services\XpService;
use Illuminate\Http\Request;
use Carbon\Carbon;

class ActivityController extends Controller
{
    public function store(Request $request, XpService $xpService, StreakService $streakService)
    {
        $user = $request->get('auth_user');

        $validated = $request->validate([
            'category' => 'required|in:cardio,strength,mental',
            'activity_type' => 'required|string',
            'intensity' => 'required|in:low,medium,high',
            'duration_minutes' => 'required|integer|min:1|max:600',
        ]);

        $allowedTypes = [
            'cardio' => ['running', 'walking', 'cycling', 'swimming'],
            'strength' => ['weightlifting', 'bodyweight'],
            'mental' => ['meditation', 'yoga', 'reading'],
        ];

        if (!in_array($validated['activity_type'], $allowedTypes[$validated['category']])) {
            return response()->json([
                'message' => 'Az activity_type nem érvényes a kiválasztott kategóriához.',
            ], 422);
        }

        $intensityMultipliers = [
            'low' => 1.0,
            'medium' => 1.5,
            'high' => 2.0,
        ];

        $typeMultipliers = [
            'running' => 1.4,
            'walking' => 1.0,
            'cycling' => 1.2,
            'swimming' => 1.5,
            'weightlifting' => 1.5,
            'bodyweight' => 1.2,
            'meditation' => 1.0,
            'yoga' => 1.2,
            'reading' => 0.7,
        ];

        $points = $validated['duration_minutes']
            * $intensityMultipliers[$validated['intensity']]
            * $typeMultipliers[$validated['activity_type']];

        $activityLog = ActivityLog::create([
            'user_id' => $user->id,
            'category' => $validated['category'],
            'activity_type' => $validated['activity_type'],
            'intensity' => $validated['intensity'],
            'duration_minutes' => $validated['duration_minutes'],
            'points' => round($points, 1),
            'activity_date' => Carbon::today()->toDateString(),
        ]);

        $freshUser = $user->fresh();

        $dailyXpLog = $xpService->recalculateToday($freshUser);
        $streakData = $streakService->recalculate($freshUser->fresh());

        return response()->json([
            'activity' => $activityLog,
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

        $activities = ActivityLog::where('user_id', $user->id)
            ->where('activity_date', $today)
            ->orderBy('created_at', 'desc')
            ->get();

        return response()->json($activities);
    }

    public function todaySummary(Request $request)
    {
        $user = $request->get('auth_user');
        $today = Carbon::today()->toDateString();

        $activities = ActivityLog::where('user_id', $user->id)
            ->where('activity_date', $today)
            ->get();

        $totalPoints = round($activities->sum('points'), 1);
        $cardioPoints = round($activities->where('category', 'cardio')->sum('points'), 1);
        $strengthPoints = round($activities->where('category', 'strength')->sum('points'), 1);
        $mentalPoints = round($activities->where('category', 'mental')->sum('points'), 1);

        return response()->json([
            'total_points' => $totalPoints,
            'daily_goal' => 200,
            'cardio_points' => $cardioPoints,
            'cardio_goal' => 100,
            'strength_points' => $strengthPoints,
            'strength_goal' => 80,
            'mental_points' => $mentalPoints,
            'mental_goal' => 50,
            'activity_count' => $activities->count(),
        ]);
    }
}
