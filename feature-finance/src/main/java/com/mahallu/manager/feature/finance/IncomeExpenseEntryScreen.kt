package com.mahallu.manager.feature.finance

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
                title = if (state.type == "INCOME") "Add Income" else "Add Expense",
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
                Text("Type", style = MaterialTheme.typography.labelLarge, color = colors.textSecondary)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("INCOME", "EXPENSE").forEach { t ->
                        ChipPill(text = t, selected = state.type == t, onClick = { viewModel.setType(t) })
                    }
                }
                Spacer(Modifier.height(12.dp))
                Text("Category", style = MaterialTheme.typography.labelLarge, color = colors.textSecondary)
                Spacer(Modifier.height(6.dp))
                val cats = if (state.type == "INCOME") incomeCats else expenseCats
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    cats.take(4).forEach { c ->
                        ChipPill(text = c, selected = state.category == c, onClick = { viewModel.update { it.copy(category = c) } })
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    cats.drop(4).forEach { c ->
                        ChipPill(text = c, selected = state.category == c, onClick = { viewModel.update { it.copy(category = c) } })
                    }
                }
                Spacer(Modifier.height(12.dp))
                AppTextField(
                    value = state.description,
                    onValueChange = { v -> viewModel.update { it.copy(description = v) } },
                    label = "Description",
                    isRequired = true
                )
                Spacer(Modifier.height(12.dp))
                AppTextField(
                    value = state.amount,
                    onValueChange = { v -> viewModel.update { it.copy(amount = v) } },
                    label = "Amount",
                    keyboardType = KeyboardType.Decimal,
                    isRequired = true
                )
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
                AppButton(
                    text = if (state.isSaving) "Saving..." else "Save Entry",
                    onClick = { viewModel.save() },
                    isLoading = state.isSaving
                )
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}