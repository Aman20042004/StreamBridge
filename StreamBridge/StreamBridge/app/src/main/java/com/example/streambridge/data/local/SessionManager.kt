package com.example.streambridge.data.local



import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(
    name = "user_session"
)

class SessionManager(
    private val context: Context
) {

    companion object {
        private val USER_ID =
            stringPreferencesKey("user_id")

        private val EMAIL =
            stringPreferencesKey("email")

        private val ROLE =
            stringPreferencesKey("role")
    }

    suspend fun saveSession(
        userId: String,
        email: String,
        role: String
    ) {

        context.dataStore.edit { preferences ->

            preferences[USER_ID] = userId
            preferences[EMAIL] = email
            preferences[ROLE] = role
        }
    }

    val session: Flow<UserSession?> =
        context.dataStore.data.map { preferences ->

            val userId = preferences[USER_ID]
            val email = preferences[EMAIL]
            val role = preferences[ROLE]

            if (
                userId != null &&
                email != null &&
                role != null
            ) {
                UserSession(
                    userId = userId,
                    email = email,
                    role = role
                )
            } else {
                null
            }
        }

    suspend fun clearSession() {

        context.dataStore.edit {
            it.clear()
        }
    }
}