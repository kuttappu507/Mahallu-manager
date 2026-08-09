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
import com.mahallu.manager.feature.certificates.pdf.Align
import com.mahallu.manager.feature.certificates.pdf.PdfGenerator
import com.mahallu.manager.feature.certificates.pdf.PdfTextLine
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

    init {
        viewModelScope.launch {
            familyRepo.observeAll().collect { fams ->
                _state.update { it.copy(families = fams) }
            }
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

            // Generate the PDF receipt right away
            val pdfPath = try {
                val mahalluName = settingsRepo.getString("mahallu.name", "Mahallu Manager")
                val formattedDate = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault()).format(s.date)
                val lines = mutableListOf<PdfTextLine>()
                lines += PdfTextLine(mahalluName, sizeSp = 22f, bold = true, align = Align.CENTER)
                lines += PdfTextLine(context.getString(R.string.pdf_subscription_receipt), sizeSp = 16f, bold = true, align = Align.CENTER)
                lines += PdfTextLine(" ", sizeSp = 8f)
                lines += PdfTextLine(context.getString(R.string.pdf_receipt_no, s.receiptNumber), sizeSp = 12f, bold = true)
                lines += PdfTextLine(context.getString(R.string.pdf_date, formattedDate), sizeSp = 11f)
                lines += PdfTextLine(" ", sizeSp = 6f)
                lines += PdfTextLine(context.getString(R.string.pdf_received_from), sizeSp = 11f, color = android.graphics.Color.DKGRAY)
                val family = s.selectedFamilyName.ifBlank { s.selectedMemberName }
                lines += PdfTextLine(family.ifBlank { context.getString(R.string.pdf_family_member_placeholder) }, sizeSp = 14f, bold = true)
                if (s.selectedMemberName.isNotBlank() && s.selectedMemberName != s.selectedFamilyName) {
                    lines += PdfTextLine(context.getString(R.string.pdf_member, s.selectedMemberName), sizeSp = 11f)
                }
                lines += PdfTextLine(" ", sizeSp = 6f)
                lines += PdfTextLine(context.getString(R.string.pdf_subscription_type, s.type), sizeSp = 11f)
                lines += PdfTextLine(context.getString(R.string.pdf_the_sum_of), sizeSp = 11f)
                lines += PdfTextLine("Rs. ${"%,.2f".format(amount)}", sizeSp = 22f, bold = true, color = android.graphics.Color.parseColor("#3B4FB8"))
                lines += PdfTextLine("(", sizeSp = 10f, color = android.graphics.Color.DKGRAY)
                lines += PdfTextLine(context.getString(R.string.pdf_payment, s.paymentMethod), sizeSp = 11f)
                if (s.remarks.isNotBlank()) {
                    lines += PdfTextLine(context.getString(R.string.pdf_remarks, s.remarks.trim()), sizeSp = 11f)
                }
                lines += PdfTextLine(" ", sizeSp = 14f)
                lines += PdfTextLine(context.getString(R.string.pdf_issued_on, java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault()).format(java.util.Date())), sizeSp = 11f, align = Align.RIGHT)
                lines += PdfTextLine(context.getString(R.string.pdf_authorised_signatory), sizeSp = 11f, bold = true, align = Align.RIGHT)

                val file = pdfGenerator.generate(
                    fileName = "subscription_${s.receiptNumber}.pdf",
                    spec = com.mahallu.manager.feature.certificates.pdf.PdfDocumentSpec(
                        title = context.getString(R.string.pdf_subscription_receipt),
                        subtitle = mahalluName,
                        lines = lines,
                        footer = context.getString(R.string.pdf_footer, mahalluName)
                    )
                )
                file.absolutePath
            } catch (t: Throwable) {
                null
            }

            _state.update { it.copy(isSaving = false, saved = true, pdfPath = pdfPath) }
        }
    }
}
