package com.example.streambridge.viewmodel



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.streambridge.data.model.*
import com.example.streambridge.data.repository.MediaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class MediaViewModel(
    private val repository: MediaRepository
) : ViewModel() {

    private val _mediaSession = MutableStateFlow<MediaSessionDto?>(null)
    val mediaSession: StateFlow<MediaSessionDto?> =
        _mediaSession.asStateFlow()

    private val _participants =
        MutableStateFlow<List<MediaParticipantDto>>(emptyList())
    val participants: StateFlow<List<MediaParticipantDto>> =
        _participants.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    private val _healthStatus = MutableStateFlow("Not checked")
    val healthStatus: StateFlow<String> = _healthStatus.asStateFlow()

    fun checkHealth() {
        viewModelScope.launch {
            _healthStatus.value = "Checking..."

            repository.health().fold(
                onSuccess = { response ->
                    _healthStatus.value =
                        response["status"]?.toString() ?: "Connected"
                },
                onFailure = { error ->
                    _healthStatus.value =
                        "Failed: ${error.message ?: "Connection error"}"
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
                    _mediaSession.value = it
                    _message.value = "Media session started"
                }
                .onFailure { _error.value = it.message }
        }
    }

    fun endMediaSession(sessionId: String) {
        performSessionAction("Ending media session") {
            repository.endMediaSession(sessionId)
                .onSuccess {
                    _mediaSession.value = it
                    _message.value = "Media session ended"
                }
                .onFailure { _error.value = it.message }
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

    fun updateAudio(
        sessionId: String,
        participantId: String,
        muted: Boolean
    ) {
        viewModelScope.launch {
            _error.value = null

            repository.updateAudio(
                sessionId, participantId, AudioRequest(muted)
            ).onSuccess {
                updateParticipant(it)
                _message.value = if (muted) "Audio muted" else "Audio unmuted"
            }.onFailure {
                _error.value = it.message
            }
        }
    }

    fun updateVideo(
        sessionId: String,
        participantId: String,
        videoEnabled: Boolean
    ) {
        viewModelScope.launch {
            _error.value = null

            repository.updateVideo(
                sessionId, participantId, VideoRequest(videoEnabled)
            ).onSuccess {
                updateParticipant(it)
                _message.value =
                    if (videoEnabled) "Video enabled" else "Video disabled"
            }.onFailure {
                _error.value = it.message
            }
        }
    }

    fun updateSpotlight(
        sessionId: String,
        participantId: String,
        spotlighted: Boolean
    ) {
        viewModelScope.launch {
            _error.value = null

            repository.updateSpotlight(
                sessionId, participantId, SpotlightRequest(spotlighted)
            ).onSuccess {
                updateParticipant(it)
            }.onFailure {
                _error.value = it.message
            }
        }
    }

    fun updateLayout(sessionId: String, layoutMode: String) {
        viewModelScope.launch {
            _error.value = null

            repository.updateLayout(
                sessionId, LayoutRequest(layoutMode)
            ).onSuccess {
                _mediaSession.value = it
                _message.value = "Layout updated"
            }.onFailure {
                _error.value = it.message
            }
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
                    _message.value =
                        "Failed: ${error.message ?: "Unknown error"}"
                }
            )
        }
    }
}