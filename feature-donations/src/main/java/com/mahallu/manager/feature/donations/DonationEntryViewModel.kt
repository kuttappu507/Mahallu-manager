package com.mahallu.manager.feature.donations

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahallu.manager.core.database.entity.DonationEntity
import com.mahallu.manager.core.database.repository.DonationRepository
import com.mahallu.manager.core.database.repository.SettingsRepository
import com.mahallu.manager.core.util.IdGenerator
import com.mahallu.manager.feature.certificates.pdf.Align
import com.mahallu.manager.feature.certificates.pdf.PdfGenerator
import com.mahallu.manager.feature.certificates.pdf.PdfTextLine
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
    val pdfPath: String? = null,
    val error: String? = null
)

@HiltViewModel
class DonationEntryViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repo: DonationRepository,
    private val settingsRepo: SettingsRepository,
    private val pdfGenerator: PdfGenerator
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

            // Generate the PDF receipt right away so the user can print/share it
            val pdfPath = try {
                val mahalluName = settingsRepo.getString("mahallu.name", "Mahallu Manager")
                val formattedDate = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault()).format(s.date)
                val lines = mutableListOf<PdfTextLine>()
                lines += PdfTextLine(mahalluName, sizeSp = 22f, bold = true, align = Align.CENTER)
                lines += PdfTextLine("Donation Receipt", sizeSp = 16f, bold = true, align = Align.CENTER)
                lines += PdfTextLine(" ", sizeSp = 8f)
                lines += PdfTextLine("Receipt No: ${s.receiptNumber}", sizeSp = 12f, bold = true)
                lines += PdfTextLine("Date: $formattedDate", sizeSp = 11f)
                lines += PdfTextLine(" ", sizeSp = 6f)
                lines += PdfTextLine("Received from:", sizeSp = 11f, color = android.graphics.Color.DKGRAY)
                lines += PdfTextLine(s.donorName.trim(), sizeSp = 14f, bold = true)
                if (s.donorMobile.isNotBlank()) {
                    lines += PdfTextLine("Mobile: ${s.donorMobile.trim()}", sizeSp = 11f)
                }
                lines += PdfTextLine(" ", sizeSp = 6f)
                lines += PdfTextLine("The sum of", sizeSp = 11f)
                lines += PdfTextLine("Rs. ${"%,.2f".format(amount)}", sizeSp = 22f, bold = true, color = android.graphics.Color.parseColor("#FF6B6B"))
                lines += PdfTextLine("(${numberToWordsInr(amount)} rupees only)", sizeSp = 10f, color = android.graphics.Color.DKGRAY)
                lines += PdfTextLine(" ", sizeSp = 8f)
                lines += PdfTextLine("Category: ${s.category}", sizeSp = 11f)
                lines += PdfTextLine("Payment: ${s.paymentMethod}", sizeSp = 11f)
                if (s.purpose.isNotBlank()) {
                    lines += PdfTextLine("Purpose: ${s.purpose.trim()}", sizeSp = 11f)
                }
                if (s.remarks.isNotBlank()) {
                    lines += PdfTextLine("Remarks: ${s.remarks.trim()}", sizeSp = 11f)
                }
                lines += PdfTextLine(" ", sizeSp = 14f)
                lines += PdfTextLine("Issued on: ${java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault()).format(java.util.Date())}", sizeSp = 11f, align = Align.RIGHT)
                lines += PdfTextLine("Authorised Signatory", sizeSp = 11f, bold = true, align = Align.RIGHT)

                val file = pdfGenerator.generate(
                    fileName = "donation_${s.receiptNumber}.pdf",
                    spec = com.mahallu.manager.feature.certificates.pdf.PdfDocumentSpec(
                        title = "Donation Receipt",
                        subtitle = mahalluName,
                        lines = lines,
                        footer = "$mahalluName • Generated by Mahallu Manager"
                    )
                )
                file.absolutePath
            } catch (t: Throwable) {
                null
            }

            _state.update { it.copy(isSaving = false, saved = true, pdfPath = pdfPath) }
        }
    }

    private fun numberToWordsInr(amount: Double): String {
        val whole = amount.toLong()
        val paise = ((amount - whole) * 100).toLong()
        val w = convertNumberToWords(whole)
        return if (paise > 0) "$w and $paise paise" else w
    }

    private fun convertNumberToWords(n: Long): String {
        if (n == 0L) return "zero"
        val ones = arrayOf("", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine",
            "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eighteen", "nineteen")
        val tens = arrayOf("", "", "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety")
        fun twoDigits(num: Long): String {
            return if (num < 20) ones[num.toInt()]
            else tens[(num / 10).toInt()] + (if (num % 10 != 0L) " " + ones[(num % 10).toInt()] else "")
        }
        fun threeDigits(num: Long): String {
            val h = num / 100
            val r = num % 100
            return (if (h > 0) "${ones[h.toInt()]} hundred" + (if (r > 0) " " else "") else "") + (if (r > 0) twoDigits(r) else "")
        }
        val parts = mutableListOf<String>()
        var v = n
        val units = arrayOf("", "thousand", "lakh", "crore")
        var idx = 0
        while (v > 0 && idx < units.size) {
            val chunk = v % 1000
            if (chunk > 0) {
                val s = threeDigits(chunk)
                parts.add(0, if (units[idx].isNotEmpty()) "$s ${units[idx]}" else s)
            }
            v /= 1000
            idx++
        }
        return parts.joinToString(" ")
    }
}
