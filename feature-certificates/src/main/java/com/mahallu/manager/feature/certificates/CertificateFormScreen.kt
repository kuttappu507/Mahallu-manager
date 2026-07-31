package com.mahallu.manager.feature.certificates

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.PictureAsPdf
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mahallu.manager.core.ui.components.AppButton
import com.mahallu.manager.core.ui.components.AppCard
import com.mahallu.manager.core.ui.components.AppTextField
import com.mahallu.manager.core.ui.components.TopAppBar
import com.mahallu.manager.core.ui.theme.LocalMahalluColors
import com.mahallu.manager.core.ui.util.PdfShare
import java.io.File

/**
 * Generic certificate form. Switches between Membership/Residence/Marriage/Death
 * by passing a [type] arg. Each form is scrollable, fully editable, and at the bottom
 * shows a "Generate PDF" button. Once the PDF is generated, a "View PDF" and "Share"
 * row appears.
 */
@Composable
fun CertificateFormScreen(
    type: String,
    onBack: () -> Unit,
    viewModel: CertificateFormViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = LocalMahalluColors.current
    val context = LocalContext.current

    // Reset state when type changes (so a stale form from another certificate doesn't leak in)
    LaunchedEffect(type) {
        viewModel.reset()
        // Apply any prefill data (e.g. from a marriage/death/member record) on first open
        CertificatePrefillHolder.consume()?.let { prefill ->
            viewModel.prefill(
                memberName = prefill.memberName,
                fatherName = prefill.fatherName,
                address = prefill.address,
                memberNumber = prefill.memberNumber,
                brideName = prefill.brideName,
                groomName = prefill.groomName,
                witnesses = prefill.witnesses,
                registrationNumber = prefill.registrationNumber,
                date = prefill.date,
                deceasedName = prefill.deceasedName
            )
        }
    }

    val title = when (type) {
        "MEMBERSHIP" -> "Membership Certificate"
        "RESIDENCE" -> "Residence Certificate"
        "MARRIAGE" -> "Marriage Certificate"
        "DEATH" -> "Death Certificate"
        else -> "Certificate"
    }

    Box(modifier = Modifier.fillMaxSize().background(colors.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = title,
                showBack = true,
                onBackClick = onBack,
                trailingActions = {
                    IconButton(onClick = { viewModel.generate(type) }, enabled = !state.isGenerating) {
                        Icon(Icons.Rounded.PictureAsPdf, contentDescription = "Generate PDF", tint = colors.primaryIndigo)
                    }
                }
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                when (type) {
                    "MEMBERSHIP" -> MembershipFields(state, viewModel)
                    "RESIDENCE" -> ResidenceFields(state, viewModel)
                    "MARRIAGE" -> MarriageFields(state, viewModel)
                    "DEATH" -> DeathFields(state, viewModel)
                }
                Spacer(Modifier.height(20.dp))
                if (!state.message.isNullOrBlank()) {
                    AppCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = (if (state.pdfPath != null) colors.success else colors.error).copy(alpha = 0.10f),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(14.dp)
                    ) {
                        Text(state.message ?: "", color = colors.textPrimary, style = MaterialTheme.typography.bodyMedium)
                    }
                    Spacer(Modifier.height(12.dp))
                }
                if (state.pdfPath != null) {
                    AppCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = colors.primaryIndigo.copy(alpha = 0.08f),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(14.dp)
                    ) {
                        Column {
                            Text(
                                text = "PDF ready: ${File(state.pdfPath!!).name}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.textPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = state.pdfPath!!,
                                style = MaterialTheme.typography.labelSmall,
                                color = colors.textTertiary,
                                maxLines = 2
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        AppButton(
                            text = "View PDF",
                            onClick = { PdfShare.open(context, File(state.pdfPath!!)) },
                            modifier = Modifier.weight(1f),
                            leadingIcon = Icons.Rounded.PictureAsPdf
                        )
                        AppButton(
                            text = "Share",
                            onClick = { PdfShare.share(context, File(state.pdfPath!!)) },
                            modifier = Modifier.weight(1f),
                            leadingIcon = Icons.Rounded.Share
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                AppButton(
                    text = if (state.isGenerating) "Generating PDF..." else "Generate PDF",
                    onClick = { viewModel.generate(type) },
                    isLoading = state.isGenerating
                )
                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun MembershipFields(state: CertificateFormState, vm: CertificateFormViewModel) {
    AppTextField(
        value = state.memberName,
        onValueChange = { v -> vm.update { it.copy(memberName = v) } },
        label = "Member Name",
        isRequired = true
    )
    Spacer(Modifier.height(10.dp))
    AppTextField(
        value = state.fatherName,
        onValueChange = { v -> vm.update { it.copy(fatherName = v) } },
        label = "Father / Spouse Name"
    )
    Spacer(Modifier.height(10.dp))
    AppTextField(
        value = state.address,
        onValueChange = { v -> vm.update { it.copy(address = v) } },
        label = "Address",
        singleLine = false,
        maxLines = 3
    )
    Spacer(Modifier.height(10.dp))
    AppTextField(
        value = state.memberNumber,
        onValueChange = { v -> vm.update { it.copy(memberNumber = v) } },
        label = "Member ID / Number"
    )
}

@Composable
private fun ResidenceFields(state: CertificateFormState, vm: CertificateFormViewModel) {
    AppTextField(
        value = state.memberName,
        onValueChange = { v -> vm.update { it.copy(memberName = v) } },
        label = "Resident Name",
        isRequired = true
    )
    Spacer(Modifier.height(10.dp))
    AppTextField(
        value = state.fatherName,
        onValueChange = { v -> vm.update { it.copy(fatherName = v) } },
        label = "Father / Spouse Name"
    )
    Spacer(Modifier.height(10.dp))
    AppTextField(
        value = state.address,
        onValueChange = { v -> vm.update { it.copy(address = v) } },
        label = "Address",
        singleLine = false,
        maxLines = 3
    )
    Spacer(Modifier.height(10.dp))
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(modifier = Modifier.weight(1f)) {
            AppTextField(
                value = state.ward,
                onValueChange = { v -> vm.update { it.copy(ward = v) } },
                label = "Ward"
            )
        }
        Box(modifier = Modifier.weight(1f)) {
            AppTextField(
                value = state.pincode,
                onValueChange = { v -> vm.update { it.copy(pincode = v) } },
                label = "Pincode",
                keyboardType = KeyboardType.Number
            )
        }
    }
}

@Composable
private fun MarriageFields(state: CertificateFormState, vm: CertificateFormViewModel) {
    AppTextField(
        value = state.brideName,
        onValueChange = { v -> vm.update { it.copy(brideName = v) } },
        label = "Bride Name",
        isRequired = true
    )
    Spacer(Modifier.height(10.dp))
    AppTextField(
        value = state.groomName,
        onValueChange = { v -> vm.update { it.copy(groomName = v) } },
        label = "Groom Name",
        isRequired = true
    )
    Spacer(Modifier.height(10.dp))
    AppTextField(
        value = state.date,
        onValueChange = { v -> vm.update { it.copy(date = v) } },
        label = "Nikah Date (e.g. 25 Dec 2024)"
    )
    Spacer(Modifier.height(10.dp))
    AppTextField(
        value = state.witnesses,
        onValueChange = { v -> vm.update { it.copy(witnesses = v) } },
        label = "Witnesses (comma-separated)",
        singleLine = false,
        maxLines = 2
    )
    Spacer(Modifier.height(10.dp))
    AppTextField(
        value = state.registrationNumber,
        onValueChange = { v -> vm.update { it.copy(registrationNumber = v) } },
        label = "Registration Number"
    )
    Spacer(Modifier.height(10.dp))
    AppTextField(
        value = state.address,
        onValueChange = { v -> vm.update { it.copy(address = v) } },
        label = "Nikah Location / Address",
        singleLine = false,
        maxLines = 2
    )
}

@Composable
private fun DeathFields(state: CertificateFormState, vm: CertificateFormViewModel) {
    AppTextField(
        value = state.deceasedName,
        onValueChange = { v -> vm.update { it.copy(deceasedName = v) } },
        label = "Deceased Name",
        isRequired = true
    )
    Spacer(Modifier.height(10.dp))
    AppTextField(
        value = state.fatherName,
        onValueChange = { v -> vm.update { it.copy(fatherName = v) } },
        label = "Father / Spouse Name"
    )
    Spacer(Modifier.height(10.dp))
    AppTextField(
        value = state.date,
        onValueChange = { v -> vm.update { it.copy(date = v) } },
        label = "Date of Death (e.g. 25 Dec 2024)"
    )
    Spacer(Modifier.height(10.dp))
    AppTextField(
        value = state.registrationNumber,
        onValueChange = { v -> vm.update { it.copy(registrationNumber = v) } },
        label = "Registration Number"
    )
    Spacer(Modifier.height(10.dp))
    AppTextField(
        value = state.address,
        onValueChange = { v -> vm.update { it.copy(address = v) } },
        label = "Address (where death occurred)",
        singleLine = false,
        maxLines = 2
    )
}
