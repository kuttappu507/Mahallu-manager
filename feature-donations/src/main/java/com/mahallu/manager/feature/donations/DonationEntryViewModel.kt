package com.mahallu.manager.feature.donations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahallu.manager.core.database.entity.DonationEntity
import com.mahallu.manager.core.database.repository.DonationRepository
import com.mahallu.manager.core.util.IdGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DonationEntryState(
    val receiptNumber: String = IdGenerator.donationReceipt(),
    val donorName: String = "",
    val donorMobile: String = "",
    val amount: String = "",
    val category: String = "GENERAL",
    val purpose: String = "",
    val paymentMethod: String = "CASH",
    val remarks: String = "",
    val date: Long = System.currentTimeMillis(),
    val isSaving: Boolean = false,
    val saved: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class DonationEntryViewModel @Inject constructor(
    private val repo: DonationRepository
) : ViewModel() {
    private val _state = MutableStateFlow(DonationEntryState())
    val state: StateFlow<DonationEntryState> = _state.asStateFlow()

    fun update(transform: (DonationEntryState) -> DonationEntryState) {
        _state.update(transform)
    }

    fun save() {
        val s = _state.value
        val amount = s.amount.toDoubleOrNull()
        if (s.donorName.isBlank()) {
            _state.update { it.copy(error = "Donor name is required") }
            return
        }
        if (amount == null || amount <= 0) {
            _state.update { it.copy(error = "Enter a valid amount") }
            return
        }
        _state.update { it.copy(isSaving = true, error = null) }
        viewModelScope.launch {
            val entity = DonationEntity(
                id = IdGenerator.newId(),
                receiptNumber = s.receiptNumber,
                donorName = s.donorName.trim(),
                donorMobile = s.donorMobile.trim().ifBlank { null },
                amount = amount,
                category = s.category,
                purpose = s.purpose.trim().ifBlank { null },
                date = s.date,
                paymentMethod = s.paymentMethod,
                remarks = s.remarks.trim().ifBlank { null }
            )
            repo.save(entity)
            _state.update { it.copy(isSaving = false, saved = true) }
        }
    }
}