package com.mahallu.manager.feature.donations

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahallu.manager.core.database.entity.DonationEntity
import com.mahallu.manager.core.database.repository.DonationRepository
import com.mahallu.manager.core.database.repository.SettingsRepository
import com.mahallu.manager.feature.certificates.pdf.PdfGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import feature.donations.feature.donations.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DonationDetailState(
    val donation: DonationEntity? = null,
    val isLoading: Boolean = true,
    val isGenerating: Boolean = false,
    val pdfPath: String? = null,
    val error: String? = null
)

@HiltViewModel
class DonationDetailViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repo: DonationRepository,
    private val settingsRepo: SettingsRepository,
    private val pdfGenerator: PdfGenerator,
    savedStateHandle: androidx.lifecycle.SavedStateHandle
) : ViewModel() {

    private val donationId = savedStateHandle.get<String>("donationId").orEmpty()
    private val _state = MutableStateFlow(DonationDetailState())
    val state: StateFlow<DonationDetailState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val donation = repo.getById(donationId)
            if (donation == null) {
                _state.value = DonationDetailState(isLoading = false, error = context.getString(R.string.donations_error_not_found))
                return@launch
            }
            _state.value = DonationDetailState(donation = donation, isLoading = false)
            generateReceipt()
        }
    }

    fun generateReceipt() {
        val donation = _state.value.donation ?: return
        _state.update { it.copy(isGenerating = true, error = null) }
        viewModelScope.launch {
            val path = generateDonationReceipt(context, pdfGenerator, settingsRepo, donation)?.absolutePath
            _state.update {
                it.copy(
                    isGenerating = false,
                    pdfPath = path,
                    error = if (path == null) context.getString(R.string.donations_pdf_failed) else null
                )
            }
        }
    }
}
