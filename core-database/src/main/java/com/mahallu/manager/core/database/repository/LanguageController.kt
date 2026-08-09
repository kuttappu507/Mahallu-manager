package com.mahallu.manager.core.database.repository

import android.content.SharedPreferences
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
 * Single source of truth for the user-selected app language (e.g. "en"/"ml").
 *
 * The selected value is persisted to [SharedPreferences] synchronously so the
 * activity can apply it in [android.app.Activity.attachBaseContext] before any
 * resource is created, and is mirrored to [SettingsRepository] (Room) for the
 * Settings screen. Exposes a [StateFlow] so the Activity-level locale can react
 * live when the user toggles it in Settings.
 */
@Singleton
class LanguageController @Inject constructor(
    private val appLanguagePrefs: SharedPreferences,
    private val settingsRepo: SettingsRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val prefs: SharedPreferences = appLanguagePrefs

    private val _language = MutableStateFlow(prefs.getString(KEY, DEFAULT) ?: DEFAULT)
    val language: StateFlow<String> = _language.asStateFlow()

    init {
        scope.launch {
            val stored = settingsRepo.getString(KEY, DEFAULT)
            if (stored != _language.value) {
                _language.value = stored
                prefs.edit().putString(KEY, stored).apply()
            }
        }
    }

    fun setLanguage(lang: String) {
        val value = if (lang == "ml") "ml" else "en"
        _language.value = value
        prefs.edit().putString(KEY, value).apply()
        scope.launch {
            settingsRepo.put(KEY, value)
        }
    }

    companion object {
        const val KEY = "app_language"
        const val DEFAULT = "en"
        const val PREFS_FILE = "app_language_prefs"
    }
}
