package com.example.streambridge.data.remote


import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {

    @GET("api/auth/health")
    suspend fun health(): HealthResponse

    @POST("api/auth/login")
    suspend fun login(
        @Body request: AuthRequest
    ): AuthResponse

    @POST("api/auth/register")
    suspend fun register(
        @Body request: AuthRequest
    ): AuthResponse
}

data class HealthResponse(
    val status: String,
    val service: String
)