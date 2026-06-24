package com.mahallu.core.database.dao

import androidx.room.*
import com.mahallu.core.database.entity.Settings
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Query("SELECT * FROM settings WHERE id = 1")
    fun getSettings(): Flow<Settings?>

    @Query("SELECT * FROM settings WHERE id = 1")
    suspend fun getSettingsOnce(): Settings?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: Settings): Long

    @Update
    suspend fun updateSettings(settings: Settings)

    @Query("UPDATE settings SET mahalluName = :name, address = :address, phone = :phone, email = :email, logoUri = :logoUri, sealUri = :sealUri, website = :website, subscriptionAmount = :subAmount, yearlySubscriptionAmount = :yearlySubAmount, updatedAt = :updatedAt WHERE id = 1")
    suspend fun updateMahalluSettings(
        name: String,
        address: String?,
        phone: String?,
        email: String?,
        logoUri: String?,
        sealUri: String?,
        website: String?,
        subAmount: Double,
        yearlySubAmount: Double,
        updatedAt: Long
    )
}
