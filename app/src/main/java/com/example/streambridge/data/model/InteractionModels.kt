package com.example.streambridge.data.model

data class CreateCommentRequest(
    val sessionId: String,
    val userId: String,
    val message: String
)

data class DeleteCommentRequest(
    val userId: String
)

data class CommentDto(
    val id: String,
    val sessionId: String,
    val userId: String,
    val message: String,
    val createdAt: String?,
    val updatedAt: String?
)

data class CreateAskRequest(
    val sessionId: String,
    val userId: String,
    val message: String? = null
)

data class AskDecisionRequest(
    val ownerId: String
)

data class AskDto(
    val id: String,
    val sessionId: String,
    val userId: String,
    val message: String?,
    val status: String,
    val createdAt: String?,
    val updatedAt: String?
)