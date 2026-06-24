package com.mahallu.core.database.dao

import androidx.room.*
import com.mahallu.core.database.entity.Donation
import com.mahallu.core.database.entity.DonationPurpose
import kotlinx.coroutines.flow.Flow

@Dao
interface DonationDao {
    @Query("SELECT * FROM donations ORDER BY date DESC")
    fun getAllDonations(): Flow<List<Donation>>

    @Query("SELECT * FROM donations WHERE familyId = :familyId ORDER BY date DESC")
    fun getDonationsByFamily(familyId: Long): Flow<List<Donation>>

    @Query("SELECT * FROM donations WHERE memberId = :memberId ORDER BY date DESC")
    fun getDonationsByMember(memberId: Long): Flow<List<Donation>>

    @Query("SELECT * FROM donations WHERE id = :id")
    suspend fun getDonationById(id: Long): Donation?

    @Query("SELECT * FROM donations WHERE purpose = :purpose ORDER BY date DESC")
    fun getDonationsByPurpose(purpose: DonationPurpose): Flow<List<Donation>>

    @Query("SELECT SUM(amount) FROM donations WHERE purpose = :purpose AND date BETWEEN :startDate AND :endDate")
    suspend fun getTotalDonationsByPurpose(purpose: DonationPurpose, startDate: Long, endDate: Long): Double?

    @Query("SELECT SUM(amount) FROM donations WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getTotalDonations(startDate: Long, endDate: Long): Double?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDonation(donation: Donation): Long

    @Update
    suspend fun updateDonation(donation: Donation)

    @Delete
    suspend fun deleteDonation(donation: Donation)
}
