package com.example.streambridge.data.remote.interaction_api

import com.example.streambridge.data.model.AskDecisionRequest
import com.example.streambridge.data.model.AskDto
import com.example.streambridge.data.model.CommentDto
import com.example.streambridge.data.model.CreateAskRequest
import com.example.streambridge.data.model.CreateCommentRequest
import com.example.streambridge.data.model.DeleteCommentRequest
import retrofit2.Response
import retrofit2.http.Body

import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface InteractionApi {

    @GET("api/interactions/health")
    suspend fun health(): Response<Any>

    // Comments

    @POST("api/interactions/comments")
    suspend fun createComment(
        @Body request: CreateCommentRequest
    ): Response<CommentDto>

    @GET("api/interactions/comments/session/{sessionId}")
    suspend fun getCommentsBySession(
        @Path("sessionId") sessionId: String
    ): Response<List<CommentDto>>

    @GET("api/interactions/comments/{commentId}")
    suspend fun getComment(
        @Path("commentId") commentId: String
    ): Response<CommentDto>

    @HTTP(
        method = "DELETE",
        path = "api/interactions/comments/{commentId}",
        hasBody = true
    )
    suspend fun deleteComment(
        @Path("commentId") commentId: String,
        @Body request: DeleteCommentRequest
    ): Response<Unit>

    // ASK requests

    @POST("api/interactions/ask")
    suspend fun createAskRequest(
        @Body request: CreateAskRequest
    ): Response<AskDto>

    @GET("api/interactions/ask/{askId}")
    suspend fun getAskRequest(
        @Path("askId") askId: String
    ): Response<AskDto>

    @GET("api/interactions/ask/session/{sessionId}")
    suspend fun getAskRequestsBySession(
        @Path("sessionId") sessionId: String,
        @Query("status") status: String? = null
    ): Response<List<AskDto>>

    @POST("api/interactions/ask/{askId}/accept")
    suspend fun acceptAskRequest(
        @Path("askId") askId: String,
        @Body request: AskDecisionRequest
    ): Response<AskDto>

    @POST("api/interactions/ask/{askId}/reject")
    suspend fun rejectAskRequest(
        @Path("askId") askId: String,
        @Body request: AskDecisionRequest
    ): Response<AskDto>
}