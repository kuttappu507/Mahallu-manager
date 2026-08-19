package com.mahallu.manager

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.content.res.ResourcesCompat
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.mahallu.manager.core.database.repository.SeedData
import com.mahallu.manager.core.ui.R as CoreUiR
import com.mahallu.manager.worker.BackupScheduler
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class MahalluApplication : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory
    @Inject lateinit var backupScheduler: BackupScheduler
    @Inject lateinit var seedData: SeedData

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _appReady = MutableStateFlow(false)
    val appReady: StateFlow<Boolean> = _appReady.asStateFlow()

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
            // Keep all first-run preparation off the main thread. In particular,
            // seedIfEmpty() may create the Room database and hash default users.
            prewarmFonts()
            try {
                seedData.seedIfEmpty()
                Timber.i("Database seed completed")
            } catch (t: Throwable) {
                Timber.e(t, "Database seed failed")
            }

            // The UI is ready as soon as the data required for login is ready.
            // Backup scheduling is independent and must not hold the splash/login
            // transition open.
            _appReady.value = true
            runCatching { backupScheduler.scheduleIfEnabled() }
                .onFailure { Timber.e(it, "Backup scheduling failed") }
        }
    }

    private fun prewarmFonts() {
        runCatching {
            val fontResIds = intArrayOf(
                CoreUiR.font.sora_600, CoreUiR.font.sora_700,
                CoreUiR.font.manrope_400, CoreUiR.font.manrope_500, CoreUiR.font.manrope_600,
                CoreUiR.font.manrope_700, CoreUiR.font.manrope_800,
                CoreUiR.font.gayathri_100, CoreUiR.font.gayathri_400, CoreUiR.font.gayathri_700
            )
            fontResIds.forEach { ResourcesCompat.getFont(this, it) }
        }
    }

    private fun createNotificationChannels() {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.createNotificationChannel(
            NotificationChannel(
                CHANNEL_BACKUP,
                getString(R.string.notification_channel_backup),
                NotificationManager.IMPORTANCE_LOW
            ).apply { description = getString(R.string.notification_channel_backup_desc) }
        )
        nm.createNotificationChannel(
            NotificationChannel(
                CHANNEL_GENERAL,
                getString(R.string.notification_channel_general),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = getString(R.string.notification_channel_general_desc) }
        )
    }

    companion object {
        const val CHANNEL_BACKUP = "backup_channel"
        const val CHANNEL_GENERAL = "general_channel"
    }
}