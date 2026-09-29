package com.example.streambridge.navigation


import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object Splash : NavKey

@Serializable
data object Login : NavKey

@Serializable
data object SignUp : NavKey

@Serializable
data object Home: NavKey

@Serializable
data object CreateSession : NavKey

@Serializable
data object SessionLobby : NavKey

@Serializable
data object HostLive : NavKey

@Serializable
data object ParticipantLive : NavKey

@Serializable
data object Participants : NavKey

@Serializable
data object Chat : NavKey

@Serializable
data object AskRequest : NavKey

@Serializable
data object AskRequests : NavKey

@Serializable
data object SessionSummary : NavKey

@Serializable
data object JoinSession : NavKey

@Serializable
data object Profile : NavKey

@Serializable
data object Settings : NavKey

@Serializable
data class SessionDetails(
    val sessionId: String
) : NavKey

@Serializable
data object SessionHistory : NavKey
