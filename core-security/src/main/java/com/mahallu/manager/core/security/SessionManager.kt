package com.mahallu.manager.core.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import timber.log.Timber

/**
 * Stores session tokens in EncryptedSharedPreferences backed by Android Keystore.
 * If androidx-security is unavailable, falls back to plain preferences.
 */
class SessionManager(context: Context) {

    private val prefs = try {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        EncryptedSharedPreferences.create(
            context,
            "mahallu_secure_session",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    } catch (t: Throwable) {
        Timber.w(t, "EncryptedSharedPreferences unavailable; using fallback")
        context.getSharedPreferences("mahallu_session_fallback", Context.MODE_PRIVATE)
    }

    fun putString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    fun getString(key: String, default: String? = null): String? = prefs.getString(key, default)

    fun putBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    fun getBoolean(key: String, default: Boolean = false): Boolean = prefs.getBoolean(key, default)

    fun clear() {
        prefs.edit().clear().apply()
    }

    fun putLong(key: String, value: Long) {
        prefs.edit().putLong(key, value).apply()
    }

    fun getLong(key: String, default: Long = 0L): Long = prefs.getLong(key, default)

    companion object {
        const val KEY_USER_ID = "user_id"
        const val KEY_USERNAME = "username"
        const val KEY_ROLE = "role"
        const val KEY_FULL_NAME = "full_name"
        const val KEY_LOGGED_IN = "logged_in"
        const val KEY_LOGIN_TIME = "login_time"
        const val KEY_REMEMBER = "remember"
        const val KEY_MUST_CHANGE_PASSWORD = "must_change_password"
    }
}