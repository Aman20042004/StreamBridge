package com.example.streambridge.data.repository



import com.example.streambridge.data.model.Session

import com.example.streambridge.data.remote.RetrofitClient

import com.example.streambridge.data.remote.session_api.CreateSessionRequest
import com.example.streambridge.data.remote.session_api.UpdateSessionRequest

class SessionRepository {

    private val sessionApi = RetrofitClient.sessionApi

    suspend fun createSession(
        request: CreateSessionRequest
    ): Result<Session> {
        return try {
            val response = sessionApi.createSession(request)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(
                    Exception("Failed to create session: ${response.code()}")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSession(
        sessionId: String
    ): Result<Session> {
        return try {
            val response = sessionApi.getSession(sessionId)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(
                    Exception("Failed to fetch session: ${response.code()}")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSessionsByOwner(
        ownerId: String
    ): Result<List<Session>> {
        return try {
            val response = sessionApi.getSessionsByOwner(ownerId)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(
                    Exception("Failed to fetch sessions: ${response.code()}")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateSession(
        sessionId: String,
        request: UpdateSessionRequest
    ): Result<Session> {
        return try {
            val response = sessionApi.updateSession(
                sessionId,
                request
            )

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(
                    Exception("Failed to update session: ${response.code()}")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun startSession(
        sessionId: String,
        ownerId: String
    ): Result<Session> {
        return try {
            val response = sessionApi.startSession(
                sessionId,
                ownerId
            )

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(
                    Exception("Failed to start session: ${response.code()}")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun endSession(
        sessionId: String,
        ownerId: String
    ): Result<Session> {
        return try {
            val response = sessionApi.endSession(
                sessionId,
                ownerId
            )

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(
                    Exception("Failed to end session: ${response.code()}")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

