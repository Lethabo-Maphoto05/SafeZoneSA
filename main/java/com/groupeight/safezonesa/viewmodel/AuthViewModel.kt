package com.groupeight.safezonesa.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.groupeight.safezonesa.data.SessionManager
import com.groupeight.safezonesa.network.ApiClient
import com.groupeight.safezonesa.network.apiErrorMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/** UI state shared by Login and Register (Section 4.2, FR1). */
data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

/**
 * Talks to POST /api/auth/login, /api/auth/register, /api/auth/verify-otp (Section 5.3).
 * On a successful login the returned JWT is stored via [SessionManager] so [ApiClient]
 * attaches it to every subsequent request. The API isn't deployed yet (Gantt milestone:
 * core endpoints live 20 Sept 2026), so failures here are expected until then — the error
 * message is surfaced to the user rather than swallowed, per NFR1/NFR2 expectations around
 * predictable, secure auth behaviour.
 */
class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState(errorMessage = "Enter your email and password.")
            return
        }
        _uiState.value = AuthUiState(isLoading = true)
        viewModelScope.launch {
            try {
                val service = ApiClient.getService(getApplication())
                val response = service.login(mapOf("email" to email, "password" to password))
                if (response.isSuccessful) {
                    val body = response.body()
                    val token = body?.get("token")
                    val userId = body?.get("userId") ?: ""
                    val displayName = body?.get("displayName") ?: ""
                    val email = body?.get("email") ?: ""
                    if (token != null) {
                        SessionManager.saveSession(getApplication(), token, userId, displayName, email)
                        _uiState.value = AuthUiState()
                        onSuccess()
                    } else {
                        _uiState.value = AuthUiState(errorMessage = "Login succeeded but no token was returned.")
                    }
                } else {
                    val message = response.apiErrorMessage("Login failed (${response.code()}). Check your details and try again.")
                    _uiState.value = AuthUiState(errorMessage = message)
                }
            } catch (e: Exception) {
                // Expected until the Section 5.5 Azure deployment is live — see README.
                _uiState.value = AuthUiState(errorMessage = "Couldn't reach the SafeZone SA server: ${e.localizedMessage ?: "unknown error"}")
            }
        }
    }

    fun register(fullName: String, email: String, phone: String, password: String, onOtpRequired: () -> Unit) {
        if (fullName.isBlank() || email.isBlank() || phone.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState(errorMessage = "Please fill in every field.")
            return
        }
        _uiState.value = AuthUiState(isLoading = true)
        viewModelScope.launch {
            try {
                val service = ApiClient.getService(getApplication())
                val response = service.register(
                    mapOf("fullName" to fullName, "email" to email, "mobileNumber" to phone, "password" to password)
                )
                if (response.isSuccessful) {
                    _uiState.value = AuthUiState()
                    onOtpRequired() // FR1: an OTP is sent by SMS before the account is authorised
                } else {
                    val message = response.apiErrorMessage("Registration failed (${response.code()}).")
                    _uiState.value = AuthUiState(errorMessage = message)
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState(errorMessage = "Couldn't reach the SafeZone SA server: ${e.localizedMessage ?: "unknown error"}")
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
