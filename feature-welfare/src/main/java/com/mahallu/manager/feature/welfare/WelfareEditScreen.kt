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
import androidx.compose.foundation.layout.imePadding
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
import feature.welfare.feature.welfare.R

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
                title = if (state.id.isBlank()) stringResource(R.string.welfare_edit_new) else stringResource(R.string.welfare_edit_title),
                showBack = true,
                onBackClick = onDone,
                trailingActions = {
                    IconButton(onClick = { viewModel.save() }) {
                        Icon(Icons.Rounded.Check, contentDescription = stringResource(R.string.welfare_cd_save), tint = colors.primaryIndigo)
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
                Text(stringResource(R.string.welfare_field_family), style = MaterialTheme.typography.labelLarge, color = colors.textSecondary)
                Spacer(Modifier.height(6.dp))
                FamilyDropdown(
                    families = state.families,
                    selectedId = state.familyId,
                    onSelect = { id -> viewModel.update { it.copy(familyId = id) } }
                )
                Spacer(Modifier.height(12.dp))
                AppTextField(value = state.applicantName, onValueChange = { v -> viewModel.update { it.copy(applicantName = v) } }, label = stringResource(R.string.welfare_field_applicant_name), isRequired = true)
                Spacer(Modifier.height(12.dp))
                Text(stringResource(R.string.welfare_field_category), style = MaterialTheme.typography.labelLarge, color = colors.textSecondary)
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
                AppTextField(value = state.amount, onValueChange = { v -> viewModel.update { it.copy(amount = v) } }, label = stringResource(R.string.welfare_field_amount), keyboardType = KeyboardType.Decimal, isRequired = true)
                Spacer(Modifier.height(12.dp))
                AppTextField(value = state.reason, onValueChange = { v -> viewModel.update { it.copy(reason = v) } }, label = stringResource(R.string.welfare_field_reason), maxLines = 3, singleLine = false)
                Spacer(Modifier.height(12.dp))
                AppTextField(value = Formatters.date(state.date), onValueChange = { }, label = stringResource(R.string.welfare_field_date), readOnly = true)
                Spacer(Modifier.height(12.dp))
                Text(stringResource(R.string.welfare_field_status), style = MaterialTheme.typography.labelLarge, color = colors.textSecondary)
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
                AppTextField(value = state.remarks, onValueChange = { v -> viewModel.update { it.copy(remarks = v) } }, label = stringResource(R.string.welfare_field_remarks), maxLines = 3, singleLine = false)
                if (!state.error.isNullOrBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(state.error ?: "", color = colors.error, style = MaterialTheme.typography.bodySmall)
                }
                Spacer(Modifier.height(20.dp))
                AppButton(text = if (state.isSaving) stringResource(R.string.welfare_saving) else stringResource(R.string.welfare_save), onClick = { viewModel.save() }, isLoading = state.isSaving)
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
    val display = selected?.let { stringResource(R.string.welfare_family_selector, it.familyNumber, it.houseName) } ?: stringResource(R.string.welfare_family_select)
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
                    text = { Text(stringResource(R.string.welfare_family_selector, f.familyNumber, f.houseName)) },
                    onClick = {
                        onSelect(f.id)
                        expanded = false
                    }
                )
            }
        }
    }
}