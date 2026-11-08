package com.example.streambridge.data.repository

import com.example.streambridge.data.remote.RetrofitClient
import com.example.streambridge.data.remote.participant_api.JoinParticipantRequest
import com.example.streambridge.data.remote.participant_api.MuteParticipantRequest
import com.example.streambridge.data.remote.participant_api.Participant
import com.example.streambridge.data.remote.participant_api.RemoveParticipantRequest
import com.example.streambridge.data.remote.participant_api.VideoParticipantRequest

class ParticipantRepository {

    private val participantApi = RetrofitClient.participantApi

    suspend fun joinSession(
        request: JoinParticipantRequest
    ): Result<Participant> {
        return try {
            val response = participantApi.joinSession(request)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(
                    Exception(
                        "Failed to join session: HTTP ${response.code()} - " +
                                (response.errorBody()?.string() ?: "Unknown error")
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getParticipantsBySession(
        sessionId: String
    ): Result<List<Participant>> {
        return try {
            val response = participantApi.getParticipantsBySession(sessionId)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(
                    Exception(
                        "Failed to fetch participants: HTTP ${response.code()} - " +
                                (response.errorBody()?.string() ?: "Unknown error")
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getParticipant(
        participantId: String
    ): Result<Participant> {
        return try {
            val response = participantApi.getParticipant(participantId)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(
                    Exception(
                        "Failed to fetch participant: HTTP ${response.code()} - " +
                                (response.errorBody()?.string() ?: "Unknown error")
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun leaveSession(
        participantId: String
    ): Result<Participant> {
        return try {
            val response = participantApi.leaveSession(participantId)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(
                    Exception(
                        "Failed to leave session: HTTP ${response.code()} - " +
                                (response.errorBody()?.string() ?: "Unknown error")
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeParticipant(
        participantId: String,
        ownerId: String
    ): Result<Participant> {
        return try {
            val response = participantApi.removeParticipant(
                participantId,
                RemoveParticipantRequest(ownerId)
            )

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(
                    Exception(
                        "Failed to remove participant: HTTP ${response.code()} - " +
                                (response.errorBody()?.string() ?: "Unknown error")
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateMute(
        participantId: String,
        muted: Boolean
    ): Result<Participant> {
        return try {
            val response = participantApi.updateMute(
                participantId,
                MuteParticipantRequest(muted)
            )

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(
                    Exception(
                        "Failed to update mute: HTTP ${response.code()} - " +
                                (response.errorBody()?.string() ?: "Unknown error")
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateVideo(
        participantId: String,
        videoEnabled: Boolean
    ): Result<Participant> {
        return try {
            val response = participantApi.updateVideo(
                participantId,
                VideoParticipantRequest(videoEnabled)
            )

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(
                    Exception(
                        "Failed to update video: HTTP ${response.code()} - " +
                                (response.errorBody()?.string() ?: "Unknown error")
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun health(): Result<Map<String, String>> {
        return try {
            val response = participantApi.health()

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(
                    Exception("Participant Service health failed: HTTP ${response.code()}")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}