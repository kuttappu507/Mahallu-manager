package com.mahallu.core.database.dao

import androidx.room.*
import com.mahallu.core.database.entity.Settings
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Query("SELECT * FROM settings WHERE id = 1")
    fun getSettings(): Flow<Settings?>

    @Query("SELECT * FROM settings WHERE id = 1")
    suspend fun getSettingsSync(): Settings?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: Settings)

    @Update
    suspend fun updateSettings(settings: Settings)

    @Query("UPDATE settings SET mahalluName = :name, address = :address, phone = :phone, email = :email, logoUri = :logoUri, sealUri = :sealUri, updatedAt = :updatedAt WHERE id = 1")
    suspend fun updateMahalluInfo(name: String, address: String?, phone: String?, email: String?, logoUri: String?, sealUri: String?, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE settings SET lastBackupDate = :lastBackupDate WHERE id = 1")
    suspend fun updateLastBackupDate(lastBackupDate: Long)
}
