<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class DailyXpLog extends Model
{
    use HasFactory;

    protected $table = 'daily_xp_logs';

    protected $fillable = [
        'user_id',
        'xp_date',
        'activity_xp',
        'nutrition_xp',
        'total_xp',
    ];

    protected $casts = [
        'xp_date' => 'date',
        'activity_xp' => 'integer',
        'nutrition_xp' => 'integer',
        'total_xp' => 'integer',
    ];

    public function user()
    {
        return $this->belongsTo(User::class);
    }
}
