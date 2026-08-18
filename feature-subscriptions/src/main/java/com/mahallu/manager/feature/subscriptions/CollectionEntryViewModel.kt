package com.mahallu.manager.feature.subscriptions

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahallu.manager.core.database.entity.FamilyEntity
import com.mahallu.manager.core.database.entity.MemberEntity
import com.mahallu.manager.core.database.entity.SubscriptionEntity
import com.mahallu.manager.core.database.repository.FamilyRepository
import com.mahallu.manager.core.database.repository.MemberRepository
import com.mahallu.manager.core.database.repository.SettingsRepository
import com.mahallu.manager.core.database.repository.SubscriptionRepository
import com.mahallu.manager.core.util.IdGenerator
import com.mahallu.manager.feature.certificates.pdf.PdfGenerator
import feature.subscriptions.feature.subscriptions.R
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class CollectionEntryState(
    val families: List<FamilyEntity> = emptyList(),
    val members: List<MemberEntity> = emptyList(),
    val selectedFamilyId: String = "",
    val selectedMemberId: String = "",
    val selectedFamilyName: String = "",
    val selectedMemberName: String = "",
    val type: String = "MONTHLY",
    val amount: String = "500",
    val paymentMethod: String = "CASH",
    val remarks: String = "",
    val date: Long = System.currentTimeMillis(),
    val isSaving: Boolean = false,
    val saved: Boolean = false,
    val pdfPath: String? = null,
    val pdfFailed: Boolean = false,
    val receiptNumber: String = "",
    val error: String? = null
)

@HiltViewModel
class CollectionEntryViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val subRepo: SubscriptionRepository,
    private val familyRepo: FamilyRepository,
    private val memberRepo: MemberRepository,
    private val settingsRepo: SettingsRepository,
    private val pdfGenerator: PdfGenerator,
    savedStateHandle: androidx.lifecycle.SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(CollectionEntryState(receiptNumber = IdGenerator.receiptNumber()))
    val state: StateFlow<CollectionEntryState> = _state

    private var savedEntity: SubscriptionEntity? = null

    init {
        viewModelScope.launch {
            familyRepo.observeAll().collect { fams ->
                _state.update { it.copy(families = fams) }
            }
        }
        viewModelScope.launch {
            val defaultAmount = settingsRepo.getString("default_subscription_amount", "500")
            _state.update { it.copy(amount = defaultAmount) }
        }
        val preMemberId = savedStateHandle.get<String>("memberId").orEmpty()
        if (preMemberId.isNotBlank()) {
            viewModelScope.launch {
                val m = memberRepo.getById(preMemberId)
                if (m != null) {
                    val fam = m.familyId.takeIf { it.isNotBlank() }?.let { familyRepo.getById(it) }
                    _state.update {
                        it.copy(
                            selectedMemberId = m.id,
                            selectedMemberName = m.name,
                            selectedFamilyId = m.familyId,
                            selectedFamilyName = fam?.houseName.orEmpty()
                        )
                    }
                }
            }
        }
    }

    fun selectMember(memberId: String) {
        viewModelScope.launch {
            val m = memberRepo.getById(memberId)
            val fam = m?.familyId?.takeIf { it.isNotBlank() }?.let { familyRepo.getById(it) }
            _state.update {
                it.copy(
                    selectedMemberId = m?.id.orEmpty(),
                    selectedMemberName = m?.name.orEmpty(),
                    selectedFamilyId = m?.familyId.orEmpty(),
                    selectedFamilyName = fam?.houseName.orEmpty()
                )
            }
        }
    }

    fun selectFamily(familyId: String) {
        viewModelScope.launch {
            val fam = familyRepo.getById(familyId)
            memberRepo.observeByFamily(familyId).collect { list ->
                _state.update {
                    it.copy(
                        members = list,
                        selectedFamilyId = familyId,
                        selectedFamilyName = fam?.houseName.orEmpty(),
                        selectedMemberId = list.firstOrNull()?.id.orEmpty(),
                        selectedMemberName = list.firstOrNull()?.name.orEmpty()
                    )
                }
                return@collect
            }
        }
    }

    fun update(transform: (CollectionEntryState) -> CollectionEntryState) {
        _state.update(transform)
    }

    fun save() {
        val s = _state.value
        val amount = s.amount.toDoubleOrNull()
        if (s.selectedFamilyId.isBlank() && s.selectedMemberId.isBlank()) {
            _state.update { it.copy(error = context.getString(R.string.collection_error_no_selection)) }
            return
        }
        if (amount == null || amount <= 0) {
            _state.update { it.copy(error = context.getString(R.string.collection_error_invalid_amount)) }
            return
        }
        _state.update { it.copy(isSaving = true, error = null) }
        viewModelScope.launch {
            val cal = Calendar.getInstance().apply { timeInMillis = s.date }
            val entity = SubscriptionEntity(
                id = IdGenerator.newId(),
                receiptNumber = s.receiptNumber,
                familyId = s.selectedFamilyId,
                memberId = s.selectedMemberId.ifBlank { null },
                type = s.type,
                amount = amount,
                date = s.date,
                paymentMethod = s.paymentMethod,
                remarks = s.remarks.trim().ifBlank { null },
                receivedBy = null,
                year = cal.get(Calendar.YEAR),
                month = cal.get(Calendar.MONTH) + 1
            )
            subRepo.save(entity)
            savedEntity = entity

            // Generate the PDF receipt right away so the user can print/share it
            generateReceiptPdf(entity, s.selectedFamilyName, s.selectedMemberName)
        }
    }

    /**
     * Re-runs PDF generation for the already-saved collection (used when the
     * first attempt failed). Never re-inserts a duplicate row.
     */
    fun retry() {
        val entity = savedEntity ?: return
        _state.update { it.copy(isSaving = true, error = null) }
        viewModelScope.launch { generateReceiptPdf(entity, _state.value.selectedFamilyName, _state.value.selectedMemberName) }
    }

    private suspend fun generateReceiptPdf(entity: SubscriptionEntity, familyName: String, memberName: String) {
        val pdfPath = generateSubscriptionReceipt(
            context = context,
            pdfGenerator = pdfGenerator,
            settingsRepo = settingsRepo,
            subscription = entity,
            familyName = familyName,
            memberName = memberName
        )?.absolutePath

        _state.update {
            it.copy(
                isSaving = false,
                saved = true,
                pdfPath = pdfPath,
                pdfFailed = pdfPath == null
            )
        }
    }
}
