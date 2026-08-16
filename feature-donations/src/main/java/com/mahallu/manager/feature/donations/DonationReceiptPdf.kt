package com.mahallu.manager.feature.donations

import android.content.Context
import com.mahallu.manager.core.database.entity.DonationEntity
import com.mahallu.manager.core.database.repository.SettingsRepository
import com.mahallu.manager.feature.certificates.pdf.Align
import com.mahallu.manager.feature.certificates.pdf.PdfDocumentSpec
import com.mahallu.manager.feature.certificates.pdf.PdfGenerator
import com.mahallu.manager.feature.certificates.pdf.PdfTextLine
import feature.donations.feature.donations.R
import java.io.File

/**
 * Builds the donation receipt PDF from a stored [DonationEntity] so that
 * receipts can be regenerated for existing donations (entry + detail screens).
 */
suspend fun generateDonationReceipt(
    context: Context,
    pdfGenerator: PdfGenerator,
    settingsRepo: SettingsRepository,
    donation: DonationEntity
): File? = try {
    val mahalluName = settingsRepo.getString("mahallu.name", "Mahallu Manager")
    val formattedDate = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault()).format(donation.date)
    val lines = mutableListOf<PdfTextLine>()
    lines += PdfTextLine(mahalluName, sizeSp = 22f, bold = true, align = Align.CENTER)
    lines += PdfTextLine(context.getString(R.string.donations_pdf_receipt_title), sizeSp = 16f, bold = true, align = Align.CENTER)
    lines += PdfTextLine(" ", sizeSp = 8f)
    lines += PdfTextLine(context.getString(R.string.donations_pdf_receipt_no, donation.receiptNumber), sizeSp = 12f, bold = true)
    lines += PdfTextLine(context.getString(R.string.donations_pdf_date, formattedDate), sizeSp = 11f)
    lines += PdfTextLine(" ", sizeSp = 6f)
    lines += PdfTextLine(context.getString(R.string.donations_pdf_received_from), sizeSp = 11f, color = android.graphics.Color.DKGRAY)
    lines += PdfTextLine(donation.donorName, sizeSp = 14f, bold = true)
    if (!donation.donorMobile.isNullOrBlank()) {
        lines += PdfTextLine(context.getString(R.string.donations_pdf_mobile, donation.donorMobile), sizeSp = 11f)
    }
    lines += PdfTextLine(" ", sizeSp = 6f)
    lines += PdfTextLine(context.getString(R.string.donations_pdf_sum_of), sizeSp = 11f)
    lines += PdfTextLine("Rs. ${"%,.2f".format(donation.amount)}", sizeSp = 22f, bold = true, color = android.graphics.Color.parseColor("#FF6B6B"))
    lines += PdfTextLine(context.getString(R.string.donations_pdf_rupees_only, numberToWordsInr(donation.amount)), sizeSp = 10f, color = android.graphics.Color.DKGRAY)
    lines += PdfTextLine(" ", sizeSp = 8f)
    lines += PdfTextLine(context.getString(R.string.donations_pdf_category, context.getString(donationCategoryLabelRes(donation.category))), sizeSp = 11f)
    lines += PdfTextLine(context.getString(R.string.donations_pdf_payment, context.getString(donationPaymentLabelRes(donation.paymentMethod))), sizeSp = 11f)
    if (!donation.purpose.isNullOrBlank()) {
        lines += PdfTextLine(context.getString(R.string.donations_pdf_purpose, donation.purpose), sizeSp = 11f)
    }
    if (!donation.remarks.isNullOrBlank()) {
        lines += PdfTextLine(context.getString(R.string.donations_pdf_remarks, donation.remarks), sizeSp = 11f)
    }
    lines += PdfTextLine(" ", sizeSp = 14f)
    lines += PdfTextLine(context.getString(R.string.donations_pdf_issued_on, java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault()).format(java.util.Date())), sizeSp = 11f, align = Align.RIGHT)
    lines += PdfTextLine(context.getString(R.string.donations_pdf_authorised_signatory), sizeSp = 11f, bold = true, align = Align.RIGHT)

    pdfGenerator.generate(
        fileName = "donation_${donation.receiptNumber}.pdf",
        spec = PdfDocumentSpec(
            title = context.getString(R.string.donations_pdf_receipt_title),
            subtitle = mahalluName,
            lines = lines,
            footer = context.getString(R.string.donations_pdf_footer, mahalluName)
        )
    )
} catch (t: Throwable) {
    null
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
