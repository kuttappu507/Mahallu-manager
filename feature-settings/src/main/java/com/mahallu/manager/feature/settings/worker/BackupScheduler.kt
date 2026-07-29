package com.mahallu.manager.feature.settings.worker

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackupScheduler @Inject constructor() {
    fun scheduleIfEnabled() { /* no-op in feature module */ }
    fun cancel() { /* no-op */ }
}
