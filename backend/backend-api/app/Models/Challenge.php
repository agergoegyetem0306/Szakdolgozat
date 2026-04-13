<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Challenge extends Model
{
    use HasFactory;

    protected $fillable = [
        'title',
        'description',
        'challenge_type',
        'category',
        'activity_type',
        'target_value',
        'reward_xp',
        'is_active',
    ];

    protected $casts = [
        'target_value' => 'integer',
        'reward_xp' => 'integer',
        'is_active' => 'boolean',
    ];

    public function userDailyChallenges()
    {
        return $this->hasMany(UserDailyChallenge::class);
    }
}
