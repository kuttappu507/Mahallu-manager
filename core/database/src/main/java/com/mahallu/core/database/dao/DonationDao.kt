package com.mahallu.core.database.dao

import androidx.room.*
import com.mahallu.core.database.entity.Donation
import com.mahallu.core.database.entity.DonationPurpose
import kotlinx.coroutines.flow.Flow

@Dao
interface DonationDao {
    @Query("SELECT * FROM donations ORDER BY date DESC")
    fun getAllDonations(): Flow<List<Donation>>

    @Query("SELECT * FROM donations WHERE purpose = :purpose ORDER BY date DESC")
    fun getDonationsByPurpose(purpose: DonationPurpose): Flow<List<Donation>>

    @Query("SELECT * FROM donations WHERE familyId = :familyId ORDER BY date DESC")
    fun getDonationsByFamily(familyId: Long): Flow<List<Donation>>

    @Query("SELECT * FROM donations WHERE memberId = :memberId ORDER BY date DESC")
    fun getDonationsByMember(memberId: Long): Flow<List<Donation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDonation(donation: Donation): Long

    @Update
    suspend fun updateDonation(donation: Donation)

    @Delete
    suspend fun deleteDonation(donation: Donation)

    @Query("SELECT SUM(amount) FROM donations WHERE purpose = :purpose AND strftime('%Y', date) = :year")
    suspend fun getTotalDonationsByPurposeAndYear(purpose: DonationPurpose, year: Int): Double?

    @Query("SELECT SUM(amount) FROM donations WHERE strftime('%Y', date) = :year")
    suspend fun getTotalDonationsByYear(year: Int): Double?

    @Query("SELECT SUM(amount) FROM donations WHERE strftime('%m', date) = :month AND strftime('%Y', date) = :year")
    suspend fun getTotalDonationsByMonth(month: String, year: Int): Double?
}
