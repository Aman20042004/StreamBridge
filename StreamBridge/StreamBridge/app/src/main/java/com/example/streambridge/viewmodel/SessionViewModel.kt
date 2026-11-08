package com.example.streambridge.viewmodel



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.streambridge.data.model.Session
import com.example.streambridge.data.remote.session_api.CreateSessionRequest
import com.example.streambridge.data.remote.session_api.UpdateSessionRequest

import com.example.streambridge.data.repository.SessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SessionViewModel(
    private val sessionRepository: SessionRepository = SessionRepository()
) : ViewModel() {

    private val _session = MutableStateFlow<Session?>(null)
    val session: StateFlow<Session?> = _session.asStateFlow()

    private val _sessions = MutableStateFlow<List<Session>>(emptyList())
    val sessions: StateFlow<List<Session>> = _sessions.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun resetSessionState() {
        _session.value = null
        _error.value = null
        _isLoading.value = false
    }

    fun createSession(
        ownerId: String,
        title: String,
        description: String?,
        accessMode: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val request = CreateSessionRequest(
                ownerId = ownerId,
                title = title,
                description = description,
                accessMode = accessMode
            )

            sessionRepository
                .createSession(request)
                .onSuccess {
                    _session.value = it
                }
                .onFailure {
                    _error.value =
                        it.message ?: "Failed to create session"
                }

            _isLoading.value = false
        }
    }

    fun getSession(sessionId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            sessionRepository
                .getSession(sessionId)
                .onSuccess {
                    _session.value = it
                }
                .onFailure {
                    _error.value =
                        it.message ?: "Failed to load session"
                }

            _isLoading.value = false
        }
    }

    fun getSessionsByOwner(ownerId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            sessionRepository
                .getSessionsByOwner(ownerId)
                .onSuccess {
                    _sessions.value = it
                }
                .onFailure {
                    _error.value =
                        it.message ?: "Failed to load sessions"
                }

            _isLoading.value = false
        }
    }

    fun updateSession(
        sessionId: String,
        title: String,
        description: String?,
        accessMode: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val request = UpdateSessionRequest(
                title = title,
                description = description,
                accessMode = accessMode
            )

            sessionRepository
                .updateSession(sessionId, request)
                .onSuccess {
                    _session.value = it
                }
                .onFailure {
                    _error.value =
                        it.message ?: "Failed to update session"
                }

            _isLoading.value = false
        }
    }

    fun startSession(
        sessionId: String,
        ownerId: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            sessionRepository
                .startSession(sessionId, ownerId)
                .onSuccess {
                    _session.value = it
                }
                .onFailure {
                    _error.value =
                        it.message ?: "Failed to start session"
                }

            _isLoading.value = false
        }
    }

    fun endSession(
        sessionId: String,
        ownerId: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            sessionRepository
                .endSession(sessionId, ownerId)
                .onSuccess {
                    _session.value = it
                }
                .onFailure {
                    _error.value =
                        it.message ?: "Failed to end session"
                }

            _isLoading.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }
}

