package com.example.streambridge.data.model



data class Session(
    val id: String?,
    val ownerId: String,
    val title: String,
    val description: String?,
    val accessMode: String,
    val status: String,
    val createdAt: String?,
    val startedAt: String?,
    val endedAt: String?
)

