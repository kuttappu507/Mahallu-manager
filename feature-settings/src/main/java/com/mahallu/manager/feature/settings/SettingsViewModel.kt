package com.mahallu.manager.feature.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahallu.manager.core.database.entity.CertificateEntity
import com.mahallu.manager.core.database.entity.FamilyEntity
import com.mahallu.manager.core.database.entity.MemberEntity
import com.mahallu.manager.core.database.repository.CertificateRepository
import com.mahallu.manager.core.database.repository.FamilyRepository
import com.mahallu.manager.core.database.repository.LanguageController
import com.mahallu.manager.core.database.repository.MemberRepository
import com.mahallu.manager.core.database.repository.SettingsRepository
import com.mahallu.manager.core.database.repository.ThemeModeController
import com.mahallu.manager.core.security.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import feature.settings.feature.settings.R
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
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
    val language: String = "en",
    val backupAutoEnabled: Boolean = true,
    val userName: String = "User",
    val userRole: String = "",
    val lastBackupAt: Long = 0,
    val totalMembers: Int = 0,
    val totalFamilies: Int = 0,
    val totalCertificates: Int = 0
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sessionManager: SessionManager,
    private val settingsRepo: SettingsRepository,
    private val themeModeController: ThemeModeController,
    private val languageController: LanguageController,
    private val memberRepo: MemberRepository,
    private val familyRepo: FamilyRepository,
    private val certificateRepo: CertificateRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    private val _toastMessages = MutableSharedFlow<String>()
    val toastMessages: SharedFlow<String> = _toastMessages.asSharedFlow()

    init {
        viewModelScope.launch {
            _state.value = SettingsUiState(
                mahalluName = settingsRepo.getString("mahallu.name", context.getString(R.string.settings_app_name)),
                mahalluAddress = settingsRepo.getString("mahallu.address", ""),
                mahalluPhone = settingsRepo.getString("mahallu.phone", ""),
                mahalluEmail = settingsRepo.getString("mahallu.email", ""),
                themeMode = settingsRepo.getString("theme_mode", "system"),
                language = settingsRepo.getString(LanguageController.KEY, LanguageController.DEFAULT),
                backupAutoEnabled = settingsRepo.getBoolean("backup.auto_enabled", true),
                userName = sessionManager.getString(SessionManager.KEY_FULL_NAME, context.getString(R.string.settings_default_user)) ?: context.getString(R.string.settings_default_user),
                userRole = sessionManager.getString(SessionManager.KEY_ROLE, "") ?: "",
                lastBackupAt = settingsRepo.getLong("backup.last_at", 0)
            )
        }
        viewModelScope.launch {
            memberRepo.observeAll().collect { list: List<MemberEntity> ->
                _state.update { it.copy(totalMembers = list.size) }
            }
        }
        viewModelScope.launch {
            familyRepo.observeAll().collect { list: List<FamilyEntity> ->
                _state.update { it.copy(totalFamilies = list.size) }
            }
        }
        viewModelScope.launch {
            certificateRepo.observeAll().collect { list: List<CertificateEntity> ->
                _state.update { it.copy(totalCertificates = list.size) }
            }
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
            _toastMessages.emit(context.getString(R.string.settings_saved_message))
        }
    }

    fun setTheme(mode: String) {
        themeModeController.setTheme(mode)
        _state.update { it.copy(themeMode = mode) }
    }

    fun setLanguage(lang: String) {
        languageController.setLanguage(lang)
        _state.update { it.copy(language = lang) }
    }

    fun setAutoBackup(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepo.putBool("backup.auto_enabled", enabled)
            _state.update { it.copy(backupAutoEnabled = enabled) }
        }
    }
}