package com.example.streambridge.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.streambridge.data.remote.participant_api.JoinParticipantRequest
import com.example.streambridge.data.remote.participant_api.Participant
import com.example.streambridge.data.repository.ParticipantRepository
import com.example.streambridge.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ParticipantUiModel(
    val participant: Participant,
    val name: String
)

class ParticipantViewModel(
    private val repository: ParticipantRepository = ParticipantRepository(),
    private val userRepository: UserRepository = UserRepository()
) : ViewModel() {

    private val _participants =
        MutableStateFlow<List<Participant>>(emptyList())
    val participants: StateFlow<List<Participant>> =
        _participants.asStateFlow()
    private val _participantsWithProfiles =
        MutableStateFlow<List<ParticipantUiModel>>(emptyList())

    val participantsWithProfiles: StateFlow<List<ParticipantUiModel>> =
        _participantsWithProfiles.asStateFlow()
    private val _myParticipant =
        MutableStateFlow<Participant?>(null)
    val myParticipant: StateFlow<Participant?> =
        _myParticipant.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun joinSession(sessionId: String, userId: String) {
        if (sessionId.isBlank() || userId.isBlank()) {
            _error.value = "Session ID and user ID are required"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _message.value = null

            repository.joinSession(
                JoinParticipantRequest(
                    sessionId = sessionId,
                    userId = userId
                )
            ).onSuccess { participant ->
                _myParticipant.value = participant
                loadParticipants(sessionId)
                _message.value = "Joined session successfully"
            }.onFailure { exception ->
                _error.value = exception.message ?: "Unable to join session"
            }

            _isLoading.value = false
        }
    }

    fun loadParticipants(sessionId: String) {
        if (sessionId.isBlank()) return

        viewModelScope.launch {
            repository.getParticipantsBySession(sessionId)
                .onSuccess { list ->
                    _participants.value = list

                    val currentId = _myParticipant.value?.id
                    if (currentId != null) {
                        _myParticipant.value =
                            list.firstOrNull { it.id == currentId }
                                ?: _myParticipant.value
                    }

                    val activeParticipants = list.filter {
                        it.status == "JOINED"
                    }

                    _participantsWithProfiles.value =
                        activeParticipants.map { participant ->
                            val profileResult =
                                userRepository.getUserProfile(
                                    participant.userId
                                )

                            ParticipantUiModel(
                                participant = participant,
                                name = profileResult.getOrNull()
                                    ?.name
                                    ?.takeIf { it.isNotBlank() }
                                    ?: "Unknown participant"
                            )
                        }
                }
                .onFailure { exception ->
                    _error.value =
                        exception.message ?: "Unable to load participants"
                }
        }
    }

    fun leaveSession(
        participantId: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.leaveSession(participantId)
                .onSuccess { updatedParticipant ->
                    _myParticipant.value = updatedParticipant
                    _message.value = "Left session successfully"
                    onSuccess()
                }
                .onFailure { exception ->
                    _error.value =
                        exception.message ?: "Failed to leave session"
                }

            _isLoading.value = false
        }
    }

    fun setMuted(muted: Boolean) {
        val participant = _myParticipant.value

        if (participant?.id == null) {
            _error.value = "Join the session first"
            return
        }

        viewModelScope.launch {
            _error.value = null

            repository.updateMute(participant.id, muted)
                .onSuccess { _myParticipant.value = it }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Unable to update mute"
                }
        }
    }

    fun setVideoEnabled(enabled: Boolean) {
        val participant = _myParticipant.value

        if (participant?.id == null) {
            _error.value = "Join the session first"
            return
        }

        viewModelScope.launch {
            _error.value = null

            repository.updateVideo(participant.id, enabled)
                .onSuccess { _myParticipant.value = it }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Unable to update video"
                }
        }
    }

    fun removeParticipant(participantId: String, ownerId: String) {
        if (participantId.isBlank() || ownerId.isBlank()) {
            _error.value = "Participant ID and owner ID are required"
            return
        }

        viewModelScope.launch {
            _error.value = null

            repository.removeParticipant(participantId, ownerId)
                .onSuccess { updated ->
                    loadParticipants(updated.sessionId)
                    _message.value = "Participant removed successfully"
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Unable to remove participant"
                }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun clearMessage() {
        _message.value = null
    }
}