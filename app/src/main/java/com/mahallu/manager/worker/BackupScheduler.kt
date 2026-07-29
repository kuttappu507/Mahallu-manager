package com.mahallu.manager.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.mahallu.manager.core.database.repository.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackupScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settings: SettingsRepository
) {
    companion object {
        const val UNIQUE_DAILY = "mahallu-backup-daily"
        const val UNIQUE_MANUAL = "mahallu-backup-manual"
    }

    suspend fun scheduleIfEnabled() {
        val enabled = settings.getBoolean("backup.auto_enabled", true)
        if (!enabled) {
            cancel()
            return
        }
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()
        val request = PeriodicWorkRequestBuilder<BackupWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .setInitialDelay(2, TimeUnit.HOURS)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            UNIQUE_DAILY,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    fun scheduleManualNow() {
        val request = OneTimeWorkRequestBuilder<BackupWorker>()
            .setInputData(androidx.work.Data.Builder().putBoolean(BackupWorker.KEY_MANUAL, true).build())
            .build()
        WorkManager.getInstance(context).enqueue(request)
    }

    fun cancel() {
        WorkManager.getInstance(context).cancelUniqueWork(UNIQUE_DAILY)
    }
}