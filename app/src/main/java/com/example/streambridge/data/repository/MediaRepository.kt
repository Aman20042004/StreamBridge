package com.example.streambridge.data.repository



import com.example.streambridge.data.model.*
import com.example.streambridge.data.remote.media_api.MediaApi

import retrofit2.Response


class MediaRepository(
    private val api: MediaApi
) {

    private suspend fun <T : Any> safeCall(
        call: suspend () -> Response<T>
    ): Result<T> {
        return try {
            val response = call()

            if (response.isSuccessful) {
                val body = response.body()

                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(
                        Exception("Server returned an empty response")
                    )
                }
            } else {
                val errorMessage = response.errorBody()
                    ?.string()
                    ?.takeIf { it.isNotBlank() }
                    ?: "Request failed with HTTP ${response.code()}"

                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(
                Exception(
                    e.message ?: "Unable to connect to Media Service",
                    e
                )
            )
        }
    }

    suspend fun createMediaSession(
        request: CreateMediaSessionRequest
    ) = safeCall {
        api.createMediaSession(request)
    }

    suspend fun getMediaSession(
        sessionId: String
    ) = safeCall {
        api.getMediaSession(sessionId)
    }

    suspend fun startMediaSession(
        sessionId: String
    ) = safeCall {
        api.startMediaSession(sessionId)
    }

    suspend fun endMediaSession(
        sessionId: String
    ) = safeCall {
        api.endMediaSession(sessionId)
    }

    suspend fun registerParticipant(
        sessionId: String,
        request: RegisterMediaParticipantRequest
    ) = safeCall {
        api.registerParticipant(sessionId, request)
    }

    suspend fun getParticipants(
        sessionId: String
    ) = safeCall {
        api.getParticipants(sessionId)
    }

    suspend fun disconnectParticipant(
        sessionId: String,
        participantId: String
    ) = safeCall {
        api.disconnectParticipant(sessionId, participantId)
    }

    suspend fun updateAudio(
        sessionId: String,
        participantId: String,
        request: AudioRequest
    ) = safeCall {
        api.updateAudio(sessionId, participantId, request)
    }

    suspend fun updateVideo(
        sessionId: String,
        participantId: String,
        request: VideoRequest
    ) = safeCall {
        api.updateVideo(sessionId, participantId, request)
    }

    suspend fun updateSpotlight(
        sessionId: String,
        participantId: String,
        request: SpotlightRequest
    ) = safeCall {
        api.updateSpotlight(sessionId, participantId, request)
    }

    suspend fun updateLayout(
        sessionId: String,
        request: LayoutRequest
    ) = safeCall {
        api.updateLayout(sessionId, request)
    }

    suspend fun health(): Result<Map<String, Any>> {
        return safeCall {
            api.health()
        }
    }

    suspend fun getMediaState(
        sessionId: String
    ): Result<MediaStateResponseDto> {
        return safeCall {
            api.getMediaState(sessionId)
        }
    }
}