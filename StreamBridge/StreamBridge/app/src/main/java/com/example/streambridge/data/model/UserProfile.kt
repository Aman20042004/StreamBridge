package com.example.streambridge.data.model



data class UserProfile(
    val id: String?,
    val userId: String,
    val name: String,
    val avatar: String?,
    val bio: String?,
    val createdAt: String,
    val updatedAt: String
)
