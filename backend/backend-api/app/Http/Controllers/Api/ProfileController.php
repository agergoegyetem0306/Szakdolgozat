<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;

class ProfileController extends Controller
{
    public function show(Request $request)
    {
        $user = $request->get('auth_user');

        return response()->json($user);
    }

    public function update(Request $request)
    {
        $user = $request->get('auth_user');

        $validated = $request->validate([
            'gender' => 'nullable|string|max:20',
            'age' => 'nullable|integer|min:1|max:120',
            'height' => 'nullable|integer|min:50|max:250',
            'weight' => 'nullable|numeric|min:20|max:500',
        ]);

        $user->update($validated);

        return response()->json([
            'message' => 'Profile updated successfully',
            'user' => $user,
        ]);
    }
}
