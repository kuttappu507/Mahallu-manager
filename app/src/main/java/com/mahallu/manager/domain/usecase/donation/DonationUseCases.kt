package com.mahallu.manager.domain.usecase.donation

import com.mahallu.manager.domain.model.Donation
import com.mahallu.manager.domain.repository.DonationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllDonationsUseCase @Inject constructor(
    private val repository: DonationRepository
) {
    operator fun invoke(): Flow<List<Donation>> {
        return repository.getAllDonations()
    }
}

class GetDonationByIdUseCase @Inject constructor(
    private val repository: DonationRepository
) {
    suspend operator fun invoke(id: Int): Donation? {
        return repository.getDonationById(id)
    }
}

class SaveDonationUseCase @Inject constructor(
    private val repository: DonationRepository
) {
    suspend operator fun invoke(donation: Donation) {
        if (donation.id == 0) {
            repository.insertDonation(donation)
        } else {
            repository.updateDonation(donation)
        }
    }
}

class DeleteDonationUseCase @Inject constructor(
    private val repository: DonationRepository
) {
    suspend operator fun invoke(donation: Donation) {
        repository.deleteDonation(donation)
    }
}

class SearchDonationsUseCase @Inject constructor(
    private val repository: DonationRepository
) {
    operator fun invoke(query: String): Flow<List<Donation>> {
        return repository.searchDonations(query)
    }
}

class GetDonationsByCategoryUseCase @Inject constructor(
    private val repository: DonationRepository
) {
    operator fun invoke(category: String): Flow<List<Donation>> {
        return repository.getDonationsByCategory(category)
    }
}

class GetTotalDonationsUseCase @Inject constructor(
    private val repository: DonationRepository
) {
    suspend operator fun invoke(): Double {
        return repository.getTotalDonations()
    }
}

class GetMonthlyDonationsUseCase @Inject constructor(
    private val repository: DonationRepository
) {
    suspend operator fun invoke(year: Int, month: Int): Double {
        return repository.getMonthlyDonations(year, month)
    }
}
