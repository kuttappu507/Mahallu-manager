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
import feature.reports.feature.reports.R
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
                    "FAMILY" -> Triple(context.getString(R.string.reports_family_register),
                        listOf(context.getString(R.string.reports_header_number), context.getString(R.string.reports_header_house_name), context.getString(R.string.reports_header_address), context.getString(R.string.reports_header_mobile), context.getString(R.string.reports_header_status)),
                        runBlockingFamilyRows())
                    "MEMBER" -> Triple(context.getString(R.string.reports_member_register),
                        listOf(context.getString(R.string.reports_header_member_id), context.getString(R.string.reports_header_name), context.getString(R.string.reports_header_family), context.getString(R.string.reports_header_gender), context.getString(R.string.reports_header_age)),
                        runBlockingMemberRows())
                    "COLLECTION" -> Triple(context.getString(R.string.reports_collection_report),
                        listOf(context.getString(R.string.reports_header_receipt), context.getString(R.string.reports_header_type), context.getString(R.string.reports_header_date), context.getString(R.string.reports_header_amount), context.getString(R.string.reports_header_method)),
                        subRepo.all().map { arrayOf(it.receiptNumber, it.type, it.date.toString(), it.amount.toString(), it.paymentMethod) })
                    "DONATION" -> Triple(context.getString(R.string.reports_donation_report),
                        listOf(context.getString(R.string.reports_header_receipt), context.getString(R.string.reports_header_donor), context.getString(R.string.reports_header_category), context.getString(R.string.reports_header_date), context.getString(R.string.reports_header_amount)),
                        donationRepo.all().map { arrayOf(it.receiptNumber, it.donorName, it.category, it.date.toString(), it.amount.toString()) })
                    "FINANCE" -> Triple(context.getString(R.string.reports_finance_report),
                        listOf(context.getString(R.string.reports_header_type), context.getString(R.string.reports_header_category), context.getString(R.string.reports_header_date), context.getString(R.string.reports_header_amount), context.getString(R.string.reports_header_method), context.getString(R.string.reports_header_description)),
                        runBlockingFinanceRows())
                    "MARRIAGE" -> Triple(context.getString(R.string.reports_marriage_register),
                        listOf(context.getString(R.string.reports_header_reg_no), context.getString(R.string.reports_header_bride), context.getString(R.string.reports_header_groom), context.getString(R.string.reports_header_nikah_date)),
                        marriageRepo.all().map { arrayOf(it.registrationNumber, it.brideName, it.groomName, it.nikahDate.toString()) })
                    "DEATH" -> Triple(context.getString(R.string.reports_death_register),
                        listOf(context.getString(R.string.reports_header_reg_no), context.getString(R.string.reports_header_name), context.getString(R.string.reports_header_father), context.getString(R.string.reports_header_date_of_death)),
                        deathRepo.all().map { arrayOf(it.registrationNumber, it.name, it.fatherName ?: "", it.dateOfDeath.toString()) })
                    else -> Triple(context.getString(R.string.reports_report_fallback), listOf(context.getString(R.string.reports_header_fallback)), listOf(arrayOf("")))
                }

                val file = pdfGenerator.generate(
                    fileName = "${reportType.lowercase()}_report_${System.currentTimeMillis()}.pdf",
                    spec = com.mahallu.manager.feature.reports.pdf.PdfDocumentSpec(
                        title = title,
                        subtitle = context.getString(R.string.reports_generated_subtitle, java.text.SimpleDateFormat("dd MMM yyyy").format(java.util.Date())),
                        lines = listOf(PdfTextLine(context.getString(R.string.reports_mahallu_management_report), sizeSp = 18f)),
                        table = PdfTable(headers, rows.map { it.toList() }),
                        footer = context.getString(R.string.reports_footer_confidential)
                    )
                )
                _state.update { it.copy(isGenerating = false, lastGeneratedPath = file.absolutePath, message = context.getString(R.string.reports_generated_message, file.name)) }
            } catch (t: Throwable) {
                _state.update { it.copy(isGenerating = false, message = context.getString(R.string.reports_failed_message, t.message)) }
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

    private suspend fun runBlockingFinanceRows(): List<Array<String>> {
        return financeRepo.all().map {
            arrayOf(
                it.type,
                it.category,
                it.date.toString(),
                it.amount.toString(),
                it.paymentMethod,
                it.description
            )
        }
    }
}