package com.mahallu.manager

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.mahallu.manager.core.database.repository.SeedData
import com.mahallu.manager.worker.BackupScheduler
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class MahalluApplication : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory
    @Inject lateinit var backupScheduler: BackupScheduler
    @Inject lateinit var seedData: SeedData

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        createNotificationChannels()
        appScope.launch {
            // CRITICAL: Seed default admin user + sample data on first launch
            // so user can actually log in!
            try {
                seedData.seedIfEmpty()
                Timber.i("Database seed completed")
            } catch (t: Throwable) {
                Timber.e(t, "Database seed failed")
            }
            // Schedule daily backup if enabled
            backupScheduler.scheduleIfEnabled()
        }
    }

    private fun createNotificationChannels() {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.createNotificationChannel(
            NotificationChannel(
                CHANNEL_BACKUP,
                "Backups",
                NotificationManager.IMPORTANCE_LOW
            ).apply { description = "Database backup notifications" }
        )
        nm.createNotificationChannel(
            NotificationChannel(
                CHANNEL_GENERAL,
                "General",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = "General notifications" }
        )
    }

    companion object {
        const val CHANNEL_BACKUP = "backup_channel"
        const val CHANNEL_GENERAL = "general_channel"
    }
}