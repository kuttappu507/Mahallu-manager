package com.mahallu.manager.feature.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mahallu.manager.core.database.entity.UserEntity
import com.mahallu.manager.core.database.repository.AuditActor
import com.mahallu.manager.core.database.repository.CurrentActor
import com.mahallu.manager.core.database.repository.SettingsRepository
import com.mahallu.manager.core.database.repository.UserRepository
import com.mahallu.manager.core.security.SessionManager
import com.mahallu.manager.feature.auth.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthState(
    val isLoggedIn: Boolean = false,
    val isInitializing: Boolean = true,
    val currentUser: UserEntity? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val mahalluName: String = "",
    val mustChangePassword: Boolean = false
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    application: Application,
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager,
    private val currentActor: CurrentActor,
    private val settingsRepository: SettingsRepository
) : AndroidViewModel(application) {

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        checkExistingSession()
        viewModelScope.launch {
            val name = settingsRepository.getString("mahallu.name", "Mahallu Manager")
            _authState.update { it.copy(mahalluName = name) }
        }
    }

    private fun checkExistingSession() {
        viewModelScope.launch {
            val userId = sessionManager.getString(SessionManager.KEY_USER_ID)
            if (userId != null) {
                val user = userRepository.getById(userId)
                if (user != null && user.isActive) {
                    currentActor.set(AuditActor(user.id, user.fullName))
                    _authState.update {
                        it.copy(
                            isLoggedIn = true,
                            isInitializing = false,
                            currentUser = user,
                            mustChangePassword = sessionManager.getBoolean(SessionManager.KEY_MUST_CHANGE_PASSWORD)
                        )
                    }
                } else {
                    sessionManager.clear()
                    currentActor.set(null)
                    _authState.update { it.copy(isInitializing = false) }
                }
            } else {
                _authState.update { it.copy(isInitializing = false) }
            }
        }
    }

    fun login(username: String, password: String, remember: Boolean, onSuccess: () -> Unit) {
        if (username.isBlank()) {
            _authState.update { it.copy(error = getApplication<Application>().getString(R.string.login_error_enter_username)) }
            return
        }
        if (password.isBlank()) {
            _authState.update { it.copy(error = getApplication<Application>().getString(R.string.login_error_enter_password)) }
            return
        }
        _authState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            val result = userRepository.authenticate(username.trim(), password)
            result.fold(
                onSuccess = { user ->
                    sessionManager.putString(SessionManager.KEY_USER_ID, user.id)
                    sessionManager.putString(SessionManager.KEY_USERNAME, user.username)
                    sessionManager.putString(SessionManager.KEY_FULL_NAME, user.fullName)
                    sessionManager.putString(SessionManager.KEY_ROLE, user.role)
                    sessionManager.putBoolean(SessionManager.KEY_REMEMBER, remember)
                    sessionManager.putBoolean(SessionManager.KEY_LOGGED_IN, true)
                    sessionManager.putLong(SessionManager.KEY_LOGIN_TIME, System.currentTimeMillis())
                    currentActor.set(AuditActor(user.id, user.fullName))
                    val mustChange = user.isDefaultCredential
                    sessionManager.putBoolean(SessionManager.KEY_MUST_CHANGE_PASSWORD, mustChange)
                    _authState.update {
                        AuthState(
                            isInitializing = false,
                            isLoggedIn = true,
                            currentUser = user,
                            mustChangePassword = mustChange
                        )
                    }
                    onSuccess()
                },
                onFailure = { err ->
                    _authState.update { it.copy(isLoading = false, error = err.message ?: getApplication<Application>().getString(R.string.login_error_failed)) }
                }
            )
        }
    }

    fun changePassword(currentPassword: String, newPassword: String, onDone: () -> Unit) {
        val userId = sessionManager.getString(SessionManager.KEY_USER_ID)
        if (userId == null) {
            onDone()
            return
        }
        if (newPassword.length < 6) {
            _authState.update { it.copy(error = getApplication<Application>().getString(R.string.change_password_error_too_short)) }
            return
        }
        _authState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            val valid = userRepository.verifyPassword(userId, currentPassword)
            if (!valid) {
                _authState.update { it.copy(isLoading = false, error = getApplication<Application>().getString(R.string.change_password_error_wrong_current)) }
                return@launch
            }
            userRepository.updatePassword(userId, newPassword)
                .onSuccess {
                    sessionManager.putBoolean(SessionManager.KEY_MUST_CHANGE_PASSWORD, false)
                    _authState.update { it.copy(isLoading = false, error = null, mustChangePassword = false) }
                    onDone()
                }
                .onFailure { err ->
                    _authState.update { it.copy(isLoading = false, error = err.message ?: getApplication<Application>().getString(R.string.change_password_error_failed)) }
                }
        }
    }

    fun logout() {
        sessionManager.clear()
        currentActor.set(null)
        _authState.value = AuthState(isInitializing = false)
    }

    fun clearError() {
        _authState.update { it.copy(error = null) }
    }

    fun setError(message: String) {
        _authState.update { it.copy(error = message) }
    }

    fun currentRole(): String? = sessionManager.getString(SessionManager.KEY_ROLE)
    fun currentUserName(): String = sessionManager.getString(SessionManager.KEY_FULL_NAME, getApplication<Application>().getString(R.string.login_default_user_name)) ?: getApplication<Application>().getString(R.string.login_default_user_name)
}
