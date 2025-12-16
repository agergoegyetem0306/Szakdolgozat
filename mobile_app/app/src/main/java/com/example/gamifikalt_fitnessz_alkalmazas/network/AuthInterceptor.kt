package com.example.gamifikalt_fitnessz_alkalmazas.network

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val token = TokenStore.get(context)

        if (token.isNullOrBlank()) {
            return chain.proceed(original)
        }

        val newReq = original.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()

        return chain.proceed(newReq)
    }
}
