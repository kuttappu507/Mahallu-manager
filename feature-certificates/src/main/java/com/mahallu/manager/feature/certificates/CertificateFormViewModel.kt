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
import com.mahallu.manager.feature.certificates.pdf.PdfInfoBlock
import com.mahallu.manager.feature.certificates.pdf.PdfPanel
import com.mahallu.manager.feature.certificates.pdf.PdfTable
import com.mahallu.manager.feature.certificates.pdf.PdfTextLine
import feature.certificates.feature.certificates.R
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
    val groomFatherName: String = "",
    val groomAge: String = "",
    val brideFatherName: String = "",
    val brideAge: String = "",
    val mahar: String = "",
    val performedBy: String = "",
    val groomAddress: String = "",
    val brideAddress: String = "",
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
        deceasedName: String? = null,
        groomFatherName: String? = null,
        groomAge: String? = null,
        brideFatherName: String? = null,
        brideAge: String? = null,
        mahar: String? = null,
        performedBy: String? = null,
        groomAddress: String? = null,
        brideAddress: String? = null
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
                deceasedName = deceasedName ?: it.deceasedName,
                groomFatherName = groomFatherName ?: it.groomFatherName,
                groomAge = groomAge ?: it.groomAge,
                brideFatherName = brideFatherName ?: it.brideFatherName,
                brideAge = brideAge ?: it.brideAge,
                mahar = mahar ?: it.mahar,
                performedBy = performedBy ?: it.performedBy,
                groomAddress = groomAddress ?: it.groomAddress,
                brideAddress = brideAddress ?: it.brideAddress
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
                        subtitle = context.getString(R.string.cert_record_option_membership, it.memberNumber, it.relationToHead ?: context.getString(R.string.cert_relation_member))
                    )
                }
                "MARRIAGE" -> marriageRepo.search(query).map {
                    CertificateRecordOption(
                        id = it.id,
                        title = context.getString(R.string.cert_record_option_marriage, it.brideName, it.groomName),
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
                    val groom = mar.groomId?.let { memberRepo.getById(it) }
                    val bride = mar.brideId?.let { memberRepo.getById(it) }
                    _state.update {
                        it.copy(
                            brideName = mar.brideName,
                            groomName = mar.groomName,
                            witnesses = listOfNotNull(mar.witnessOneName, mar.witnessTwoName).joinToString(", "),
                            registrationNumber = mar.registrationNumber,
                            date = Formatters.date(mar.nikahDate),
                            groomFatherName = mar.groomFatherName,
                            groomAge = mar.groomAge?.toString().orEmpty(),
                            brideFatherName = mar.brideFatherName,
                            brideAge = mar.brideAge?.toString().orEmpty(),
                            mahar = mar.maharAmount.takeIf { it > 0 }?.let { "Rs. ${"%,.2f".format(it)}" }.orEmpty(),
                            performedBy = mar.performedBy.orEmpty(),
                            address = mar.nikahLocation.orEmpty(),
                            groomAddress = groom?.address.orEmpty(),
                            brideAddress = bride?.address.orEmpty(),
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
                val mahalluAddress = settingsRepo.getString("mahallu.address", "").orEmpty()
                val title = context.getString(
                    when (type) {
                        "MEMBERSHIP" -> R.string.cert_membership_title
                        "RESIDENCE" -> R.string.cert_residence_title
                        "MARRIAGE" -> R.string.cert_marriage_title
                        "DEATH" -> R.string.cert_death_title
                        else -> R.string.cert_generic_title
                    }
                ).uppercase(java.util.Locale.getDefault())
                val s = _state.value
                val teal = android.graphics.Color.parseColor("#0F766E")
                val janab = context.getString(R.string.cert_pdf_janab)
                val stamp = java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", java.util.Locale.getDefault()).format(java.util.Date())

                val lines = mutableListOf<PdfTextLine>()
                var panels = emptyList<PdfPanel>()
                var infoBlocks = emptyList<PdfInfoBlock>()
                var signatureLabels = listOf(
                    context.getString(R.string.cert_secretary),
                    context.getString(R.string.cert_president)
                )

                fun centered(text: String, sizeSp: Float = 14f, bold: Boolean = false) {
                    lines += PdfTextLine(
                        text,
                        sizeSp = sizeSp,
                        bold = bold,
                        align = Align.CENTER,
                        color = if (bold) teal else android.graphics.Color.BLACK
                    )
                }

                fun lv(label: String, value: String): String =
                    context.getString(R.string.cert_pdf_label_value, label, value)

                fun fieldsLines(pairs: List<Pair<String, String>>): List<String> {
                    val mapped = pairs.filter { it.second.isNotBlank() }
                        .map { (k, v) -> lv(k, v) }
                    return mapped.chunked(2).map { it.joinToString("  •  ") }
                }

                fun ageLabel(age: String): String {
                    val n = age.toIntOrNull()
                    return if (n != null) context.getString(R.string.cert_years, n) else age
                }

                when (type) {
                    "MEMBERSHIP" -> {
                        centered(context.getString(R.string.cert_pdf_certify_that))
                        centered("$janab ${s.memberName}", sizeSp = 20f, bold = true)
                        centered(context.getString(R.string.cert_pdf_body_membership))
                        fieldsLines(listOf(
                            context.getString(R.string.cert_pdf_label_member_id) to s.memberNumber,
                            context.getString(R.string.cert_pdf_label_ward) to s.ward,
                            context.getString(R.string.cert_pdf_label_father_spouse) to s.fatherName,
                            context.getString(R.string.cert_pdf_label_address) to s.address
                        )).forEach { centered(it) }
                    }
                    "RESIDENCE" -> {
                        centered(context.getString(R.string.cert_pdf_certify_that))
                        centered("$janab ${s.memberName}", sizeSp = 20f, bold = true)
                        centered(context.getString(R.string.cert_pdf_body_residence))
                        fieldsLines(listOf(
                            context.getString(R.string.cert_pdf_label_name) to s.memberName,
                            context.getString(R.string.cert_pdf_label_father_spouse) to s.fatherName,
                            context.getString(R.string.cert_pdf_label_ward) to s.ward,
                            context.getString(R.string.cert_pdf_label_pincode) to s.pincode,
                            context.getString(R.string.cert_pdf_label_address) to s.address
                        )).forEach { centered(it) }
                    }
                    "MARRIAGE" -> {
                        signatureLabels = listOf(
                            context.getString(R.string.cert_secretary),
                            context.getString(R.string.cert_qazi_label),
                            context.getString(R.string.cert_president)
                        )
                        centered(context.getString(R.string.cert_pdf_marriage_intro), sizeSp = 14f)
                        val groomRows = listOfNotNull(
                            context.getString(R.string.cert_label_full_name) to s.groomName,
                            context.getString(R.string.cert_label_fathers_name) to s.groomFatherName,
                            context.getString(R.string.cert_label_age) to ageLabel(s.groomAge),
                            context.getString(R.string.cert_label_address) to s.groomAddress
                        ).filter { it.second.isNotBlank() }
                        val brideRows = listOfNotNull(
                            context.getString(R.string.cert_label_full_name) to s.brideName,
                            context.getString(R.string.cert_label_fathers_name) to s.brideFatherName,
                            context.getString(R.string.cert_label_age) to ageLabel(s.brideAge),
                            context.getString(R.string.cert_label_address) to s.brideAddress
                        ).filter { it.second.isNotBlank() }
                        panels = listOf(
                            PdfPanel(
                                title1 = context.getString(R.string.cert_marriage_groom),
                                rows1 = groomRows,
                                title2 = context.getString(R.string.cert_marriage_bride),
                                rows2 = brideRows
                            )
                        )
                        val nikahRows = listOfNotNull(
                            context.getString(R.string.cert_nikah_date) to s.date,
                            context.getString(R.string.cert_nikah_venue) to s.address,
                            context.getString(R.string.cert_mahr) to s.mahar,
                            context.getString(R.string.cert_registration_no) to s.registrationNumber
                        ).filter { it.second.isNotBlank() }
                        val witnessLines = s.witnesses
                            .split(",")
                            .map { it.trim() }
                            .filter { it.isNotEmpty() }
                            .mapIndexed { i, w -> "${i + 1}. $w" }
                        infoBlocks = listOfNotNull(
                            PdfInfoBlock(context.getString(R.string.cert_nikah_details), keyValueRows = nikahRows),
                            PdfInfoBlock(context.getString(R.string.cert_witnesses), textRows = witnessLines),
                            s.performedBy.ifBlank { null }?.let { PdfInfoBlock(context.getString(R.string.cert_qazi), textRows = listOf(it)) }
                        )
                    }
                    "DEATH" -> {
                        centered(context.getString(R.string.cert_pdf_certify_that))
                        centered("$janab ${s.deceasedName}", sizeSp = 20f, bold = true)
                        centered(context.getString(R.string.cert_pdf_body_death, s.date, s.address))
                        fieldsLines(listOf(
                            context.getString(R.string.cert_pdf_label_father_spouse) to s.fatherName,
                            context.getString(R.string.cert_pdf_label_registration) to s.registrationNumber
                        )).forEach { centered(it) }
                    }
                }

                val file = pdfGenerator.generate(
                    fileName = "${type.lowercase()}_${System.currentTimeMillis()}.pdf",
                    spec = com.mahallu.manager.feature.certificates.pdf.PdfDocumentSpec(
                        title = title,
                        subtitle = mahalluName,
                        address = mahalluAddress,
                        lines = lines,
                        ornament = true,
                        panels = panels,
                        infoBlocks = infoBlocks,
                        signatureLabels = signatureLabels,
                        issuedLine = context.getString(R.string.cert_pdf_issued_on, stamp),
                        footer = context.getString(R.string.cert_pdf_footer_generated_at, mahalluName, stamp)
                    )
                )
                val subjectName = when (type) {
                    "MARRIAGE" -> "${s.brideName} & ${s.groomName}"
                    "DEATH" -> s.deceasedName
                    else -> s.memberName
                }
                val now = System.currentTimeMillis()
                val existing = certificateRepo.findByTypeAndSubject(type, subjectName)
                val certEntity = if (existing != null) {
                    existing.copy(issuedDate = now, pdfPath = file.absolutePath)
                } else {
                    CertificateEntity(
                        id = IdGenerator.newId(),
                        certificateNumber = "CRT-${now.toString().takeLast(7)}",
                        type = type,
                        subjectId = "",
                        subjectName = subjectName,
                        issuedTo = subjectName,
                        issuedDate = now,
                        pdfPath = file.absolutePath
                    )
                }
                certificateRepo.save(certEntity)
                _state.update { it.copy(pdfPath = file.absolutePath, isGenerating = false, message = context.getString(R.string.cert_pdf_generated_name, file.name)) }
            } catch (t: Throwable) {
                _state.update { it.copy(isGenerating = false, message = context.getString(R.string.cert_pdf_failed_error, t.message ?: t::class.java.simpleName)) }
            }
        }
    }
}
