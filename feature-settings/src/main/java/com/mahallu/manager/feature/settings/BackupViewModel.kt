package com.mahallu.manager.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahallu.manager.feature.settings.backup.BackupManager
import com.mahallu.manager.core.database.entity.BackupEntity
import com.mahallu.manager.core.database.repository.BackupRepository
import com.mahallu.manager.core.database.repository.SettingsRepository
import com.mahallu.manager.feature.settings.worker.BackupScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BackupUiState(
    val backups: List<BackupEntity> = emptyList(),
    val autoEnabled: Boolean = true,
    val lastBackupAt: Long = 0,
    val isBackingUp: Boolean = false,
    val message: String? = null
)

@HiltViewModel
class BackupViewModel @Inject constructor(
    private val backupManager: BackupManager,
    private val backupRepo: BackupRepository,
    private val settingsRepo: SettingsRepository,
    private val scheduler: BackupScheduler
) : ViewModel() {

    private val isBackingUp = MutableStateFlow(false)
    private val message = MutableStateFlow<String?>(null)
    private val lastBackup = MutableStateFlow(0L)
    private val autoEnabled = MutableStateFlow(true)

    val state: StateFlow<BackupUiState> = combine(
        backupRepo.observeAll(),
        combine(isBackingUp, message, lastBackup, autoEnabled) { b, m, l, a ->
            BackupUiState(
                backups = emptyList(),
                isBackingUp = b,
                message = m,
                lastBackupAt = l,
                autoEnabled = a
            )
        }
    ) { backups, uiState ->
        uiState.copy(backups = backups)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), BackupUiState())

    init {
        viewModelScope.launch {
            lastBackup.value = settingsRepo.getLong("backup.last_at", 0)
            autoEnabled.value = settingsRepo.getBoolean("backup.auto_enabled", true)
        }
    }

    fun backupNow() {
        if (isBackingUp.value) return
        isBackingUp.value = true
        viewModelScope.launch {
            val outcome = backupManager.performBackup(isManual = true)
            isBackingUp.value = false
            message.value = outcome.message
            if (outcome.success) lastBackup.value = System.currentTimeMillis()
        }
    }

    fun toggleAutoBackup() {
        viewModelScope.launch {
            val newVal = !autoEnabled.value
            settingsRepo.putBool("backup.auto_enabled", newVal)
            autoEnabled.value = newVal
            if (newVal) scheduler.scheduleIfEnabled() else scheduler.cancel()
            message.value = if (newVal) "Auto backup enabled" else "Auto backup disabled"
        }
    }

    fun restore(backupId: String) {
        viewModelScope.launch {
            isBackingUp.value = true
            val result = backupManager.restoreBackup(backupId)
            isBackingUp.value = false
            message.value = result.fold(
                onSuccess = { "Restored successfully. Please relaunch the app." },
                onFailure = { "Restore failed: ${it.message}" }
            )
        }
    }
}