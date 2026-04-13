<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class UserDailyChallenge extends Model
{
    use HasFactory;

    protected $fillable = [
        'user_id',
        'challenge_id',
        'challenge_date',
        'is_completed',
        'completed_at',
        'reward_granted',
    ];

    protected $casts = [
        'challenge_date' => 'date',
        'is_completed' => 'boolean',
        'completed_at' => 'datetime',
        'reward_granted' => 'boolean',
    ];

    public function user()
    {
        return $this->belongsTo(User::class);
    }

    public function challenge()
    {
        return $this->belongsTo(Challenge::class);
    }
}
