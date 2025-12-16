package com.example.gamifikalt_fitnessz_alkalmazas.network

import android.content.Context

object TokenStore {
    private const val PREFS = "app_prefs"
    private const val KEY = "api_token"

    fun save(context: Context, token: String) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY, token).apply()
    }

    fun get(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return prefs.getString(KEY, null)
    }

    fun clear(context: Context) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY).apply()
    }
}
