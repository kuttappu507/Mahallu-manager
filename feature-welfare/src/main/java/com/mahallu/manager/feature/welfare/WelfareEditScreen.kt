package com.mahallu.manager.feature.welfare

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.draw.clip
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
fun WelfareEditScreen(
    onDone: () -> Unit,
    viewModel: WelfareEditViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = LocalMahalluColors.current

    LaunchedEffect(state.saved) { if (state.saved) onDone() }

    Box(modifier = Modifier.fillMaxSize().background(colors.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = if (state.id.isBlank()) "New Welfare Request" else "Edit Welfare",
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
                Text("Family", style = MaterialTheme.typography.labelLarge, color = colors.textSecondary)
                Spacer(Modifier.height(6.dp))
                FamilyDropdown(
                    families = state.families,
                    selectedId = state.familyId,
                    onSelect = { id -> viewModel.update { it.copy(familyId = id) } }
                )
                Spacer(Modifier.height(12.dp))
                AppTextField(value = state.applicantName, onValueChange = { v -> viewModel.update { it.copy(applicantName = v) } }, label = "Applicant Name", isRequired = true)
                Spacer(Modifier.height(12.dp))
                Text("Category", style = MaterialTheme.typography.labelLarge, color = colors.textSecondary)
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("MEDICAL", "EDUCATION", "MARRIAGE", "FINANCIAL", "OTHER").forEach { c ->
                        ChipPill(text = c, selected = state.category == c, onClick = { viewModel.update { it.copy(category = c) } })
                    }
                }
                Spacer(Modifier.height(12.dp))
                AppTextField(value = state.amount, onValueChange = { v -> viewModel.update { it.copy(amount = v) } }, label = "Amount", keyboardType = KeyboardType.Decimal, isRequired = true)
                Spacer(Modifier.height(12.dp))
                AppTextField(value = state.reason, onValueChange = { v -> viewModel.update { it.copy(reason = v) } }, label = "Reason", maxLines = 3, singleLine = false)
                Spacer(Modifier.height(12.dp))
                AppTextField(value = Formatters.date(state.date), onValueChange = { }, label = "Date", readOnly = true)
                Spacer(Modifier.height(12.dp))
                Text("Status", style = MaterialTheme.typography.labelLarge, color = colors.textSecondary)
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("PENDING", "APPROVED", "DISBURSED", "REJECTED").forEach { s ->
                        ChipPill(text = s, selected = state.status == s, onClick = { viewModel.update { it.copy(status = s) } })
                    }
                }
                Spacer(Modifier.height(12.dp))
                AppTextField(value = state.remarks, onValueChange = { v -> viewModel.update { it.copy(remarks = v) } }, label = "Remarks", maxLines = 3, singleLine = false)
                if (!state.error.isNullOrBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(state.error ?: "", color = colors.error, style = MaterialTheme.typography.bodySmall)
                }
                Spacer(Modifier.height(20.dp))
                AppButton(text = if (state.isSaving) "Saving..." else "Save", onClick = { viewModel.save() }, isLoading = state.isSaving)
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun FamilyDropdown(
    families: List<com.mahallu.manager.core.database.entity.FamilyEntity>,
    selectedId: String,
    onSelect: (String) -> Unit
) {
    val colors = LocalMahalluColors.current
    var expanded by remember { mutableStateOf(false) }
    val selected = families.firstOrNull { it.id == selectedId }
    val display = selected?.let { "${it.familyNumber} • ${it.houseName}" } ?: "Select family"
    Box(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(colors.surfaceVariant)
                .clickable { expanded = true }
                .padding(horizontal = 14.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = display,
                color = if (selected != null) colors.textPrimary else colors.textTertiary,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            families.forEach { f ->
                DropdownMenuItem(
                    text = { Text("${f.familyNumber} • ${f.houseName}") },
                    onClick = {
                        onSelect(f.id)
                        expanded = false
                    }
                )
            }
        }
    }
}