package com.example.streambridge.data.repository

import com.example.streambridge.data.model.AskDecisionRequest
import com.example.streambridge.data.model.AskDto
import com.example.streambridge.data.model.CommentDto
import com.example.streambridge.data.model.CreateAskRequest
import com.example.streambridge.data.model.CreateCommentRequest
import com.example.streambridge.data.model.DeleteCommentRequest
import com.example.streambridge.data.remote.RetrofitClient
import com.example.streambridge.data.remote.interaction_api.InteractionApi

import retrofit2.Response

class InteractionRepository {

    private val api = RetrofitClient.interactionApi

    suspend fun createComment(
        request: CreateCommentRequest
    ): Result<CommentDto> = safeCall {
        api.createComment(request)
    }

    suspend fun getComments(
        sessionId: String
    ): Result<List<CommentDto>> = safeCall {
        api.getCommentsBySession(sessionId)
    }

    suspend fun deleteComment(
        commentId: String,
        userId: String
    ): Result<Unit> = safeCall {
        api.deleteComment(
            commentId = commentId,
            request = DeleteCommentRequest(userId)
        )
    }

    suspend fun createAsk(
        request: CreateAskRequest
    ): Result<AskDto> = safeCall {
        api.createAskRequest(request)
    }

    suspend fun getAskRequests(
        sessionId: String,
        status: String? = null
    ): Result<List<AskDto>> = safeCall {
        api.getAskRequestsBySession(sessionId, status)
    }

    suspend fun acceptAsk(
        askId: String,
        ownerId: String
    ): Result<AskDto> = safeCall {
        api.acceptAskRequest(
            askId,
            AskDecisionRequest(ownerId)
        )
    }

    suspend fun rejectAsk(
        askId: String,
        ownerId: String
    ): Result<AskDto> = safeCall {
        api.rejectAskRequest(
            askId,
            AskDecisionRequest(ownerId)
        )
    }

    private suspend fun <T> safeCall(
        call: suspend () -> retrofit2.Response<T>
    ): Result<T> {
        return try {
            val response = call()

            if (response.isSuccessful) {
                val body = response.body()

                if (body != null) {
                    Result.success(body)
                } else {
                    @Suppress("UNCHECKED_CAST")
                    if (response.code() == 204) {
                        Result.success(Unit as T)
                    } else {
                        Result.failure(
                            Exception("Empty response: HTTP ${response.code()}")
                        )
                    }
                }
            } else {
                val errorMessage = when (response.code()) {
                    403 -> "You are not allowed to perform this action."
                    404 -> "The requested item was not found."
                    400 -> "Invalid request. Please check your input."
                    else -> "Request failed: HTTP ${response.code()}"
                }

                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(
                Exception(
                    e.message ?: "Network error. Please try again.",
                    e
                )
            )
        }
    }
}

//private fun InteractionApi.createComment(request: CreateCommentRequest): Response<T> {}

