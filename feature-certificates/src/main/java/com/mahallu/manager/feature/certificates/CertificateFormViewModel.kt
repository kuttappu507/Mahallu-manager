package com.mahallu.manager.feature.certificates

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahallu.manager.core.database.entity.CertificateEntity
import com.mahallu.manager.core.database.repository.CertificateRepository
import com.mahallu.manager.core.database.repository.DeathRepository
import com.mahallu.manager.core.database.repository.FamilyRepository
import com.mahallu.manager.core.database.repository.MarriageRepository
import com.mahallu.manager.core.database.repository.MemberRepository
import com.mahallu.manager.core.database.repository.SettingsRepository
import com.mahallu.manager.core.util.IdGenerator
import com.mahallu.manager.core.ui.util.Formatters
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

data class CertificateRecordOption(
    val id: String,
    val title: String,
    val subtitle: String
)

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
    val message: String? = null,
    val recordQuery: String = "",
    val recordOptions: List<CertificateRecordOption> = emptyList()
)

@HiltViewModel
class CertificateFormViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val pdfGenerator: PdfGenerator,
    private val certificateRepo: CertificateRepository,
    private val settingsRepo: SettingsRepository,
    private val memberRepo: MemberRepository,
    private val familyRepo: FamilyRepository,
    private val marriageRepo: MarriageRepository,
    private val deathRepo: DeathRepository
) : ViewModel() {
    private val _state = MutableStateFlow(CertificateFormState())
    val state: StateFlow<CertificateFormState> = _state.asStateFlow()

    fun update(transform: (CertificateFormState) -> CertificateFormState) {
        _state.update(transform)
    }

    /** Reset the form so switching certificate types doesn't leak state between them. */
    fun reset() {
        _state.update { CertificateFormState() }
    }

    /** Pre-populate fields (used when generating a cert from an existing entity). */
    fun prefill(
        memberName: String? = null,
        fatherName: String? = null,
        address: String? = null,
        memberNumber: String? = null,
        brideName: String? = null,
        groomName: String? = null,
        witnesses: String? = null,
        registrationNumber: String? = null,
        date: String? = null,
        deceasedName: String? = null
    ) {
        _state.update {
            it.copy(
                memberName = memberName ?: it.memberName,
                fatherName = fatherName ?: it.fatherName,
                address = address ?: it.address,
                memberNumber = memberNumber ?: it.memberNumber,
                brideName = brideName ?: it.brideName,
                groomName = groomName ?: it.groomName,
                witnesses = witnesses ?: it.witnesses,
                registrationNumber = registrationNumber ?: it.registrationNumber,
                date = date ?: it.date,
                deceasedName = deceasedName ?: it.deceasedName
            )
        }
    }

    fun searchRecords(type: String, query: String) {
        if (query.isBlank()) {
            _state.update { it.copy(recordQuery = "", recordOptions = emptyList()) }
            return
        }
        _state.update { it.copy(recordQuery = query) }
        viewModelScope.launch {
            val options = when (type) {
                "MEMBERSHIP", "RESIDENCE" -> memberRepo.search(query).map {
                    CertificateRecordOption(
                        id = it.id,
                        title = it.name,
                        subtitle = "${it.memberNumber} • ${it.relationToHead ?: "Member"}"
                    )
                }
                "MARRIAGE" -> marriageRepo.search(query).map {
                    CertificateRecordOption(
                        id = it.id,
                        title = "${it.brideName} & ${it.groomName}",
                        subtitle = it.registrationNumber
                    )
                }
                "DEATH" -> deathRepo.search(query).map {
                    CertificateRecordOption(
                        id = it.id,
                        title = it.name,
                        subtitle = it.registrationNumber
                    )
                }
                else -> emptyList()
            }
            _state.update { it.copy(recordOptions = options.take(20)) }
        }
    }

    fun selectRecord(type: String, option: CertificateRecordOption) {
        viewModelScope.launch {
            when (type) {
                "MEMBERSHIP" -> {
                    val m = memberRepo.getById(option.id) ?: return@launch
                    val fam = m.familyId?.let { familyRepo.getById(it) }
                    _state.update {
                        it.copy(
                            memberName = m.name,
                            fatherName = "",
                            address = m.address ?: fam?.address ?: "",
                            memberNumber = m.memberNumber,
                            recordQuery = "",
                            recordOptions = emptyList()
                        )
                    }
                }
                "RESIDENCE" -> {
                    val m = memberRepo.getById(option.id) ?: return@launch
                    val fam = m.familyId?.let { familyRepo.getById(it) }
                    _state.update {
                        it.copy(
                            memberName = m.name,
                            fatherName = "",
                            address = m.address ?: fam?.address ?: "",
                            ward = fam?.ward ?: "",
                            pincode = fam?.pincode ?: "",
                            recordQuery = "",
                            recordOptions = emptyList()
                        )
                    }
                }
                "MARRIAGE" -> {
                    val mar = marriageRepo.getById(option.id) ?: return@launch
                    _state.update {
                        it.copy(
                            brideName = mar.brideName,
                            groomName = mar.groomName,
                            witnesses = listOfNotNull(mar.witnessOneName, mar.witnessTwoName).joinToString(", "),
                            registrationNumber = mar.registrationNumber,
                            date = Formatters.date(mar.nikahDate),
                            recordQuery = "",
                            recordOptions = emptyList()
                        )
                    }
                }
                "DEATH" -> {
                    val d = deathRepo.getById(option.id) ?: return@launch
                    _state.update {
                        it.copy(
                            deceasedName = d.name,
                            fatherName = d.fatherName ?: "",
                            registrationNumber = d.registrationNumber,
                            date = Formatters.date(d.dateOfDeath),
                            recordQuery = "",
                            recordOptions = emptyList()
                        )
                    }
                }
            }
        }
    }

    fun generate(type: String) {
        if (_state.value.isGenerating) return
        _state.update { it.copy(isGenerating = true, message = null) }
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
                        lines += PdfTextLine("Father / Spouse: ${s.fatherName.ifBlank { "[Father / Spouse Name]" }}", sizeSp = 12f)
                        lines += PdfTextLine("Address: ${s.address.ifBlank { "[Address]" }}", sizeSp = 12f)
                        lines += PdfTextLine("Member ID: ${s.memberNumber.ifBlank { "[Member ID]" }}", sizeSp = 12f)
                    }
                    "RESIDENCE" -> {
                        lines += PdfTextLine("Name: ${s.memberName.ifBlank { "[Name]" }}", sizeSp = 12f)
                        lines += PdfTextLine("Father / Spouse: ${s.fatherName.ifBlank { "[Father / Spouse Name]" }}", sizeSp = 12f)
                        lines += PdfTextLine("Address: ${s.address.ifBlank { "[Address]" }}", sizeSp = 12f)
                        lines += PdfTextLine("Ward: ${s.ward.ifBlank { "[Ward]" }}    Pincode: ${s.pincode.ifBlank { "[Pincode]" }}", sizeSp = 12f)
                    }
                    "MARRIAGE" -> {
                        lines += PdfTextLine("Bride: ${s.brideName.ifBlank { "[Bride Name]" }}", sizeSp = 12f)
                        lines += PdfTextLine("Groom: ${s.groomName.ifBlank { "[Groom Name]" }}", sizeSp = 12f)
                        lines += PdfTextLine("Nikah Date: ${s.date.ifBlank { "[Date]" }}", sizeSp = 12f)
                        lines += PdfTextLine("Location: ${s.address.ifBlank { "[Location]" }}", sizeSp = 12f)
                        lines += PdfTextLine("Witnesses: ${s.witnesses.ifBlank { "[Witnesses]" }}", sizeSp = 12f)
                        lines += PdfTextLine("Registration #: ${s.registrationNumber.ifBlank { "[Reg #]" }}", sizeSp = 12f)
                    }
                    "DEATH" -> {
                        lines += PdfTextLine("Name: ${s.deceasedName.ifBlank { "[Name]" }}", sizeSp = 12f)
                        lines += PdfTextLine("Father / Spouse: ${s.fatherName.ifBlank { "[Father / Spouse Name]" }}", sizeSp = 12f)
                        lines += PdfTextLine("Date of Death: ${s.date.ifBlank { "[Date]" }}", sizeSp = 12f)
                        lines += PdfTextLine("Place: ${s.address.ifBlank { "[Place]" }}", sizeSp = 12f)
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
                val subjectName = when (type) {
                    "MARRIAGE" -> "${s.brideName} & ${s.groomName}"
                    "DEATH" -> s.deceasedName
                    else -> s.memberName
                }
                val certEntity = CertificateEntity(
                    id = IdGenerator.newId(),
                    certificateNumber = "CRT-${System.currentTimeMillis().toString().takeLast(7)}",
                    type = type,
                    subjectId = "",
                    subjectName = subjectName,
                    issuedTo = subjectName,
                    issuedDate = System.currentTimeMillis(),
                    pdfPath = file.absolutePath
                )
                certificateRepo.save(certEntity)
                _state.update { it.copy(pdfPath = file.absolutePath, isGenerating = false, message = "PDF generated: ${file.name}") }
            } catch (t: Throwable) {
                _state.update { it.copy(isGenerating = false, message = "Failed: ${t.message ?: t::class.java.simpleName}") }
            }
        }
    }
}
