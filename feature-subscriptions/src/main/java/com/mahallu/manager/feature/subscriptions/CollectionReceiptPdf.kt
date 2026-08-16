package com.mahallu.manager.feature.subscriptions

import android.content.Context
import com.mahallu.manager.core.database.entity.SubscriptionEntity
import com.mahallu.manager.core.database.repository.SettingsRepository
import com.mahallu.manager.feature.certificates.pdf.Align
import com.mahallu.manager.feature.certificates.pdf.PdfDocumentSpec
import com.mahallu.manager.feature.certificates.pdf.PdfGenerator
import com.mahallu.manager.feature.certificates.pdf.PdfTextLine
import feature.subscriptions.feature.subscriptions.R
import java.io.File

/**
 * Builds the subscription-collection receipt PDF from a stored [SubscriptionEntity]
 * so that receipts can be regenerated for existing collections (entry + detail screens).
 *
 * @param familyName resolved house/family name (fallback: [memberName])
 * @param memberName resolved member name, shown as an extra line when it differs from the family name
 */
suspend fun generateSubscriptionReceipt(
    context: Context,
    pdfGenerator: PdfGenerator,
    settingsRepo: SettingsRepository,
    subscription: SubscriptionEntity,
    familyName: String,
    memberName: String
): File? = try {
    val mahalluName = settingsRepo.getString("mahallu.name", "Mahallu Manager")
    val formattedDate = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault()).format(subscription.date)
    val lines = mutableListOf<PdfTextLine>()
    lines += PdfTextLine(mahalluName, sizeSp = 22f, bold = true, align = Align.CENTER)
    lines += PdfTextLine(context.getString(R.string.pdf_subscription_receipt), sizeSp = 16f, bold = true, align = Align.CENTER)
    lines += PdfTextLine(" ", sizeSp = 8f)
    lines += PdfTextLine(context.getString(R.string.pdf_receipt_no, subscription.receiptNumber), sizeSp = 12f, bold = true)
    lines += PdfTextLine(context.getString(R.string.pdf_date, formattedDate), sizeSp = 11f)
    lines += PdfTextLine(" ", sizeSp = 6f)
    lines += PdfTextLine(context.getString(R.string.pdf_received_from), sizeSp = 11f, color = android.graphics.Color.DKGRAY)
    val family = familyName.ifBlank { memberName }
    lines += PdfTextLine(family.ifBlank { context.getString(R.string.pdf_family_member_placeholder) }, sizeSp = 14f, bold = true)
    if (memberName.isNotBlank() && memberName != familyName) {
        lines += PdfTextLine(context.getString(R.string.pdf_member, memberName), sizeSp = 11f)
    }
    lines += PdfTextLine(" ", sizeSp = 6f)
    lines += PdfTextLine(context.getString(R.string.pdf_subscription_type, subscription.type), sizeSp = 11f)
    lines += PdfTextLine(context.getString(R.string.pdf_the_sum_of), sizeSp = 11f)
    lines += PdfTextLine("Rs. ${"%,.2f".format(subscription.amount)}", sizeSp = 22f, bold = true, color = android.graphics.Color.parseColor("#3B4FB8"))
    lines += PdfTextLine("(", sizeSp = 10f, color = android.graphics.Color.DKGRAY)
    lines += PdfTextLine(context.getString(R.string.pdf_payment, subscription.paymentMethod), sizeSp = 11f)
    if (!subscription.remarks.isNullOrBlank()) {
        lines += PdfTextLine(context.getString(R.string.pdf_remarks, subscription.remarks), sizeSp = 11f)
    }
    lines += PdfTextLine(" ", sizeSp = 14f)
    lines += PdfTextLine(context.getString(R.string.pdf_issued_on, java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault()).format(java.util.Date())), sizeSp = 11f, align = Align.RIGHT)
    lines += PdfTextLine(context.getString(R.string.pdf_authorised_signatory), sizeSp = 11f, bold = true, align = Align.RIGHT)

    pdfGenerator.generate(
        fileName = "subscription_${subscription.receiptNumber}.pdf",
        spec = PdfDocumentSpec(
            title = context.getString(R.string.pdf_subscription_receipt),
            subtitle = mahalluName,
            lines = lines,
            footer = context.getString(R.string.pdf_footer, mahalluName)
        )
    )
} catch (t: Throwable) {
    null
}
