package com.example.streambridge.data.model

data class LiveKitTokenRequest(
    val participantId: String,
    val userId: String
)

data class LiveKitTokenResponse(
    val serverUrl: String,
    val roomName: String,
    val participantId: String,
    val token: String
)