package com.example.streambridge.data.remote




import com.example.streambridge.data.remote.interaction_api.InteractionApi
import com.example.streambridge.data.remote.media_api.MediaApi
import com.example.streambridge.data.remote.participant_api.ParticipantApi
import com.example.streambridge.data.remote.session_api.SessionApi
import com.example.streambridge.data.remote.user_api.UserApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val LAPTOP_IP = "YOUR_LAPTOP_IP"

    private const val AUTH_BASE_URL =
        "http://10.128.147.197:8081/"

    private const val USER_BASE_URL =
        "http://10.128.147.197:8090/"

    private const val SESSION_BASE_URL =
        "http://10.128.147.197:8086/"

    private const val PARTICIPANT_BASE_URL =
        "http://10.128.147.197:8083/"

    private const val INTERACTION_BASE_URL =
        "http://10.128.147.197:9094/"

    private const val MEDIA_BASE_URL =
        "http://10.128.147.197:9095/"

    private fun createRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
    }

    val authApi: AuthApi by lazy {
        createRetrofit(AUTH_BASE_URL)
            .create(AuthApi::class.java)
    }

    val userApi: UserApi by lazy {
        createRetrofit(USER_BASE_URL)
            .create(UserApi::class.java)
    }

    val sessionApi: SessionApi by lazy {
        createRetrofit(SESSION_BASE_URL)
            .create(SessionApi::class.java)
    }

    val participantApi: ParticipantApi by lazy {
        createRetrofit(PARTICIPANT_BASE_URL)
            .create(ParticipantApi::class.java)
    }

    val interactionApi: InteractionApi by lazy {
        createRetrofit(INTERACTION_BASE_URL)
            .create(InteractionApi::class.java)
    }

    val mediaApi: MediaApi by lazy {
        createRetrofit(MEDIA_BASE_URL)
            .create(MediaApi::class.java)
    }
}