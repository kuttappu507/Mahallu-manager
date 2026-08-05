package com.mahallu.manager.core.database.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Single source of truth for the user-selected theme mode ("system"/"light"/"dark").
 * Persists to [SettingsRepository] and exposes a [StateFlow] so the Activity-level
 * theme can react live when the user toggles it in Settings.
 */
@Singleton
class ThemeModeController @Inject constructor(
    private val settingsRepo: SettingsRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _themeMode = MutableStateFlow(DEFAULT)
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    init {
        scope.launch {
            _themeMode.value = settingsRepo.getString(KEY, DEFAULT)
        }
    }

    fun setTheme(mode: String) {
        _themeMode.value = mode
        scope.launch {
            settingsRepo.put(KEY, mode)
        }
    }

    companion object {
        const val KEY = "theme_mode"
        const val DEFAULT = "system"
    }
}
