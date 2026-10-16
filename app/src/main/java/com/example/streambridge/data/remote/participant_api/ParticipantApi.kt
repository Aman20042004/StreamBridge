package com.example.streambridge.data.remote.participant_api


import com.example.streambridge.data.model.Session
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ParticipantApi {

    @POST("api/participants/join")
    suspend fun joinSession(
        @Body request: JoinParticipantRequest
    ): Response<Participant>

    @GET("api/participants/session/{sessionId}")
    suspend fun getParticipantsBySession(
        @Path("sessionId") sessionId: String
    ): Response<List<Participant>>

    @GET("api/participants/{participantId}")
    suspend fun getParticipant(
        @Path("participantId") participantId: String
    ): Response<Participant>

    @POST("api/participants/{participantId}/leave")
    suspend fun leaveSession(
        @Path("participantId") participantId: String
    ): Response<Participant>

    @POST("api/participants/{participantId}/remove")
    suspend fun removeParticipant(
        @Path("participantId") participantId: String,
        @Body request: RemoveParticipantRequest
    ): Response<Participant>

    @POST("api/participants/{participantId}/mute")
    suspend fun updateMute(
        @Path("participantId") participantId: String,
        @Body request: MuteParticipantRequest
    ): Response<Participant>

    @POST("api/participants/{participantId}/video")
    suspend fun updateVideo(
        @Path("participantId") participantId: String,
        @Body request: VideoParticipantRequest
    ): Response<Participant>

    @GET("api/participants/health")
    suspend fun health(): Response<Map<String, String>>
}

data class JoinParticipantRequest(
    val sessionId: String,
    val userId: String
)

data class RemoveParticipantRequest(
    val ownerId: String
)

data class MuteParticipantRequest(
    val muted: Boolean
)

data class VideoParticipantRequest(
    val videoEnabled: Boolean
)

data class Participant(
    val id: String? = null,
    val sessionId: String,
    val userId: String,
    val role: String,
    val status: String,
    val videoEnabled: Boolean,
    val muted: Boolean,
    val joinedAt: String? = null,
    val leftAt: String? = null
)