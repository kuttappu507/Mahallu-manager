package com.mahallu.core.database.dao

import androidx.room.*
import com.mahallu.core.database.entity.Settings
import com.mahallu.core.database.entity.BackupLog
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Query("SELECT * FROM settings WHERE id = 0")
    fun getSettings(): Flow<Settings?>
    
    @Query("SELECT * FROM settings WHERE id = 0")
    suspend fun getSettingsOnce(): Settings?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(settings: Settings): Long
    
    @Update
    suspend fun update(settings: Settings)
}

@Dao
interface BackupDao {
    @Query("SELECT * FROM backup_logs ORDER BY createdAt DESC LIMIT 30")
    fun getBackupLogs(): Flow<List<BackupLog>>
    
    @Query("SELECT * FROM backup_logs WHERE id = :id")
    suspend fun getBackupLogById(id: Long): BackupLog?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: BackupLog): Long
    
    @Update
    suspend fun update(log: BackupLog)
    
    @Delete
    suspend fun delete(log: BackupLog)
    
    @Query("DELETE FROM backup_logs WHERE status = 'COMPLETED' ORDER BY createdAt ASC LIMIT (SELECT COUNT(*) - 30 FROM backup_logs WHERE status = 'COMPLETED')")
    suspend fun pruneOldBackups()
}
