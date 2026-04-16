<?php

use Illuminate\Support\Facades\Route;
use App\Http\Controllers\Api\AuthController;
use App\Http\Controllers\Api\ProfileController;
use App\Http\Controllers\Api\ActivityController;
use App\Http\Controllers\Api\FoodController;
use App\Http\Controllers\Api\NutritionGoalController;
use App\Http\Controllers\Api\XpController;
use App\Http\Controllers\Api\WeeklyStatsController;
use App\Http\Controllers\Api\DailyChallengeController;
use App\Http\Controllers\Api\LeaderboardController;

Route::post('/register', [AuthController::class, 'register']);
Route::post('/login', [AuthController::class, 'login']);
Route::middleware('api.token')->post('/activities', [ActivityController::class, 'store']);
Route::middleware('api.token')->get('/activities/today', [ActivityController::class, 'today']);
Route::middleware('api.token')->get('/activities/today-summary', [ActivityController::class, 'todaySummary']);
Route::middleware('api.token')->get('/profile', [ProfileController::class, 'show']);
Route::middleware('api.token')->put('/profile', [ProfileController::class, 'update']);
Route::middleware('api.token')->get('/foods', [FoodController::class, 'index']);
Route::middleware('api.token')->post('/foods/custom', [FoodController::class, 'storeCustomFood']);
Route::middleware('api.token')->post('/food-logs', [FoodController::class, 'storeLog']);
Route::middleware('api.token')->get('/food-logs/today', [FoodController::class, 'today']);
Route::middleware('api.token')->get('/food-logs/today-summary', [FoodController::class, 'todaySummary']);
Route::middleware('api.token')->get('/nutrition-goal/options', [NutritionGoalController::class, 'options']);
Route::middleware('api.token')->post('/nutrition-goal', [NutritionGoalController::class, 'update']);
Route::middleware('api.token')->get('/xp-summary', [XpController::class, 'summary']);
Route::middleware('api.token')->get('/weekly-stats', [WeeklyStatsController::class, 'summary']);
Route::middleware('api.token')->get('/daily-challenge', [DailyChallengeController::class, 'today']);
Route::middleware('api.token')->get('/leaderboards', [LeaderboardController::class, 'index']);
Route::middleware('api.token')->post('/leaderboards', [LeaderboardController::class, 'store']);
Route::middleware('api.token')->post('/leaderboards/join', [LeaderboardController::class, 'join']);
Route::middleware('api.token')->get('/leaderboards/{id}', [LeaderboardController::class, 'show']);
Route::middleware('api.token')->delete('/activities/{id}', [ActivityController::class, 'destroy']);
Route::middleware('api.token')->delete('/food-logs/{id}', [FoodController::class, 'destroyLog']);

use Illuminate\Http\Request;

Route::middleware('api.token')->get('/me', function (Request $request) {
    return $request->get('auth_user');
});
