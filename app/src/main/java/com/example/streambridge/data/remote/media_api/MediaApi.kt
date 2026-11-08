package com.example.streambridge.data.remote.media_api

import com.example.streambridge.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface MediaApi {

    @GET("api/media/health")
    suspend fun health(): Response<Map<String, String>>

    @POST("api/media/sessions")
    suspend fun createMediaSession(
        @Body request: CreateMediaSessionRequest
    ): Response<MediaSessionDto>

    @GET("api/media/sessions/{sessionId}")
    suspend fun getMediaSession(
        @Path("sessionId") sessionId: String
    ): Response<MediaSessionDto>

    @POST("api/media/sessions/{sessionId}/start")
    suspend fun startMediaSession(
        @Path("sessionId") sessionId: String
    ): Response<OperationResultDto>

    @POST("api/media/sessions/{sessionId}/end")
    suspend fun endMediaSession(
        @Path("sessionId") sessionId: String
    ): Response<OperationResultDto>

    @POST("api/media/sessions/{sessionId}/participants")
    suspend fun registerParticipant(
        @Path("sessionId") sessionId: String,
        @Body request: RegisterMediaParticipantRequest
    ): Response<MediaParticipantDto>

    @GET("api/media/sessions/{sessionId}/participants")
    suspend fun getParticipants(
        @Path("sessionId") sessionId: String
    ): Response<List<MediaParticipantDto>>

    @GET("api/media/participants/{participantId}")
    suspend fun getParticipant(
        @Path("participantId") participantId: String
    ): Response<MediaParticipantDto>

    @POST("api/media/sessions/{sessionId}/participants/{participantId}/disconnect")
    suspend fun disconnectParticipant(
        @Path("sessionId") sessionId: String,
        @Path("participantId") participantId: String
    ): Response<OperationResultDto>

    @PATCH("api/media/sessions/{sessionId}/participants/{participantId}/audio")
    suspend fun updateAudio(
        @Path("sessionId") sessionId: String,
        @Path("participantId") participantId: String,
        @Body request: AudioRequest
    ): Response<OperationResultDto>

    @PATCH("api/media/sessions/{sessionId}/participants/{participantId}/video")
    suspend fun updateVideo(
        @Path("sessionId") sessionId: String,
        @Path("participantId") participantId: String,
        @Body request: VideoRequest
    ): Response<OperationResultDto>

    @POST("api/media/sessions/{sessionId}/participants/{participantId}/spotlight")
    suspend fun updateSpotlight(
        @Path("sessionId") sessionId: String,
        @Path("participantId") participantId: String,
        @Body request: SpotlightRequest
    ): Response<OperationResultDto>

    @PATCH("api/media/sessions/{sessionId}/layout")
    suspend fun updateLayout(
        @Path("sessionId") sessionId: String,
        @Body request: LayoutRequest
    ): Response<OperationResultDto>

    @GET("api/media/sessions/{sessionId}/state")
    suspend fun getMediaState(
        @Path("sessionId") sessionId: String
    ): Response<MediaStateResponseDto>

    @POST("api/media/sessions/{sessionId}/token")
    suspend fun generateLiveKitToken(
        @Path("sessionId") sessionId: String,
        @Body request: LiveKitTokenRequest
    ): Response<LiveKitTokenResponse>
}