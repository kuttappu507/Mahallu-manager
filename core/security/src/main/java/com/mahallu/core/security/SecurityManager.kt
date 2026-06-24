package com.mahallu.core.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecurityManager @Inject constructor(
    private val context: Context
) {
    private val masterKey by lazy {
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    private val encryptedPrefs by lazy {
        EncryptedSharedPreferences.create(
            context,
            "secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun hashPassword(password: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(password.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }

    fun verifyPassword(password: String, hashedPassword: String): Boolean {
        return hashPassword(password) == hashedPassword
    }

    fun saveSession(userId: Long, username: String) {
        encryptedPrefs.edit().apply {
            putLong("session_user_id", userId)
            putString("session_username", username)
            putBoolean("is_logged_in", true)
            apply()
        }
    }

    fun getSessionUserId(): Long? {
        return if (encryptedPrefs.getBoolean("is_logged_in", false)) {
            encryptedPrefs.getLong("session_user_id", -1).takeIf { it != -1L }
        } else null
    }

    fun getSessionUsername(): String? {
        return encryptedPrefs.getString("session_username", null)
    }

    fun isLoggedIn(): Boolean {
        return encryptedPrefs.getBoolean("is_logged_in", false)
    }

    fun clearSession() {
        encryptedPrefs.edit().apply {
            clear()
            apply()
        }
    }

    fun saveString(key: String, value: String) {
        encryptedPrefs.edit().putString(key, value).apply()
    }

    fun getString(key: String, defaultValue: String? = null): String? {
        return encryptedPrefs.getString(key, defaultValue)
    }

    fun saveLong(key: String, value: Long) {
        encryptedPrefs.edit().putLong(key, value).apply()
    }

    fun getLong(key: String, defaultValue: Long = 0L): Long {
        return encryptedPrefs.getLong(key, defaultValue)
    }

    fun saveBoolean(key: String, value: Boolean) {
        encryptedPrefs.edit().putBoolean(key, value).apply()
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return encryptedPrefs.getBoolean(key, defaultValue)
    }
}
