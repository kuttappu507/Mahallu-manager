package com.mahallu.manager.domain.repository

import com.mahallu.manager.domain.model.Donation
import kotlinx.coroutines.flow.Flow

interface DonationRepository {
    fun getAllDonations(): Flow<List<Donation>>
    fun getDonationById(id: Int): Flow<Donation?>
    suspend fun insertDonation(donation: Donation)
    suspend fun updateDonation(donation: Donation)
    suspend fun deleteDonation(donation: Donation)
    fun getDonationsByPurpose(purpose: String): Flow<List<Donation>>
    fun getDonationsByDateRange(startDate: Long, endDate: Long): Flow<List<Donation>>
    fun searchDonations(query: String): Flow<List<Donation>>
    suspend fun getTotalDonations(): Double
}
