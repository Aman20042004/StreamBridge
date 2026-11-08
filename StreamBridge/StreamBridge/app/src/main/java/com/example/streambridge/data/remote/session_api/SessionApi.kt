package com.example.streambridge.data.remote.session_api



import com.example.streambridge.data.model.Session
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface SessionApi {

    @POST("api/sessions")
    suspend fun createSession(
        @Body request: CreateSessionRequest
    ): Response<Session>

    @GET("api/sessions/{sessionId}")
    suspend fun getSession(
        @Path("sessionId") sessionId: String
    ): Response<Session>

    @GET("api/sessions/owner/{ownerId}")
    suspend fun getSessionsByOwner(
        @Path("ownerId") ownerId: String
    ): Response<List<Session>>

    @PUT("api/sessions/{sessionId}")
    suspend fun updateSession(
        @Path("sessionId") sessionId: String,
        @Body request: UpdateSessionRequest
    ): Response<Session>

    @POST("api/sessions/{sessionId}/start")
    suspend fun startSession(
        @Path("sessionId") sessionId: String,
        @Query("ownerId") ownerId: String
    ): Response<Session>

    @POST("api/sessions/{sessionId}/end")
    suspend fun endSession(
        @Path("sessionId") sessionId: String,
        @Query("ownerId") ownerId: String
    ): Response<Session>

    @GET("api/sessions/health")
    suspend fun health(): Response<Map<String, String>>
}

data class CreateSessionRequest(
    val ownerId: String,
    val title: String,
    val description: String? = null,
    val accessMode: String
)

data class UpdateSessionRequest(
    val title: String,
    val description: String? = null,
    val accessMode: String
)