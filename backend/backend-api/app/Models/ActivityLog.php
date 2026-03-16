<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class ActivityLog extends Model
{
    use HasFactory;

    protected $fillable = [
        'user_id',
        'category',
        'activity_type',
        'intensity',
        'duration_minutes',
        'points',
        'activity_date',
    ];

    protected $casts = [
        'activity_date' => 'date',
        'points' => 'double',
    ];

    public function user()
    {
        return $this->belongsTo(User::class);
    }
}
