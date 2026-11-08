package com.example.streambridge.data.repository


import com.example.streambridge.data.model.UserProfile

import com.example.streambridge.data.remote.RetrofitClient
import com.example.streambridge.data.remote.user_api.CreateUserProfileRequest
import com.example.streambridge.data.remote.user_api.UpdateUserProfileRequest


class UserRepository {

    private val userApi = RetrofitClient.userApi

    suspend fun getUserProfile(
        userId: String
    ): Result<UserProfile> {
        return try {
            val response = userApi.getUserProfile(userId)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(
                    Exception(
                        "Failed to fetch profile: ${response.code()}"
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createUserProfile(
        request: CreateUserProfileRequest
    ): Result<UserProfile> {
        return try {
            val response = userApi.createUserProfile(request)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(
                    Exception(
                        "Failed to create profile: ${response.code()}"
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUserProfile(
        userId: String,
        request: UpdateUserProfileRequest
    ): Result<UserProfile> {
        return try {
            val response = userApi.updateUserProfile(
                userId,
                request
            )

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(
                    Exception(
                        "Failed to update profile: ${response.code()}"
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}