package com.example.streambridge.data.remote



data class AuthResponse(
    val userId: String,
    val email: String,
    val role: String,
    val message: String
)