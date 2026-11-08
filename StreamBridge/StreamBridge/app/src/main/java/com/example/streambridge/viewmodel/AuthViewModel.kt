package com.example.streambridge.viewmodel



import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.streambridge.data.local.SessionManager
import com.example.streambridge.data.remote.RetrofitClient
import com.example.streambridge.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository =
        AuthRepository(RetrofitClient.authApi)

    private val sessionManager =
        SessionManager(application)

    private val _loginState =
        MutableStateFlow<String?>(null)

    val loginState: StateFlow<String?> =
        _loginState

    private val _registerState =
        MutableStateFlow<String?>(null)

    val registerState: StateFlow<String?> =
        _registerState

    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit
    ) {

        viewModelScope.launch {

            try {

                _loginState.value = "Logging in..."

                val response = repository.login(
                    email = email,
                    password = password
                )

                sessionManager.saveSession(
                    userId = response.userId,
                    email = response.email,
                    role = response.role
                )

                _loginState.value = response.message

                onSuccess()

            } catch (e: Exception) {

                _loginState.value =
                    "Login failed: ${e.message}"
            }
        }
    }

    fun register(
        email: String,
        password: String,
        onSuccess: () -> Unit
    ) {

        viewModelScope.launch {

            try {

                _registerState.value =
                    "Creating account..."

                val response = repository.register(
                    email = email,
                    password = password
                )

                sessionManager.saveSession(
                    userId = response.userId,
                    email = response.email,
                    role = response.role
                )

                _registerState.value = response.message

                onSuccess()

            } catch (e: Exception) {

                _registerState.value =
                    "Registration failed: ${e.message}"
            }
        }
    }
}