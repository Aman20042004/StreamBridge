package com.example.streambridge.data.repository

import com.example.streambridge.data.model.*
import com.example.streambridge.data.remote.RetrofitClient
import com.example.streambridge.data.remote.media_api.MediaApi
import retrofit2.Response

class MediaRepository(mediaApi: MediaApi) {

    private fun <T> handleResponse(response: Response<T>): Result<T> {
        return try {
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(
                        Exception("Media Service returned an empty response")
                    )
                }
            } else {
                Result.failure(
                    Exception(
                        "HTTP ${response.code()} - " +
                                (response.errorBody()?.string() ?: "Unknown error")
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun health(): Result<Map<String, String>> =
        try {
            handleResponse(RetrofitClient.mediaApi.health())
        } catch (e: Exception) {
            Result.failure(e)
        }

    suspend fun createMediaSession(
        request: CreateMediaSessionRequest
    ): Result<MediaSessionDto> =
        try {
            handleResponse(RetrofitClient.mediaApi.createMediaSession(request))
        } catch (e: Exception) {
            Result.failure(e)
        }

    suspend fun getMediaSession(
        sessionId: String
    ): Result<MediaSessionDto> =
        try {
            handleResponse(RetrofitClient.mediaApi.getMediaSession(sessionId))
        } catch (e: Exception) {
            Result.failure(e)
        }

    suspend fun startMediaSession(
        sessionId: String
    ): Result<OperationResultDto> =
        try {
            handleResponse(RetrofitClient.mediaApi.startMediaSession(sessionId))
        } catch (e: Exception) {
            Result.failure(e)
        }

    suspend fun endMediaSession(
        sessionId: String
    ): Result<OperationResultDto> =
        try {
            handleResponse(RetrofitClient.mediaApi.endMediaSession(sessionId))
        } catch (e: Exception) {
            Result.failure(e)
        }

    suspend fun registerParticipant(
        sessionId: String,
        request: RegisterMediaParticipantRequest
    ): Result<MediaParticipantDto> =
        try {
            handleResponse(
                RetrofitClient.mediaApi.registerParticipant(sessionId, request)
            )
        } catch (e: Exception) {
            Result.failure(e)
        }

    suspend fun getParticipants(
        sessionId: String
    ): Result<List<MediaParticipantDto>> =
        try {
            handleResponse(RetrofitClient.mediaApi.getParticipants(sessionId))
        } catch (e: Exception) {
            Result.failure(e)
        }

    suspend fun getParticipant(
        participantId: String
    ): Result<MediaParticipantDto> =
        try {
            handleResponse(RetrofitClient.mediaApi.getParticipant(participantId))
        } catch (e: Exception) {
            Result.failure(e)
        }

    suspend fun disconnectParticipant(
        sessionId: String,
        participantId: String
    ): Result<OperationResultDto> =
        try {
            handleResponse(
                RetrofitClient.mediaApi.disconnectParticipant(
                    sessionId,
                    participantId
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }

    suspend fun updateAudio(
        sessionId: String,
        participantId: String,
        request: AudioRequest
    ): Result<OperationResultDto> =
        try {
            handleResponse(
                RetrofitClient.mediaApi.updateAudio(
                    sessionId,
                    participantId,
                    request
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }

    suspend fun updateVideo(
        sessionId: String,
        participantId: String,
        request: VideoRequest
    ): Result<OperationResultDto> =
        try {
            handleResponse(
                RetrofitClient.mediaApi.updateVideo(
                    sessionId,
                    participantId,
                    request
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }

    suspend fun updateSpotlight(
        sessionId: String,
        participantId: String,
        request: SpotlightRequest
    ): Result<OperationResultDto> =
        try {
            handleResponse(
                RetrofitClient.mediaApi.updateSpotlight(
                    sessionId,
                    participantId,
                    request
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }

    suspend fun updateLayout(
        sessionId: String,
        request: LayoutRequest
    ): Result<OperationResultDto> =
        try {
            handleResponse(
                RetrofitClient.mediaApi.updateLayout(sessionId, request)
            )
        } catch (e: Exception) {
            Result.failure(e)
        }

    suspend fun getMediaState(
        sessionId: String
    ): Result<MediaStateResponseDto> =
        try {
            handleResponse(RetrofitClient.mediaApi.getMediaState(sessionId))
        } catch (e: Exception) {
            Result.failure(e)
        }

    suspend fun generateLiveKitToken(
        sessionId: String,
        participantId: String,
        userId: String
    ): Result<LiveKitTokenResponse> =
        try {
            handleResponse(
                RetrofitClient.mediaApi.generateLiveKitToken(
                    sessionId,
                    LiveKitTokenRequest(
                        participantId = participantId,
                        userId = userId
                    )
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
}