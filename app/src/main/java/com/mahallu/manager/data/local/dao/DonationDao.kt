package com.mahallu.manager.data.local.dao

import androidx.room.*
import com.mahallu.manager.data.local.entity.DonationEntity
import com.mahallu.manager.data.local.entity.DonationPurpose
import kotlinx.coroutines.flow.Flow

@Dao
interface DonationDao {

    @Query("SELECT * FROM donations ORDER BY donationDate DESC")
    fun getAllDonations(): Flow<List<DonationEntity>>

    @Query("SELECT * FROM donations WHERE id = :id")
    suspend fun getDonationById(id: Long): DonationEntity?

    @Query("SELECT * FROM donations WHERE receiptNumber = :receiptNumber")
    suspend fun getDonationByReceiptNumber(receiptNumber: String): DonationEntity?

    @Query("SELECT * FROM donations WHERE purpose = :purpose ORDER BY donationDate DESC")
    fun getDonationsByPurpose(purpose: DonationPurpose): Flow<List<DonationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDonation(donation: DonationEntity): Long

    @Update
    suspend fun updateDonation(donation: DonationEntity)

    @Delete
    suspend fun deleteDonation(donation: DonationEntity)

    @Query("SELECT SUM(amount) FROM donations")
    fun getTotalDonationsAmount(): Flow<Double?>

    @Query("SELECT SUM(amount) FROM donations WHERE strftime('%m', donationDate/1000, 'unixepoch') = strftime('%m', 'now')")
    fun getCurrentMonthDonations(): Flow<Double?>

    @Query("SELECT * FROM donations WHERE donationDate >= :startDate AND donationDate <= :endDate ORDER BY donationDate DESC")
    fun getDonationsBetweenDates(startDate: Long, endDate: Long): Flow<List<DonationEntity>>

    @Query("SELECT * FROM donations ORDER BY donationDate DESC LIMIT 10")
    fun getRecentDonations(): Flow<List<DonationEntity>>

    @Query("SELECT purpose, SUM(amount) as total FROM donations GROUP BY purpose")
    fun getDonationsByPurposeTotal(): Flow<Map<String, Double>>
}
