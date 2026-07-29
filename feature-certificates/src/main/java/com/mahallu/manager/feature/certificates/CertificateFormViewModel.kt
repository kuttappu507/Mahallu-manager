package com.mahallu.manager.feature.certificates

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahallu.manager.core.database.entity.CertificateEntity
import com.mahallu.manager.core.database.repository.CertificateRepository
import com.mahallu.manager.core.database.repository.SettingsRepository
import com.mahallu.manager.core.util.IdGenerator
import com.mahallu.manager.feature.certificates.pdf.Align
import com.mahallu.manager.feature.certificates.pdf.PdfGenerator
import com.mahallu.manager.feature.certificates.pdf.PdfTable
import com.mahallu.manager.feature.certificates.pdf.PdfTextLine
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CertificateFormState(
    val memberName: String = "",
    val fatherName: String = "",
    val address: String = "",
    val memberNumber: String = "",
    val ward: String = "",
    val pincode: String = "",
    val brideName: String = "",
    val groomName: String = "",
    val witnesses: String = "",
    val registrationNumber: String = "",
    val date: String = "",
    val deceasedName: String = "",
    val pdfPath: String? = null,
    val isGenerating: Boolean = false,
    val message: String? = null
)

@HiltViewModel
class CertificateFormViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val pdfGenerator: PdfGenerator,
    private val certificateRepo: CertificateRepository,
    private val settingsRepo: SettingsRepository
) : ViewModel() {
    private val _state = MutableStateFlow(CertificateFormState())
    val state: StateFlow<CertificateFormState> = _state.asStateFlow()

    fun update(transform: (CertificateFormState) -> CertificateFormState) {
        _state.update(transform)
    }

    fun generate(type: String) {
        _state.update { it.copy(isGenerating = true) }
        viewModelScope.launch {
            try {
                val mahalluName = settingsRepo.getString("mahallu.name", "Mahallu Manager")
                val title = when (type) {
                    "MEMBERSHIP" -> "Membership Certificate"
                    "RESIDENCE" -> "Residence Certificate"
                    "MARRIAGE" -> "Marriage Certificate"
                    "DEATH" -> "Death Certificate"
                    else -> "Certificate"
                }
                val s = _state.value
                val lines = mutableListOf<PdfTextLine>()
                lines += PdfTextLine(mahalluName, sizeSp = 22f, bold = true, align = Align.CENTER)
                lines += PdfTextLine(title, sizeSp = 16f, bold = true, align = Align.CENTER)
                lines += PdfTextLine(" ", sizeSp = 8f)
                lines += PdfTextLine("This is to certify that:", sizeSp = 12f)
                when (type) {
                    "MEMBERSHIP" -> {
                        lines += PdfTextLine("Name: ${s.memberName.ifBlank { "[Name]" }}", sizeSp = 12f)
                        lines += PdfTextLine("Father: ${s.fatherName.ifBlank { "[Father Name]" }}", sizeSp = 12f)
                        lines += PdfTextLine("Address: ${s.address.ifBlank { "[Address]" }}", sizeSp = 12f)
                        lines += PdfTextLine("Member ID: ${s.memberNumber.ifBlank { "[Member ID]" }}", sizeSp = 12f)
                    }
                    "RESIDENCE" -> {
                        lines += PdfTextLine("Name: ${s.memberName.ifBlank { "[Name]" }}", sizeSp = 12f)
                        lines += PdfTextLine("Father: ${s.fatherName.ifBlank { "[Father Name]" }}", sizeSp = 12f)
                        lines += PdfTextLine("Address: ${s.address.ifBlank { "[Address]" }}", sizeSp = 12f)
                        lines += PdfTextLine("Ward: ${s.ward.ifBlank { "[Ward]" }}", sizeSp = 12f)
                        lines += PdfTextLine("Pincode: ${s.pincode.ifBlank { "[Pincode]" }}", sizeSp = 12f)
                    }
                    "MARRIAGE" -> {
                        lines += PdfTextLine("Bride: ${s.brideName.ifBlank { "[Bride Name]" }}", sizeSp = 12f)
                        lines += PdfTextLine("Groom: ${s.groomName.ifBlank { "[Groom Name]" }}", sizeSp = 12f)
                        lines += PdfTextLine("Date: ${s.date.ifBlank { "[Date]" }}", sizeSp = 12f)
                        lines += PdfTextLine("Witnesses: ${s.witnesses.ifBlank { "[Witnesses]" }}", sizeSp = 12f)
                        lines += PdfTextLine("Registration #: ${s.registrationNumber.ifBlank { "[Reg #]" }}", sizeSp = 12f)
                    }
                    "DEATH" -> {
                        lines += PdfTextLine("Name: ${s.deceasedName.ifBlank { "[Name]" }}", sizeSp = 12f)
                        lines += PdfTextLine("Father: ${s.fatherName.ifBlank { "[Father Name]" }}", sizeSp = 12f)
                        lines += PdfTextLine("Date of Death: ${s.date.ifBlank { "[Date]" }}", sizeSp = 12f)
                        lines += PdfTextLine("Registration #: ${s.registrationNumber.ifBlank { "[Reg #]" }}", sizeSp = 12f)
                    }
                }
                lines += PdfTextLine(" ", sizeSp = 8f)
                lines += PdfTextLine("Issued on: ${java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault()).format(java.util.Date())}", sizeSp = 11f, align = Align.RIGHT)
                lines += PdfTextLine("Authorised Signatory", sizeSp = 11f, bold = true, align = Align.RIGHT)

                val file = pdfGenerator.generate(
                    fileName = "${type.lowercase()}_${System.currentTimeMillis()}.pdf",
                    spec = com.mahallu.manager.feature.certificates.pdf.PdfDocumentSpec(
                        title = title,
                        subtitle = mahalluName,
                        lines = lines,
                        footer = "$mahalluName • Generated by Mahallu Manager"
                    )
                )
                val certEntity = CertificateEntity(
                    id = IdGenerator.newId(),
                    certificateNumber = "CRT-${System.currentTimeMillis().toString().takeLast(7)}",
                    type = type,
                    subjectId = "",
                    subjectName = s.memberName.ifBlank { s.deceasedName },
                    issuedTo = s.memberName.ifBlank { s.deceasedName },
                    issuedDate = System.currentTimeMillis(),
                    pdfPath = file.absolutePath
                )
                certificateRepo.save(certEntity)
                _state.update { it.copy(pdfPath = file.absolutePath, isGenerating = false, message = "PDF generated: ${file.name}") }
            } catch (t: Throwable) {
                _state.update { it.copy(isGenerating = false, message = "Failed: ${t.message}") }
            }
        }
    }
}