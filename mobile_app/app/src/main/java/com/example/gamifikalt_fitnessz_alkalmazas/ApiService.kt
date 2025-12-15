package com.example.gamifikalt_fitnessz_alkalmazas

import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("posts/{id}")
    suspend fun getPost(@Path("id") id: Int): Response<Post>

    @GET("posts")
    suspend fun getPosts(): Response<List<Post>>

    @POST("posts")
    suspend fun createPost(@Body post: Post): Response<Post>

    @PUT("posts/{id}")
    suspend fun updatePost(@Path("id") id: Int, @Body post: Post): Response<Post>

    @DELETE("posts/{id}")
    suspend fun deletePost(@Path("id") id: Int): Response<Unit>

    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>
}

data class Post(
    val id: Int,
    val userId: Int,
    val title: String,
    val body: String
)

data class RegisterRequest(
    val email: String,
    val password: String
)

data class RegisterResponse(
    val success: Boolean,
    val message: String
)
