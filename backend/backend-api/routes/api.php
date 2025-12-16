<?php

use Illuminate\Support\Facades\Route;
use App\Http\Controllers\Api\AuthController;

Route::post('/register', [AuthController::class, 'register']);
Route::post('/login', [AuthController::class, 'login']);
use Illuminate\Http\Request;

Route::middleware('api.token')->get('/me', function (Request $request) {
    return $request->get('auth_user');
});
