package com.example.streambridge.navigation


import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

// =====================================================
// 1. AUTHENTICATION
// =====================================================

@Serializable
data object Splash : NavKey

@Serializable
data object Login : NavKey

@Serializable
data object SignUp : NavKey


// =====================================================
// 2. HOME & PROFILE
// =====================================================

@Serializable
data object Home : NavKey

@Serializable
data object Profile : NavKey

@Serializable
data object Settings : NavKey


// =====================================================
// 3. SESSION MANAGEMENT
// =====================================================

@Serializable
data object CreateSession : NavKey

@Serializable
data object JoinSession : NavKey

@Serializable
data class SessionDetails(
    val sessionId: String
) : NavKey

@Serializable
data class SessionLobby(
    val sessionId: String
) : NavKey

@Serializable
data object SessionHistory : NavKey

@Serializable
data class SessionSummary(
    val sessionId: String
) : NavKey


// =====================================================
// 4. LIVE SESSION & PARTICIPANTS
// =====================================================

@Serializable
data class HostLive(
    val sessionId: String
) : NavKey

@Serializable
data class ParticipantLive(
    val sessionId: String,
    val participantId: String
) : NavKey

@Serializable
data class Participants(
    val sessionId: String
) : NavKey


// =====================================================
// 5. INTERACTION SERVICE — COMMENTS / LIVE CHAT
// =====================================================

@Serializable
data class Chat(
    val sessionId: String
) : NavKey


// =====================================================
// 6. INTERACTION SERVICE — AUDIENCE ASK REQUEST
// =====================================================

@Serializable
data class AskRequest(
    val sessionId: String
) : NavKey


// =====================================================
// 7. INTERACTION SERVICE — HOST ASK MANAGEMENT
// =====================================================

@Serializable
data class AskRequests(
    val sessionId: String
) : NavKey