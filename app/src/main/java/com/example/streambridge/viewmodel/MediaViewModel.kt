package com.example.streambridge.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.streambridge.data.model.*
import com.example.streambridge.data.realtime.LiveKitManager
import com.example.streambridge.data.repository.MediaRepository
import io.livekit.android.room.track.VideoTrack
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MediaViewModel(
    private val repository: MediaRepository,
    val liveKitManager: LiveKitManager
) : ViewModel() {

    private val _mediaSession = MutableStateFlow<MediaSessionDto?>(null)
    val mediaSession: StateFlow<MediaSessionDto?> = _mediaSession.asStateFlow()

    private val _liveKitToken = MutableStateFlow<LiveKitTokenResponse?>(null)
    val liveKitToken: StateFlow<LiveKitTokenResponse?> = _liveKitToken.asStateFlow()

    private val _isLiveKitConnected = MutableStateFlow(false)
    val isLiveKitConnected: StateFlow<Boolean> = _isLiveKitConnected.asStateFlow()

    private val _participants = MutableStateFlow<List<MediaParticipantDto>>(emptyList())
    val participants: StateFlow<List<MediaParticipantDto>> = _participants.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    private val _healthStatus = MutableStateFlow("Not checked")
    val healthStatus: StateFlow<String> = _healthStatus.asStateFlow()

    // NAYA STATE: Offline camera track ke UI update ke liye
    private val _localVideoTrack = MutableStateFlow<VideoTrack?>(null)
    val localVideoTrack: StateFlow<VideoTrack?> = _localVideoTrack.asStateFlow()

    fun checkHealth() {
        viewModelScope.launch {
            _healthStatus.value = "Checking..."
            repository.health().fold(
                onSuccess = { response ->
                    _healthStatus.value = response["status"]?.toString() ?: "Connected"
                },
                onFailure = { error ->
                    _healthStatus.value = "Failed: ${error.message ?: "Connection error"}"
                }
            )
        }
    }

    fun loadMediaSession(sessionId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.getMediaSession(sessionId)
                .onSuccess { _mediaSession.value = it }
                .onFailure { _error.value = it.message }

            _isLoading.value = false
        }
    }

    fun createMediaSession(
        sessionId: String,
        layoutMode: String = "GRID"
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _message.value = null

            repository.createMediaSession(
                CreateMediaSessionRequest(sessionId, layoutMode)
            ).onSuccess {
                _mediaSession.value = it
                _message.value = "Media session created"
            }.onFailure {
                _error.value = it.message
            }

            _isLoading.value = false
        }
    }

    fun startMediaSession(sessionId: String) {
        performSessionAction("Starting media session") {
            repository.startMediaSession(sessionId)
                .onSuccess {
                    _message.value = it.message
                    loadMediaSession(sessionId)
                }
                .onFailure { _error.value = it.message }
        }
    }

    fun endMediaSession(sessionId: String) {
        performSessionAction("Ending media session") {
            repository.endMediaSession(sessionId)
                .onSuccess {
                    _message.value = it.message
                    loadMediaSession(sessionId)
                }
                .onFailure { _error.value = it.message }
        }
    }

    fun updateAudio(sessionId: String, participantId: String, muted: Boolean) {
        viewModelScope.launch {
            _error.value = null
            repository.updateAudio(sessionId, participantId, AudioRequest(muted))
                .onSuccess {
                    _message.value = it.message
                    loadParticipants(sessionId)
                }
                .onFailure { _error.value = it.message }
        }
    }

    fun updateVideo(sessionId: String, participantId: String, videoEnabled: Boolean) {
        viewModelScope.launch {
            _error.value = null
            repository.updateVideo(
                sessionId, participantId, VideoRequest(videoEnabled)
            ).onSuccess {
                _message.value = it.message
                loadParticipants(sessionId)
            }.onFailure { _error.value = it.message }
        }
    }

    fun updateSpotlight(sessionId: String, participantId: String, spotlighted: Boolean) {
        viewModelScope.launch {
            _error.value = null
            repository.updateSpotlight(
                sessionId, participantId, SpotlightRequest(spotlighted)
            ).onSuccess {
                _message.value = it.message
                loadParticipants(sessionId)
            }.onFailure { _error.value = it.message }
        }
    }

    fun loadParticipants(sessionId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.getParticipants(sessionId)
                .onSuccess { _participants.value = it }
                .onFailure { _error.value = it.message }

            _isLoading.value = false
        }
    }

    fun registerParticipant(
        sessionId: String,
        participantId: String,
        userId: String
    ) {
        viewModelScope.launch {
            _error.value = null
            _message.value = null

            repository.registerParticipant(
                sessionId,
                RegisterMediaParticipantRequest(participantId, userId)
            ).onSuccess {
                _message.value = "Media participant registered"
                loadParticipants(sessionId)
            }.onFailure {
                _error.value = it.message
            }
        }
    }

    fun disconnectParticipant(
        sessionId: String,
        participantId: String
    ) {
        viewModelScope.launch {
            _error.value = null
            repository.disconnectParticipant(sessionId, participantId)
                .onSuccess {
                    _message.value = "Media participant disconnected"
                    loadParticipants(sessionId)
                }
                .onFailure { _error.value = it.message }
        }
    }

    fun updateLayout(sessionId: String, layoutMode: String) {
        viewModelScope.launch {
            _error.value = null
            repository.updateLayout(sessionId, LayoutRequest(layoutMode))
                .onSuccess {
                    _message.value = it.message
                    loadMediaSession(sessionId)
                }
                .onFailure { _error.value = it.message }
        }
    }

    private fun updateParticipant(updated: MediaParticipantDto) {
        _participants.value = _participants.value.map { current ->
            if (current.id == updated.id) updated else current
        }
    }

    private fun performSessionAction(
        loadingMessage: String,
        action: suspend () -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _message.value = loadingMessage

            try {
                action()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearMessages() {
        _error.value = null
        _message.value = null
    }

    fun checkMediaState(sessionId: String) {
        viewModelScope.launch {
            repository.getMediaState(sessionId).fold(
                onSuccess = { state ->
                    _message.value = buildString {
                        appendLine("Session: ${state.mediaSession.sessionId}")
                        appendLine("Status: ${state.mediaSession.status}")
                        appendLine("Participants: ${state.participants.size}")
                        appendLine("Provider: ${state.provider.name}")
                        appendLine("Mode: ${state.provider.mode}")
                        appendLine("Available: ${state.provider.available}")
                    }
                },
                onFailure = { error ->
                    _message.value = "Failed: ${error.message ?: "Unknown error"}"
                }
            )
        }
    }

    fun generateLiveKitToken(
        sessionId: String,
        participantId: String,
        userId: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _message.value = null
            _liveKitToken.value = null

            repository.generateLiveKitToken(
                sessionId = sessionId,
                participantId = participantId,
                userId = userId
            ).onSuccess { response ->
                _liveKitToken.value = response
                _message.value = "LiveKit token generated successfully"
            }.onFailure { error ->
                _error.value = error.message ?: "Token generation failed"
            }

            _isLoading.value = false
        }
    }

    fun connectToLiveKit(
        sessionId: String,
        participantId: String,
        userId: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _message.value = null

            try {
                val tokenResult = repository.generateLiveKitToken(
                    sessionId = sessionId,
                    participantId = participantId,
                    userId = userId
                )

                tokenResult.onSuccess { tokenResponse ->
                    _liveKitToken.value = tokenResponse

                    liveKitManager.connect(
                        serverUrl = tokenResponse.serverUrl,
                        token = tokenResponse.token
                    ).onSuccess {
                        _isLiveKitConnected.value = true
                        _message.value = "Connected to LiveKit room"
                    }.onFailure { error ->
                        _isLiveKitConnected.value = false
                        _error.value = error.message ?: "LiveKit connection failed"
                    }
                }.onFailure { error ->
                    _error.value = error.message ?: "Token generation failed"
                }

            } catch (e: Exception) {
                _error.value = e.message ?: "Connection failed"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // --- LIVEKIT OFFLINE/ONLINE MEDIA CONTROLS ---

    fun setLiveKitMicrophone(enabled: Boolean) {
        viewModelScope.launch {
            try {
                liveKitManager.setMicrophoneEnabled(enabled)
                _message.value = if (enabled) "Microphone enabled" else "Microphone disabled"
            } catch (e: Exception) {
                _error.value = e.message ?: "Microphone operation failed"
            }
        }
    }

    fun setLiveKitCamera(enabled: Boolean) {
        viewModelScope.launch {
            try {
                liveKitManager.setCameraEnabled(enabled)
                // Update State for Compose to re-render
                if (enabled) {
                    _localVideoTrack.value = liveKitManager.getLocalCameraTrack()
                } else {
                    _localVideoTrack.value = null
                }
                _message.value = if (enabled) "Camera enabled" else "Camera disabled"
            } catch (e: Exception) {
                _error.value = e.message ?: "Camera operation failed"
            }
        }
    }

    // --- OFFLINE CAMERA LIFECYCLE (GREEN ROOM) ---

    // --- OFFLINE CAMERA LIFECYCLE (GREEN ROOM) ---

    fun initializeLocalCamera() {
        viewModelScope.launch {
            try {
                Log.d("StreamBridgeCamera", "Starting camera initialization")

                val track = liveKitManager.createOfflineCameraTrack()

                Log.d(
                    "StreamBridgeCamera",
                    "Camera track created: ${track.name}"
                )

                _localVideoTrack.value = track

            } catch (e: Exception) {
                Log.e(
                    "StreamBridgeCamera",
                    "Camera initialization failed",
                    e
                )

                _error.value =
                    "Camera initialization failed: ${e.message}"
            }
        }
    }

    fun releaseLocalCamera() {
        viewModelScope.launch {
            try {
                liveKitManager.releaseOfflineTrack()
                _localVideoTrack.value = null
            } catch (e: Exception) {
                _error.value = "Failed to release camera"
            }
        }
    }

    fun disconnectFromLiveKit() {
        try {
            liveKitManager.disconnect()
            _isLiveKitConnected.value = false
            _liveKitToken.value = null
            _localVideoTrack.value = null
            _message.value = "Disconnected from LiveKit"
        } catch (e: Exception) {
            _error.value = e.message ?: "Disconnection failed"
        }
    }

    // --- HOST & PARTICIPANT FULL WORKFLOW ---

    fun startHostLiveKit(
        sessionId: String,
        userId: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _message.value = null

            try {
                require(userId.isNotBlank()) { "User ID is missing" }
                val hostParticipantId = "host-$userId"

                val mediaSession = repository.getMediaSession(sessionId)
                    .getOrElse {
                        repository.createMediaSession(
                            CreateMediaSessionRequest(
                                sessionId = sessionId,
                                layoutMode = "GRID"
                            )
                        ).getOrThrow()
                    }

                _mediaSession.value = mediaSession

                if (mediaSession.status != "ACTIVE") {
                    if (mediaSession.status != "CREATED") {
                        throw IllegalStateException("Media session cannot start from ${mediaSession.status}")
                    }
                    repository.startMediaSession(sessionId).getOrThrow()
                    _mediaSession.value = repository.getMediaSession(sessionId).getOrThrow()
                }

                val existingParticipants = repository.getParticipants(sessionId).getOrThrow()
                val existingHost = existingParticipants.find { it.participantId == hostParticipantId }

                if (existingHost == null) {
                    repository.registerParticipant(
                        sessionId,
                        RegisterMediaParticipantRequest(
                            participantId = hostParticipantId,
                            userId = userId
                        )
                    ).getOrThrow()
                } else if (existingHost.userId != userId) {
                    throw IllegalStateException("Host participant belongs to another user")
                }

                val token = repository.generateLiveKitToken(
                    sessionId = sessionId,
                    participantId = hostParticipantId,
                    userId = userId
                ).getOrThrow()

                _liveKitToken.value = token

                liveKitManager.connect(
                    serverUrl = token.serverUrl,
                    token = token.token
                ).getOrThrow()

                liveKitManager.publishOfflineTrack()

                _isLiveKitConnected.value = true
                _participants.value = repository.getParticipants(sessionId).getOrThrow()

                // Track update for UI
                _localVideoTrack.value = liveKitManager.getLocalCameraTrack()

                _message.value = "Host connected to LiveKit successfully"

            } catch (e: Exception) {
                _error.value = e.message ?: "Host connection failed"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun joinParticipantLiveKit(
        sessionId: String,
        participantId: String,
        userId: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _message.value = null

            try {
                require(userId.isNotBlank()) { "User ID is missing" }
                require(participantId.isNotBlank()) { "Participant ID is missing" }

                val mediaSession = repository.getMediaSession(sessionId).getOrThrow()
                if (mediaSession.status != "ACTIVE") {
                    throw IllegalStateException("Media session is not ACTIVE")
                }

                val existingParticipants = repository.getParticipants(sessionId).getOrThrow()
                val existingParticipant = existingParticipants.find { it.participantId == participantId }

                if (existingParticipant == null) {
                    repository.registerParticipant(
                        sessionId,
                        RegisterMediaParticipantRequest(
                            participantId = participantId,
                            userId = userId
                        )
                    ).getOrThrow()
                } else if (existingParticipant.userId != userId) {
                    throw IllegalStateException("Participant belongs to another user")
                }

                val token = repository.generateLiveKitToken(
                    sessionId = sessionId,
                    participantId = participantId,
                    userId = userId
                ).getOrThrow()

                _liveKitToken.value = token

                liveKitManager.connect(
                    serverUrl = token.serverUrl,
                    token = token.token
                ).getOrThrow()

                liveKitManager.publishOfflineTrack()

                _isLiveKitConnected.value = true
                _participants.value = repository.getParticipants(sessionId).getOrThrow()

                // Track update for UI
                _localVideoTrack.value = liveKitManager.getLocalCameraTrack()

                _message.value = "Participant connected to LiveKit successfully"

            } catch (e: Exception) {
                _error.value = e.message ?: "Participant connection failed"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearLiveKitToken() {
        _liveKitToken.value = null
    }



}