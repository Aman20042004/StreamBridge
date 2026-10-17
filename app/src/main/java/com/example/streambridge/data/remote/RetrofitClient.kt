package com.example.streambridge.data.remote




import com.example.streambridge.data.remote.participant_api.ParticipantApi
import com.example.streambridge.data.remote.session_api.SessionApi
import com.example.streambridge.data.remote.user_api.UserApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val AUTH_BASE_URL =
        "http://10.16.153.197:8081/"

    private const val USER_BASE_URL =
        "http://10.16.153.197:8090/"

    private const val SESSION_BASE_URL =
        "http://10.16.153.197:8086/"

    private const val PARTICIPANT_BASE_URL =
        "http://10.16.153.197:8083/"

    val authApi: AuthApi by lazy {
        Retrofit.Builder()
            .baseUrl(AUTH_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApi::class.java)
    }

    val userApi: UserApi by lazy {
        Retrofit.Builder()
            .baseUrl(USER_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UserApi::class.java)
    }

    val sessionApi: SessionApi by lazy {
        Retrofit.Builder()
            .baseUrl(SESSION_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SessionApi::class.java)
    }

    val participantApi: ParticipantApi by lazy {
        Retrofit.Builder()
            .baseUrl(PARTICIPANT_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ParticipantApi::class.java)
    }
}