package com.mahallu.manager.feature.members

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
fun MemberEditScreen(
    onDone: () -> Unit,
    viewModel: MemberEditViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = LocalMahalluColors.current

    LaunchedEffect(state.saved) { if (state.saved) onDone() }

    Box(modifier = Modifier.fillMaxSize().background(colors.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = if (state.id.isBlank()) "Add Member" else "Edit Member",
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
                AppTextField(
                    value = state.memberNumber,
                    onValueChange = { v -> viewModel.update { it.copy(memberNumber = v) } },
                    label = "Member ID",
                    placeholder = "Auto-generated"
                )
                Spacer(Modifier.height(12.dp))
                Text("Family *", style = MaterialTheme.typography.labelLarge, color = colors.textSecondary)
                Spacer(Modifier.height(6.dp))
                FamilyDropdown(
                    families = state.families,
                    selectedId = state.familyId,
                    onSelect = { id -> viewModel.update { it.copy(familyId = id) } }
                )

                Spacer(Modifier.height(12.dp))
                AppTextField(
                    value = state.name,
                    onValueChange = { v -> viewModel.update { it.copy(name = v) } },
                    label = "Full Name",
                    placeholder = "Full name",
                    isRequired = true
                )
                Spacer(Modifier.height(12.dp))
                AppTextField(
                    value = state.arabicName,
                    onValueChange = { v -> viewModel.update { it.copy(arabicName = v) } },
                    label = "Arabic Name"
                )
                Spacer(Modifier.height(12.dp))
                Text("Gender", style = MaterialTheme.typography.labelLarge, color = colors.textSecondary)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("MALE", "FEMALE", "OTHER").forEach { g ->
                        ChipPill(text = g, selected = state.gender == g, onClick = { viewModel.update { it.copy(gender = g) } })
                    }
                }
                Spacer(Modifier.height(12.dp))
                AppTextField(
                    value = Formatters.date(state.dateOfBirth),
                    onValueChange = { },
                    label = "Date of Birth",
                    readOnly = true,
                    helperText = "Default: today (tap to change in v2)"
                )
                Spacer(Modifier.height(12.dp))
                AppTextField(
                    value = state.mobile,
                    onValueChange = { v -> viewModel.update { it.copy(mobile = v) } },
                    label = "Mobile",
                    keyboardType = KeyboardType.Phone
                )
                Spacer(Modifier.height(12.dp))
                AppTextField(
                    value = state.email,
                    onValueChange = { v -> viewModel.update { it.copy(email = v) } },
                    label = "Email",
                    keyboardType = KeyboardType.Email
                )
                Spacer(Modifier.height(12.dp))
                AppTextField(
                    value = state.occupation,
                    onValueChange = { v -> viewModel.update { it.copy(occupation = v) } },
                    label = "Occupation"
                )
                Spacer(Modifier.height(12.dp))
                AppTextField(
                    value = state.education,
                    onValueChange = { v -> viewModel.update { it.copy(education = v) } },
                    label = "Education"
                )
                Spacer(Modifier.height(12.dp))
                AppTextField(
                    value = state.bloodGroup,
                    onValueChange = { v -> viewModel.update { it.copy(bloodGroup = v) } },
                    label = "Blood Group"
                )
                Spacer(Modifier.height(12.dp))
                Text("Marital Status", style = MaterialTheme.typography.labelLarge, color = colors.textSecondary)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("SINGLE", "MARRIED", "DIVORCED", "WIDOWED").forEach { s ->
                        ChipPill(text = s, selected = state.maritalStatus == s, onClick = { viewModel.update { it.copy(maritalStatus = s) } })
                    }
                }
                Spacer(Modifier.height(12.dp))
                AppTextField(
                    value = state.relationToHead,
                    onValueChange = { v -> viewModel.update { it.copy(relationToHead = v) } },
                    label = "Relation to Head"
                )
                Spacer(Modifier.height(12.dp))
                AppTextField(
                    value = state.address,
                    onValueChange = { v -> viewModel.update { it.copy(address = v) } },
                    label = "Address",
                    maxLines = 3,
                    singleLine = false
                )
                Spacer(Modifier.height(12.dp))
                AppTextField(
                    value = state.emergencyContactName,
                    onValueChange = { v -> viewModel.update { it.copy(emergencyContactName = v) } },
                    label = "Emergency Contact Name"
                )
                Spacer(Modifier.height(12.dp))
                AppTextField(
                    value = state.emergencyContactNumber,
                    onValueChange = { v -> viewModel.update { it.copy(emergencyContactNumber = v) } },
                    label = "Emergency Contact Number",
                    keyboardType = KeyboardType.Phone
                )
                Spacer(Modifier.height(12.dp))
                AppTextField(
                    value = state.notes,
                    onValueChange = { v -> viewModel.update { it.copy(notes = v) } },
                    label = "Notes",
                    maxLines = 3,
                    singleLine = false
                )
                if (!state.error.isNullOrBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(state.error ?: "", color = colors.error, style = MaterialTheme.typography.bodySmall)
                }
                Spacer(Modifier.height(20.dp))
                AppButton(
                    text = if (state.isSaving) "Saving..." else "Save Member",
                    onClick = { viewModel.save() },
                    isLoading = state.isSaving
                )
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