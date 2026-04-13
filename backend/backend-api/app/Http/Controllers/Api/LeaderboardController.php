<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\DailyXpLog;
use App\Models\Leaderboard;
use App\Models\LeaderboardMember;
use Illuminate\Http\Request;
use Illuminate\Support\Carbon;
use Illuminate\Support\Str;

class LeaderboardController extends Controller
{
    public function index(Request $request)
    {
        $user = $request->get('auth_user');

        $leaderboards = Leaderboard::whereHas('members', function ($query) use ($user) {
            $query->where('user_id', $user->id);
        })
            ->withCount('members')
            ->orderBy('created_at', 'desc')
            ->get();

        return response()->json($leaderboards);
    }

    public function store(Request $request)
    {
        $user = $request->get('auth_user');

        $validated = $request->validate([
            'name' => 'required|string|max:255',
            'join_code' => 'nullable|string|min:4|max:20|unique:leaderboards,join_code',
        ]);

        $joinCode = $validated['join_code'] ?? $this->generateJoinCode();

        $leaderboard = Leaderboard::create([
            'name' => $validated['name'],
            'join_code' => strtoupper($joinCode),
            'created_by' => $user->id,
        ]);

        LeaderboardMember::create([
            'leaderboard_id' => $leaderboard->id,
            'user_id' => $user->id,
        ]);

        return response()->json([
            'message' => 'Ranglista sikeresen létrehozva.',
            'leaderboard' => $leaderboard->loadCount('members'),
        ], 201);
    }

    public function join(Request $request)
    {
        $user = $request->get('auth_user');

        $validated = $request->validate([
            'join_code' => 'required|string',
        ]);

        $leaderboard = Leaderboard::where('join_code', strtoupper($validated['join_code']))->first();

        if (!$leaderboard) {
            return response()->json([
                'message' => 'Nincs ilyen csatlakozási kódú ranglista.',
            ], 404);
        }

        $alreadyMember = LeaderboardMember::where('leaderboard_id', $leaderboard->id)
            ->where('user_id', $user->id)
            ->exists();

        if ($alreadyMember) {
            return response()->json([
                'message' => 'Már tagja vagy ennek a ranglistának.',
            ], 422);
        }

        LeaderboardMember::create([
            'leaderboard_id' => $leaderboard->id,
            'user_id' => $user->id,
        ]);

        return response()->json([
            'message' => 'Sikeres csatlakozás a ranglistához.',
            'leaderboard' => $leaderboard->loadCount('members'),
        ]);
    }

    public function show(Request $request, int $id)
    {
        $user = $request->get('auth_user');

        $leaderboard = Leaderboard::with(['creator', 'members.user'])->find($id);

        if (!$leaderboard) {
            return response()->json([
                'message' => 'A ranglista nem található.',
            ], 404);
        }

        $isMember = LeaderboardMember::where('leaderboard_id', $leaderboard->id)
            ->where('user_id', $user->id)
            ->exists();

        if (!$isMember) {
            return response()->json([
                'message' => 'Nincs hozzáférésed ehhez a ranglistához.',
            ], 403);
        }

        $weekStart = Carbon::now()->startOfWeek()->toDateString();
        $weekEnd = Carbon::now()->endOfWeek()->toDateString();

        $memberIds = $leaderboard->members->pluck('user_id')->toArray();

        $weeklyXpMap = DailyXpLog::whereIn('user_id', $memberIds)
            ->whereBetween('xp_date', [$weekStart, $weekEnd])
            ->get()
            ->groupBy('user_id')
            ->map(function ($logs) {
                return (int) $logs->sum('total_xp');
            });

        $rankedMembers = $leaderboard->members
            ->map(function ($member) use ($weeklyXpMap) {
                return [
                    'user_id' => $member->user->id,
                    'name' => $member->user->name,
                    'weekly_xp' => $weeklyXpMap[$member->user->id] ?? 0,
                ];
            })
            ->sortByDesc('weekly_xp')
            ->values();

        $rankedMembers = $rankedMembers->map(function ($member, $index) use ($user) {
            return [
                'rank' => $index + 1,
                'user_id' => $member['user_id'],
                'name' => $member['name'],
                'weekly_xp' => $member['weekly_xp'],
                'is_me' => $member['user_id'] === $user->id,
            ];
        })->values();

        $myEntry = $rankedMembers->firstWhere('user_id', $user->id);

        return response()->json([
            'leaderboard' => [
                'id' => $leaderboard->id,
                'name' => $leaderboard->name,
                'join_code' => $leaderboard->join_code,
                'created_by' => $leaderboard->created_by,
                'creator_name' => $leaderboard->creator?->name,
                'member_count' => $leaderboard->members->count(),
                'week_start' => $weekStart,
                'week_end' => $weekEnd,
            ],
            'my_rank' => $myEntry['rank'] ?? null,
            'members' => $rankedMembers,
        ]);
    }

    private function generateJoinCode(): string
    {
        do {
            $code = strtoupper(Str::random(6));
        } while (Leaderboard::where('join_code', $code)->exists());

        return $code;
    }
}
