package com.example.streambridge.data.model



data class CreateMediaSessionRequest(
    val sessionId: String,
    val layoutMode: String = "GRID"
)

data class RegisterMediaParticipantRequest(
    val participantId: String,
    val userId: String
)

data class AudioRequest(
    val muted: Boolean
)

data class VideoRequest(
    val videoEnabled: Boolean
)

data class SpotlightRequest(
    val spotlighted: Boolean
)

data class LayoutRequest(
    val layoutMode: String
)

data class MediaSessionDto(
    val id: String? = null,
    val sessionId: String,
    val status: String,
    val provider: String? = null,
    val layoutMode: String,
    val activeSpeakerUserId: String? = null,
    val createdAt: String? = null,
    val startedAt: String? = null,
    val endedAt: String? = null,
    val updatedAt: String? = null
)

data class MediaParticipantDto(
    val id: String? = null,
    val sessionId: String,
    val participantId: String,
    val userId: String,
    val status: String,
    val audioEnabled: Boolean = true,
    val videoEnabled: Boolean = true,
    val muted: Boolean = false,
    val spotlighted: Boolean = false,
    val joinedAt: String? = null,
    val leftAt: String? = null,
    val updatedAt: String? = null
)

data class ProviderInfoDto(
    val name: String,
    val mode: String,
    val available: Boolean,
    val capabilities: Set<String>,
    val note: String
)

data class MediaStateResponseDto(
    val mediaSession: MediaSessionDto,
    val participants: List<MediaParticipantDto>,
    val provider: ProviderInfoDto
)