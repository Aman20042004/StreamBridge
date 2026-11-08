package com.example.streambridge.viewmodel




import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.streambridge.data.model.UserProfile
import com.example.streambridge.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userRepository: UserRepository = UserRepository()
) : ViewModel() {

    private val _profile = MutableStateFlow<UserProfile?>(null)
    val profile: StateFlow<UserProfile?> = _profile.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadProfile(userId: String) {

        Log.d(
            "ProfileDebug",
            "loadProfile called with userId = $userId"
        )

        viewModelScope.launch {

            _isLoading.value = true
            _error.value = null

            userRepository
                .getUserProfile(userId)
                .onSuccess { user ->
                    Log.d(
                        "ProfileDebug",
                        "PROFILE SUCCESS = $user"
                    )

                    _profile.value = user
                }
                .onFailure { exception ->
                    Log.e(
                        "ProfileDebug",
                        "PROFILE FAILED",
                        exception
                    )

                    _error.value =
                        exception.message ?: "Failed to load profile"
                }

            _isLoading.value = false
        }
    }
}