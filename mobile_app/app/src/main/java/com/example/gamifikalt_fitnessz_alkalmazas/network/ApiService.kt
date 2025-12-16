package com.example.gamifikalt_fitnessz_alkalmazas.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class RegisterRequest(val name: String, val email: String, val password: String)
data class LoginRequest(val email: String, val password: String)

data class UserDto(val id: Int, val name: String, val email: String)
data class AuthResponse(val user: UserDto, val token: String)

interface ApiService {
    @POST("register")
    fun register(@Body req: RegisterRequest): Call<AuthResponse>

    @POST("login")
    fun login(@Body req: LoginRequest): Call<AuthResponse>

    @GET("me")
    fun me(): Call<UserDto>
}
