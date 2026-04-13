<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Foundation\Auth\User as Authenticatable;
use Illuminate\Notifications\Notifiable;

class User extends Authenticatable
{
    use HasFactory, Notifiable;

    protected $fillable = [
        'name',
        'email',
        'password',
        'api_token',
        'gender',
        'age',
        'height',
        'weight',
        'goal_type',
        'calorie_target',
        'xp',
        'current_streak',
        'best_streak',
    ];

    protected $hidden = [
        'password',
        'remember_token',
        'api_token',
    ];

    protected function casts(): array
    {
        return [
            'email_verified_at' => 'datetime',
            'password' => 'hashed',
        ];
    }
    public function activityLogs()
    {
        return $this->hasMany(ActivityLog::class);
    }
    public function foodLogs()
    {
        return $this->hasMany(FoodLog::class);
    }
    public function dailyXpLogs()
    {
        return $this->hasMany(DailyXpLog::class);
    }
    public function dailyChallenges()
    {
        return $this->hasMany(UserDailyChallenge::class);
    }
    public function createdLeaderboards()
    {
        return $this->hasMany(Leaderboard::class, 'created_by');
    }

    public function leaderboardMemberships()
    {
        return $this->hasMany(LeaderboardMember::class);
    }

}
