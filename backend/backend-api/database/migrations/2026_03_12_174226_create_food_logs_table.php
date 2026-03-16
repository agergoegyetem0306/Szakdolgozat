<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('food_logs', function (Blueprint $table) {
            $table->id();
            $table->unsignedBigInteger('user_id');
            $table->unsignedBigInteger('food_id');
            $table->double('quantity_grams');
            $table->double('calories');
            $table->double('protein');
            $table->double('carbs');
            $table->double('fat');
            $table->date('consumed_date');
            $table->timestamps();
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('food_logs');
    }
};
