package com.mahallu.manager.feature.donations

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mahallu.manager.core.ui.components.AppButton
import com.mahallu.manager.core.ui.components.AppCard
import com.mahallu.manager.core.ui.components.AppTextField
import com.mahallu.manager.core.ui.components.ChipPill
import com.mahallu.manager.core.ui.components.TopAppBar
import com.mahallu.manager.core.ui.theme.LocalMahalluColors
import com.mahallu.manager.core.ui.util.Formatters
import com.mahallu.manager.core.ui.util.PdfShare
import feature.donations.feature.donations.R
import java.io.File

@Composable
fun DonationEntryScreen(
    onDone: () -> Unit,
    viewModel: DonationEntryViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = LocalMahalluColors.current
    val context = LocalContext.current

    // Don't auto-navigate if a PDF was generated — let the user view/share it first.
    LaunchedEffect(state.saved, state.pdfPath) {
        if (state.saved && state.pdfPath == null) onDone()
    }

    Box(modifier = Modifier.fillMaxSize().background(colors.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = stringResource(R.string.donations_add_title),
                showBack = true,
                onBackClick = onDone,
                trailingActions = {
                    IconButton(onClick = { viewModel.save() }) {
                        Icon(Icons.Rounded.Check, contentDescription = stringResource(R.string.cd_save), tint = colors.primaryIndigo)
                    }
                }
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                AppTextField(value = state.receiptNumber, onValueChange = { v -> viewModel.update { it.copy(receiptNumber = v) } }, label = stringResource(R.string.donations_receipt_number), readOnly = true)
                Spacer(Modifier.height(12.dp))
                AppTextField(value = state.donorName, onValueChange = { v -> viewModel.update { it.copy(donorName = v) } }, label = stringResource(R.string.donations_donor_name), isRequired = true)
                Spacer(Modifier.height(12.dp))
                AppTextField(value = state.donorMobile, onValueChange = { v -> viewModel.update { it.copy(donorMobile = v) } }, label = stringResource(R.string.donations_mobile), keyboardType = KeyboardType.Phone)
                Spacer(Modifier.height(12.dp))
                AppTextField(value = state.amount, onValueChange = { v -> viewModel.update { it.copy(amount = v) } }, label = stringResource(R.string.donations_amount), placeholder = stringResource(R.string.donations_placeholder_amount), keyboardType = KeyboardType.Decimal, isRequired = true)
                Spacer(Modifier.height(12.dp))
                Text(stringResource(R.string.donations_category), style = MaterialTheme.typography.labelLarge, color = colors.textSecondary)
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("GENERAL", "MASJID", "BUILDING", "EDUCATION", "MEDICAL", "WELFARE", "OTHER").forEach { c ->
                        ChipPill(text = stringResource(donationCategoryLabelRes(c)), selected = state.category == c, onClick = { viewModel.update { it.copy(category = c) } })
                    }
                }
                Spacer(Modifier.height(12.dp))
                AppTextField(value = state.purpose, onValueChange = { v -> viewModel.update { it.copy(purpose = v) } }, label = stringResource(R.string.donations_purpose), maxLines = 2, singleLine = false)
                Spacer(Modifier.height(12.dp))
                AppTextField(value = Formatters.date(state.date), onValueChange = { }, label = stringResource(R.string.donations_date), readOnly = true)
                Spacer(Modifier.height(12.dp))
                Text(stringResource(R.string.donations_payment_method), style = MaterialTheme.typography.labelLarge, color = colors.textSecondary)
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("CASH", "UPI", "BANK", "CHEQUE", "OTHER").forEach { p ->
                        ChipPill(text = stringResource(donationPaymentLabelRes(p)), selected = state.paymentMethod == p, onClick = { viewModel.update { it.copy(paymentMethod = p) } })
                    }
                }
                Spacer(Modifier.height(12.dp))
                AppTextField(value = state.remarks, onValueChange = { v -> viewModel.update { it.copy(remarks = v) } }, label = stringResource(R.string.donations_remarks), maxLines = 3, singleLine = false)
                if (!state.error.isNullOrBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(state.error ?: "", color = colors.error, style = MaterialTheme.typography.bodySmall)
                }
                Spacer(Modifier.height(20.dp))
                AppCard(
                    modifier = Modifier.padding(0.dp),
                    backgroundColor = colors.accentCoral.copy(alpha = 0.08f),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(stringResource(R.string.donations_total_amount), style = MaterialTheme.typography.titleMedium, color = colors.textPrimary, fontWeight = FontWeight.SemiBold)
                        Text(
                            text = Formatters.currency(state.amount.toDoubleOrNull() ?: 0.0),
                            style = MaterialTheme.typography.headlineSmall,
                            color = colors.accentCoral,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                if (state.pdfPath != null) {
                    AppCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = colors.success.copy(alpha = 0.10f),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(14.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.donations_receipt_generated, File(state.pdfPath).name),
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.textPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        AppButton(
                            text = stringResource(R.string.donations_view_receipt),
                            onClick = { PdfShare.open(context, File(state.pdfPath!!)) },
                            modifier = Modifier.weight(1f),
                            leadingIcon = Icons.Rounded.PictureAsPdf
                        )
                        AppButton(
                            text = stringResource(R.string.donations_share),
                            onClick = { PdfShare.share(context, File(state.pdfPath!!)) },
                            modifier = Modifier.weight(1f),
                            leadingIcon = Icons.Rounded.Share
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    AppButton(
                        text = stringResource(R.string.donations_done),
                        onClick = onDone,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    AppButton(
                        text = if (state.isSaving) stringResource(R.string.donations_saving) else stringResource(R.string.donations_save_generate),
                        onClick = { viewModel.save() },
                        isLoading = state.isSaving
                    )
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}