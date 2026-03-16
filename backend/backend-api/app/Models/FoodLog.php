<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class FoodLog extends Model
{
    use HasFactory;

    protected $table = 'food_logs';

    protected $fillable = [
        'user_id',
        'food_id',
        'quantity_grams',
        'calories',
        'protein',
        'carbs',
        'fat',
        'consumed_date',
    ];

    protected $casts = [
        'quantity_grams' => 'double',
        'calories' => 'double',
        'protein' => 'double',
        'carbs' => 'double',
        'fat' => 'double',
        'consumed_date' => 'date',
    ];

    public function user()
    {
        return $this->belongsTo(User::class);
    }

    public function food()
    {
        return $this->belongsTo(Food::class);
    }
}
