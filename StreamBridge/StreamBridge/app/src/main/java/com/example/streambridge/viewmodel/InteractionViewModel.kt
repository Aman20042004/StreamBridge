package com.example.streambridge.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.streambridge.data.model.AskDto
import com.example.streambridge.data.model.CommentDto
import com.example.streambridge.data.model.CreateAskRequest
import com.example.streambridge.data.model.CreateCommentRequest
import com.example.streambridge.data.repository.InteractionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InteractionViewModel(
    private val repository: InteractionRepository = InteractionRepository()
) : ViewModel() {

    private val _comments = MutableStateFlow<List<CommentDto>>(emptyList())
    val comments = _comments.asStateFlow()

    private val _askRequests = MutableStateFlow<List<AskDto>>(emptyList())
    val askRequests = _askRequests.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting = _isSubmitting.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage = _successMessage.asStateFlow()

    fun loadComments(sessionId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.getComments(sessionId)
                .onSuccess { result ->
                    _comments.value = result
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Failed to load comments"
                }

            _isLoading.value = false
        }
    }

    fun sendComment(
        sessionId: String,
        userId: String,
        message: String,
        onSuccess: () -> Unit = {}
    ) {
        val cleanMessage = message.trim()

        if (cleanMessage.isBlank() || cleanMessage.length > 1000) {
            _error.value = "Message must contain 1–1000 characters."
            return
        }

        viewModelScope.launch {
            _isSubmitting.value = true
            _error.value = null
            _successMessage.value = null

            repository.createComment(
                CreateCommentRequest(
                    sessionId = sessionId,
                    userId = userId,
                    message = cleanMessage
                )
            ).onSuccess { created ->
                if (created.sessionId == sessionId) {
                    _comments.value = _comments.value + created
                }
                _successMessage.value = "Comment sent"
                onSuccess()
            }.onFailure { exception ->
                _error.value = exception.message ?: "Failed to send comment"
            }

            _isSubmitting.value = false
        }
    }

    fun deleteComment(
        commentId: String,
        userId: String,
        sessionId: String
    ) {
        val comment = _comments.value.firstOrNull { it.id == commentId }

        if (comment == null || comment.sessionId != sessionId) {
            _error.value = "Comment not found in this session."
            return
        }

        if (comment.userId != userId) {
            _error.value = "You can delete only your own comments."
            return
        }

        viewModelScope.launch {
            _isSubmitting.value = true
            _error.value = null

            repository.deleteComment(commentId, userId)
                .onSuccess {
                    _comments.value = _comments.value.filterNot {
                        it.id == commentId
                    }
                    _successMessage.value = "Comment deleted"
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Failed to delete comment"
                }

            _isSubmitting.value = false
        }
    }

    fun createAsk(
        sessionId: String,
        userId: String,
        message: String,
        onSuccess: () -> Unit = {}
    ) {
        val cleanMessage = message.trim()

        viewModelScope.launch {
            _isSubmitting.value = true
            _error.value = null
            _successMessage.value = null

            repository.createAsk(
                CreateAskRequest(
                    sessionId = sessionId,
                    userId = userId,
                    message = cleanMessage.ifBlank { null }
                )
            ).onSuccess { created ->
                _successMessage.value = "Request submitted: ${created.status}"
                onSuccess()
            }.onFailure { exception ->
                _error.value = exception.message ?: "Failed to submit request"
            }

            _isSubmitting.value = false
        }
    }

    fun loadAskRequests(
        sessionId: String,
        status: String? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.getAskRequests(sessionId, status)
                .onSuccess { result ->
                    _askRequests.value = result.filter {
                        it.sessionId == sessionId
                    }
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Failed to load requests"
                }

            _isLoading.value = false
        }
    }

    fun acceptAsk(
        askId: String,
        ownerId: String,
        sessionId: String
    ) {
        decideAsk(askId, ownerId, sessionId, accept = true)
    }

    fun rejectAsk(
        askId: String,
        ownerId: String,
        sessionId: String
    ) {
        decideAsk(askId, ownerId, sessionId, accept = false)
    }

    private fun decideAsk(
        askId: String,
        ownerId: String,
        sessionId: String,
        accept: Boolean
    ) {
        val request = _askRequests.value.firstOrNull { it.id == askId }

        if (request == null || request.sessionId != sessionId) {
            _error.value = "Request not found in this session."
            return
        }

        if (request.status != "PENDING") {
            _error.value = "Only pending requests can be changed."
            return
        }

        viewModelScope.launch {
            _isSubmitting.value = true
            _error.value = null
            _successMessage.value = null

            val result = if (accept) {
                repository.acceptAsk(askId, ownerId)
            } else {
                repository.rejectAsk(askId, ownerId)
            }

            result.onSuccess { updated ->
                _askRequests.value = _askRequests.value.map {
                    if (it.id == updated.id) updated else it
                }
                _successMessage.value =
                    if (accept) "Request accepted" else "Request rejected"
            }.onFailure { exception ->
                _error.value = exception.message ?: "Failed to update request"
            }

            _isSubmitting.value = false
        }
    }

    fun clearMessages() {
        _error.value = null
        _successMessage.value = null
    }
}