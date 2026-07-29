package com.mahallu.manager.backup

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Google Drive integration stub. The full Google Drive REST API requires the
 * `play-services-auth` and `google-api-services-drive` artifacts. In this build
 * we keep the contract but return null — the local backup path still works
 * end-to-end. Wire up real Drive SDK + OAuth in production.
 */
@Singleton
class GoogleDriveManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun isSignedIn(): Boolean = false

    fun signOut() { /* no-op */ }

    fun storeAccountEmail(email: String, displayName: String?) {
        val prefs = context.getSharedPreferences("mahallu_drive_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("drive_account", email).apply()
    }

    fun storeAuthToken(token: String?) { /* no-op */ }

    suspend fun upload(fileName: String, data: ByteArray): Pair<String?, String?> {
        // Real implementation would push to Drive. For now, return null so caller
        // continues with local-only backup.
        return Pair<String?, String?>(null, null)
    }
}