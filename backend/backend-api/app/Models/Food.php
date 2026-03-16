<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Food extends Model
{
    use HasFactory;

    protected $table = 'foods';

    protected $fillable = [
        'name',
        'calories_per_100g',
        'protein_per_100g',
        'carbs_per_100g',
        'fat_per_100g',
        'is_custom',
        'user_id',
    ];

    protected $casts = [
        'calories_per_100g' => 'double',
        'protein_per_100g' => 'double',
        'carbs_per_100g' => 'double',
        'fat_per_100g' => 'double',
        'is_custom' => 'boolean',
    ];

    public function user()
    {
        return $this->belongsTo(User::class);
    }

    public function foodLogs()
    {
        return $this->hasMany(FoodLog::class);
    }
}
