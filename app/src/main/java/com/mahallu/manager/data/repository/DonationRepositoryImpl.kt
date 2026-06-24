package com.mahallu.manager.data.repository

import com.mahallu.manager.data.local.dao.DonationDao
import com.mahallu.manager.domain.model.Donation
import com.mahallu.manager.domain.repository.DonationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DonationRepositoryImpl @Inject constructor(
    private val donationDao: DonationDao
) : DonationRepository {

    override fun getAllDonations(): Flow<List<Donation>> {
        return donationDao.getAllDonations().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getDonationById(id: Int): Flow<Donation?> {
        return donationDao.getDonationById(id).map { it?.toDomainModel() }
    }

    override suspend fun insertDonation(donation: Donation) {
        donationDao.insertDonation(donation.toEntity())
    }

    override suspend fun updateDonation(donation: Donation) {
        donationDao.updateDonation(donation.toEntity())
    }

    override suspend fun deleteDonation(donation: Donation) {
        donationDao.deleteDonation(donation.toEntity())
    }

    override fun getDonationsByPurpose(purpose: String): Flow<List<Donation>> {
        return donationDao.getDonationsByPurpose(purpose).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getDonationsByDateRange(startDate: Long, endDate: Long): Flow<List<Donation>> {
        return donationDao.getDonationsByDateRange(startDate, endDate).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun searchDonations(query: String): Flow<List<Donation>> {
        return donationDao.searchDonations("%$query%").map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun getTotalDonations(): Double {
        return donationDao.getTotalDonations()
    }
}
