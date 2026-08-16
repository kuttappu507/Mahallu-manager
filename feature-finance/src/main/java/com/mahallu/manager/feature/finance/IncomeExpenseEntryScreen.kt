package com.mahallu.manager.feature.finance

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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mahallu.manager.core.ui.components.AppButton
import com.mahallu.manager.core.ui.components.AppTextField
import com.mahallu.manager.core.ui.components.ChipPill
import com.mahallu.manager.core.ui.components.TopAppBar
import com.mahallu.manager.core.ui.theme.LocalMahalluColors
import com.mahallu.manager.core.ui.util.Formatters
import feature.finance.feature.finance.R

@Composable
fun IncomeExpenseEntryScreen(
    onDone: () -> Unit,
    viewModel: IncomeExpenseEntryViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = LocalMahalluColors.current

    LaunchedEffect(state.saved) { if (state.saved) onDone() }

    val incomeCats = listOf("SUBSCRIPTION", "DONATION", "RENT", "OTHER_INCOME")
    val expenseCats = listOf("SALARY", "ELECTRICITY", "WATER", "MAINTENANCE", "WELFARE", "OTHER_EXPENSE")

    Box(modifier = Modifier.fillMaxSize().background(colors.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = if (state.type == "INCOME") stringResource(R.string.finance_add_income) else stringResource(R.string.finance_add_expense),
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
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(stringResource(R.string.finance_field_type), style = MaterialTheme.typography.labelLarge, color = colors.textSecondary)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("INCOME", "EXPENSE").forEach { t ->
                        ChipPill(text = typeLabel(t), selected = state.type == t, onClick = { viewModel.setType(t) })
                    }
                }
                Spacer(Modifier.height(12.dp))
                Text(stringResource(R.string.finance_field_category), style = MaterialTheme.typography.labelLarge, color = colors.textSecondary)
                Spacer(Modifier.height(6.dp))
                val cats = if (state.type == "INCOME") incomeCats else expenseCats
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    cats.forEach { c ->
                        ChipPill(text = categoryLabel(c), selected = state.category == c, onClick = { viewModel.update { it.copy(category = c) } })
                    }
                }
                Spacer(Modifier.height(12.dp))
                AppTextField(
                    value = state.description,
                    onValueChange = { v -> viewModel.update { it.copy(description = v) } },
                    label = stringResource(R.string.finance_field_description),
                    isRequired = true
                )
                Spacer(Modifier.height(12.dp))
                AppTextField(
                    value = state.amount,
                    onValueChange = { v -> viewModel.update { it.copy(amount = v) } },
                    label = stringResource(R.string.finance_field_amount),
                    keyboardType = KeyboardType.Decimal,
                    isRequired = true
                )
                Spacer(Modifier.height(12.dp))
                AppTextField(value = Formatters.date(state.date), onValueChange = { }, label = stringResource(R.string.finance_field_date), readOnly = true)
                Spacer(Modifier.height(12.dp))
                Text(stringResource(R.string.finance_field_payment_method), style = MaterialTheme.typography.labelLarge, color = colors.textSecondary)
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("CASH", "UPI", "BANK", "CHEQUE", "OTHER").forEach { p ->
                        ChipPill(text = paymentLabel(p), selected = state.paymentMethod == p, onClick = { viewModel.update { it.copy(paymentMethod = p) } })
                    }
                }
                Spacer(Modifier.height(12.dp))
                AppTextField(value = state.remarks, onValueChange = { v -> viewModel.update { it.copy(remarks = v) } }, label = stringResource(R.string.finance_field_remarks), maxLines = 3, singleLine = false)
                if (!state.error.isNullOrBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(state.error ?: "", color = colors.error, style = MaterialTheme.typography.bodySmall)
                }
                Spacer(Modifier.height(20.dp))
                AppButton(
                    text = if (state.isSaving) stringResource(R.string.finance_saving) else stringResource(R.string.finance_save_entry),
                    onClick = { viewModel.save() },
                    isLoading = state.isSaving
                )
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}