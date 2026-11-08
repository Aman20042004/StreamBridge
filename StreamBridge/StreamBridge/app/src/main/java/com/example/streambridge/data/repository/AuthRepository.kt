package com.example.streambridge.data.repository




import com.example.streambridge.data.remote.AuthApi
import com.example.streambridge.data.remote.AuthRequest
import com.example.streambridge.data.remote.AuthResponse

class AuthRepository(
    private val authApi: AuthApi
) {

    suspend fun login(
        email: String,
        password: String
    ): AuthResponse {

        return authApi.login(
            AuthRequest(
                email = email,
                password = password
            )
        )
    }

    suspend fun register(
        email: String,
        password: String
    ): AuthResponse {

        return authApi.register(
            AuthRequest(
                email = email,
                password = password
            )
        )
    }
}