package com.example.streambridge.data.remote.user_api



import com.example.streambridge.data.model.UserProfile
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserApi {

    @GET("api/users/{userId}")
    suspend fun getUserProfile(
        @Path("userId") userId: String
    ): Response<UserProfile>

    @POST("api/users")
    suspend fun createUserProfile(
        @Body request: CreateUserProfileRequest
    ): Response<UserProfile>

    @PUT("api/users/{userId}")
    suspend fun updateUserProfile(
        @Path("userId") userId: String,
        @Body request: UpdateUserProfileRequest
    ): Response<UserProfile>
}

data class CreateUserProfileRequest(
    val userId: String,
    val name: String,
    val avatar: String? = null,
    val bio: String? = null
)

data class UpdateUserProfileRequest(
    val name: String,
    val avatar: String? = null,
    val bio: String? = null
)