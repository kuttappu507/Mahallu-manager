package com.mahallu.manager.feature.donations

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

@Composable
fun DonationEntryScreen(
    onDone: () -> Unit,
    viewModel: DonationEntryViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = LocalMahalluColors.current

    LaunchedEffect(state.saved) { if (state.saved) onDone() }

    Box(modifier = Modifier.fillMaxSize().background(colors.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = "Add Donation",
                showBack = true,
                onBackClick = onDone,
                trailingActions = {
                    IconButton(onClick = { viewModel.save() }) {
                        Icon(Icons.Rounded.Check, contentDescription = "Save", tint = colors.primaryIndigo)
                    }
                }
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                AppTextField(value = state.receiptNumber, onValueChange = { v -> viewModel.update { it.copy(receiptNumber = v) } }, label = "Receipt Number", readOnly = true)
                Spacer(Modifier.height(12.dp))
                AppTextField(value = state.donorName, onValueChange = { v -> viewModel.update { it.copy(donorName = v) } }, label = "Donor Name", isRequired = true)
                Spacer(Modifier.height(12.dp))
                AppTextField(value = state.donorMobile, onValueChange = { v -> viewModel.update { it.copy(donorMobile = v) } }, label = "Mobile", keyboardType = KeyboardType.Phone)
                Spacer(Modifier.height(12.dp))
                AppTextField(value = state.amount, onValueChange = { v -> viewModel.update { it.copy(amount = v) } }, label = "Amount", placeholder = "0.00", keyboardType = KeyboardType.Decimal, isRequired = true)
                Spacer(Modifier.height(12.dp))
                Text("Category", style = MaterialTheme.typography.labelLarge, color = colors.textSecondary)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("GENERAL", "MASJID", "BUILDING", "EDUCATION", "MEDICAL", "WELFARE", "OTHER").forEach { c ->
                        ChipPill(text = c, selected = state.category == c, onClick = { viewModel.update { it.copy(category = c) } })
                    }
                }
                Spacer(Modifier.height(12.dp))
                AppTextField(value = state.purpose, onValueChange = { v -> viewModel.update { it.copy(purpose = v) } }, label = "Purpose", maxLines = 2, singleLine = false)
                Spacer(Modifier.height(12.dp))
                AppTextField(value = Formatters.date(state.date), onValueChange = { }, label = "Date", readOnly = true)
                Spacer(Modifier.height(12.dp))
                Text("Payment Method", style = MaterialTheme.typography.labelLarge, color = colors.textSecondary)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("CASH", "UPI", "BANK", "CHEQUE", "OTHER").forEach { p ->
                        ChipPill(text = p, selected = state.paymentMethod == p, onClick = { viewModel.update { it.copy(paymentMethod = p) } })
                    }
                }
                Spacer(Modifier.height(12.dp))
                AppTextField(value = state.remarks, onValueChange = { v -> viewModel.update { it.copy(remarks = v) } }, label = "Remarks", maxLines = 3, singleLine = false)
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
                        Text("Total Amount", style = MaterialTheme.typography.titleMedium, color = colors.textPrimary, fontWeight = FontWeight.SemiBold)
                        Text(
                            text = Formatters.currency(state.amount.toDoubleOrNull() ?: 0.0),
                            style = MaterialTheme.typography.headlineSmall,
                            color = colors.accentCoral,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                AppButton(
                    text = if (state.isSaving) "Saving..." else "Save & Generate Receipt",
                    onClick = { viewModel.save() },
                    isLoading = state.isSaving
                )
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}