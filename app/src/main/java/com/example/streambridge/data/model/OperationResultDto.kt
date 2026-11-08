package com.example.streambridge.data.model



data class OperationResultDto(
    val success: Boolean,
    val message: String,
    val mockMode: Boolean,
    val provider: String
)