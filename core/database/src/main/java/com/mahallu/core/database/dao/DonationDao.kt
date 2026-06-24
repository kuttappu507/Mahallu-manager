package com.mahallu.core.database.dao

import androidx.room.*
import com.mahallu.core.database.entity.Donation
import com.mahallu.core.database.entity.DonationPurpose
import kotlinx.coroutines.flow.Flow

@Dao
interface DonationDao {
    @Query("SELECT * FROM donations ORDER BY date DESC")
    fun getAllDonations(): Flow<List<Donation>>
    
    @Query("SELECT * FROM donations WHERE id = :id")
    suspend fun getDonationById(id: Long): Donation?
    
    @Query("SELECT * FROM donations WHERE purpose = :purpose ORDER BY date DESC")
    fun getDonationsByPurpose(purpose: DonationPurpose): Flow<List<Donation>>
    
    @Query("SELECT * FROM donations WHERE year(date/1000) = :year ORDER BY date DESC")
    fun getDonationsByYear(year: Int): Flow<List<Donation>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(donation: Donation): Long
    
    @Update
    suspend fun update(donation: Donation)
    
    @Delete
    suspend fun delete(donation: Donation)
    
    @Query("SELECT SUM(amount) FROM donations WHERE purpose = :purpose AND year(date/1000) = :year")
    fun getTotalByPurposeAndYear(purpose: DonationPurpose, year: Int): Flow<Double?>
    
    @Query("SELECT SUM(amount) FROM donations WHERE year(date/1000) = :year")
    fun getTotalDonationsByYear(year: Int): Flow<Double?>
}
