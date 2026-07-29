package com.mahallu.manager.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.mahallu.manager.backup.BackupManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

@HiltWorker
class BackupWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val backupManager: BackupManager
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val isManual = inputData.getBoolean(KEY_MANUAL, false)
        Timber.i("BackupWorker started (manual=$isManual)")
        try {
            val outcome = backupManager.performBackup(isManual = isManual)
            if (outcome.success) {
                Result.success(Data.Builder().putString("backup_id", outcome.backupId).build())
            } else {
                Result.failure(Data.Builder().putString("error", outcome.message).build())
            }
        } catch (t: Throwable) {
            Timber.e(t, "BackupWorker failed")
            Result.failure(Data.Builder().putString("error", t.message ?: "Unknown").build())
        }
    }

    companion object {
        const val KEY_MANUAL = "manual"
    }
}