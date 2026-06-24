package com.mahallu.manager.data.local.dao

import androidx.room.*
import com.mahallu.manager.data.local.entity.SettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {

    @Query("SELECT * FROM settings ORDER BY id DESC LIMIT 1")
    fun getSettings(): Flow<SettingsEntity?>

    @Query("SELECT * FROM settings ORDER BY id DESC LIMIT 1")
    suspend fun getSettingsOnce(): SettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: SettingsEntity): Long

    @Update
    suspend fun updateSettings(settings: SettingsEntity)

    @Query("UPDATE settings SET lastBackupAt = :timestamp WHERE id = (SELECT id FROM settings ORDER BY id DESC LIMIT 1)")
    suspend fun updateLastBackup(timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE settings SET theme = :theme WHERE id = (SELECT id FROM settings ORDER BY id DESC LIMIT 1)")
    suspend fun updateTheme(theme: String)
}
