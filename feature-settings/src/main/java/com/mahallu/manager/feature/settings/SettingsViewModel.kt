package com.mahallu.manager.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahallu.manager.core.database.repository.SettingsRepository
import com.mahallu.manager.core.security.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val mahalluName: String = "Mahallu Manager",
    val mahalluAddress: String = "",
    val mahalluPhone: String = "",
    val mahalluEmail: String = "",
    val themeMode: String = "system",
    val backupAutoEnabled: Boolean = true,
    val userName: String = "User",
    val userRole: String = "",
    val lastBackupAt: Long = 0
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val settingsRepo: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.value = SettingsUiState(
                mahalluName = settingsRepo.getString("mahallu.name", "Mahallu Manager"),
                mahalluAddress = settingsRepo.getString("mahallu.address", ""),
                mahalluPhone = settingsRepo.getString("mahallu.phone", ""),
                mahalluEmail = settingsRepo.getString("mahallu.email", ""),
                themeMode = settingsRepo.getString("theme_mode", "system"),
                backupAutoEnabled = settingsRepo.getBoolean("backup.auto_enabled", true),
                userName = sessionManager.getString(SessionManager.KEY_FULL_NAME, "User") ?: "User",
                userRole = sessionManager.getString(SessionManager.KEY_ROLE, "") ?: "",
                lastBackupAt = settingsRepo.getLong("backup.last_at", 0)
            )
        }
    }

    fun updateMahallu(name: String, address: String, phone: String, email: String) {
        viewModelScope.launch {
            settingsRepo.put("mahallu.name", name)
            settingsRepo.put("mahallu.address", address)
            settingsRepo.put("mahallu.phone", phone)
            settingsRepo.put("mahallu.email", email)
            _state.update {
                it.copy(mahalluName = name, mahalluAddress = address, mahalluPhone = phone, mahalluEmail = email)
            }
        }
    }

    fun setTheme(mode: String) {
        viewModelScope.launch {
            settingsRepo.put("theme_mode", mode)
            _state.update { it.copy(themeMode = mode) }
        }
    }

    fun setAutoBackup(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepo.putBool("backup.auto_enabled", enabled)
            _state.update { it.copy(backupAutoEnabled = enabled) }
        }
    }
}