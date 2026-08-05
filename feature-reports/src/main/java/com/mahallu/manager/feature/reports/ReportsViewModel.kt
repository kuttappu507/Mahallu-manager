package com.mahallu.manager.feature.reports

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahallu.manager.core.database.repository.DeathRepository
import com.mahallu.manager.core.database.repository.DonationRepository
import com.mahallu.manager.core.database.repository.FamilyRepository
import com.mahallu.manager.core.database.repository.FinanceRepository
import com.mahallu.manager.core.database.repository.MarriageRepository
import com.mahallu.manager.core.database.repository.MemberRepository
import com.mahallu.manager.core.database.repository.SubscriptionRepository
import com.mahallu.manager.core.database.repository.WelfareRepository
import com.mahallu.manager.feature.reports.pdf.PdfGenerator
import com.mahallu.manager.feature.reports.pdf.PdfTable
import com.mahallu.manager.feature.reports.pdf.PdfTextLine
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class ReportsState(
    val isGenerating: Boolean = false,
    val lastGeneratedPath: String? = null,
    val message: String? = null
)

@HiltViewModel
class ReportsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val pdfGenerator: PdfGenerator,
    private val familyRepo: FamilyRepository,
    private val memberRepo: MemberRepository,
    private val subRepo: SubscriptionRepository,
    private val donationRepo: DonationRepository,
    private val financeRepo: FinanceRepository,
    private val marriageRepo: MarriageRepository,
    private val deathRepo: DeathRepository,
    private val welfareRepo: WelfareRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ReportsState())
    val state: StateFlow<ReportsState> = _state.asStateFlow()

    fun update(transform: (ReportsState) -> ReportsState) {
        _state.update(transform)
    }

    fun generate(reportType: String) {
        _state.update { it.copy(isGenerating = true, message = null) }
        viewModelScope.launch {
            try {
                val (title, headers, rows) = when (reportType) {
                    "FAMILY" -> Triple("Family Register", listOf("Number", "House Name", "Address", "Mobile", "Status"),
                        runBlockingFamilyRows())
                    "MEMBER" -> Triple("Member Register", listOf("Member ID", "Name", "Family", "Gender", "Age"),
                        runBlockingMemberRows())
                    "COLLECTION" -> Triple("Collection Report", listOf("Receipt", "Type", "Date", "Amount", "Method"),
                        subRepo.all().map { arrayOf(it.receiptNumber, it.type, it.date.toString(), it.amount.toString(), it.paymentMethod) })
                    "DONATION" -> Triple("Donation Report", listOf("Receipt", "Donor", "Category", "Date", "Amount"),
                        donationRepo.all().map { arrayOf(it.receiptNumber, it.donorName, it.category, it.date.toString(), it.amount.toString()) })
                    "MARRIAGE" -> Triple("Marriage Register", listOf("Reg #", "Bride", "Groom", "Nikah Date"),
                        marriageRepo.all().map { arrayOf(it.registrationNumber, it.brideName, it.groomName, it.nikahDate.toString()) })
                    "DEATH" -> Triple("Death Register", listOf("Reg #", "Name", "Father", "Date of Death"),
                        deathRepo.all().map { arrayOf(it.registrationNumber, it.name, it.fatherName ?: "", it.dateOfDeath.toString()) })
                    else -> Triple("Report", listOf("Col 1"), listOf(arrayOf("")))
                }

                val file = pdfGenerator.generate(
                    fileName = "${reportType.lowercase()}_report_${System.currentTimeMillis()}.pdf",
                    spec = com.mahallu.manager.feature.reports.pdf.PdfDocumentSpec(
                        title = title,
                        subtitle = "Generated ${java.text.SimpleDateFormat("dd MMM yyyy").format(java.util.Date())}",
                        lines = listOf(PdfTextLine("Mahallu Management Report", sizeSp = 18f)),
                        table = PdfTable(headers, rows.map { it.toList() }),
                        footer = "Mahallu Manager • Confidential"
                    )
                )
                _state.update { it.copy(isGenerating = false, lastGeneratedPath = file.absolutePath, message = "Generated ${file.name}") }
            } catch (t: Throwable) {
                _state.update { it.copy(isGenerating = false, message = "Failed: ${t.message}") }
            }
        }
    }

    private suspend fun runBlockingFamilyRows(): List<Array<String>> {
        val page = familyRepo.search("")
        return page.map { arrayOf(it.familyNumber, it.houseName, it.address, it.primaryMobile ?: "", it.status) }
    }

    private suspend fun runBlockingMemberRows(): List<Array<String>> {
        val members = memberRepo.search("")
        val families = familyRepo.search("").associateBy { it.id }
        return members.map { m ->
            arrayOf(
                m.memberNumber,
                m.name,
                families[m.familyId]?.houseName ?: "",
                m.gender,
                ((System.currentTimeMillis() - m.dateOfBirth) / (365L * 24 * 60 * 60 * 1000)).toString()
            )
        }
    }
}