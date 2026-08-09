package com.mahallu.manager.feature.death

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.PictureAsPdf
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
import com.mahallu.manager.core.ui.components.SearchableSelectField
import com.mahallu.manager.core.ui.components.TopAppBar
import com.mahallu.manager.core.ui.theme.LocalMahalluColors
import com.mahallu.manager.core.ui.util.Formatters
import feature.death.feature.death.R

@Composable
fun DeathEditScreen(
    onDone: () -> Unit,
    onGenerateCertificate: (DeathEditState) -> Unit = {},
    viewModel: DeathEditViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = LocalMahalluColors.current

    LaunchedEffect(state.saved) { if (state.saved) onDone() }

    Box(modifier = Modifier.fillMaxSize().background(colors.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = if (state.id.isBlank()) stringResource(R.string.death_edit_record_title) else stringResource(R.string.death_edit_title),
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
                AppTextField(value = state.registrationNumber, onValueChange = { v -> viewModel.update { it.copy(registrationNumber = v) } }, label = stringResource(R.string.death_edit_reg_number), readOnly = true)
                Spacer(Modifier.height(12.dp))
                SearchableSelectField(
                    label = stringResource(R.string.death_edit_select_member),
                    selectedLabel = state.name,
                    placeholder = stringResource(R.string.death_edit_pick_deceased),
                    options = state.members.map { it.id to it.name },
                    onSelect = { viewModel.selectDeceased(it) }
                )
                Spacer(Modifier.height(12.dp))
                AppTextField(value = state.name, onValueChange = { v -> viewModel.update { it.copy(name = v) } }, label = stringResource(R.string.death_edit_name), isRequired = true)
                Spacer(Modifier.height(12.dp))
                AppTextField(value = state.fatherName, onValueChange = { v -> viewModel.update { it.copy(fatherName = v) } }, label = stringResource(R.string.death_edit_father_name))
                Spacer(Modifier.height(12.dp))
                AppTextField(value = state.age, onValueChange = { v -> viewModel.update { it.copy(age = v) } }, label = stringResource(R.string.death_edit_age), keyboardType = KeyboardType.Number)
                Spacer(Modifier.height(12.dp))
                Text(stringResource(R.string.death_edit_gender), style = MaterialTheme.typography.labelLarge, color = colors.textSecondary)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("MALE", "FEMALE", "OTHER").forEach { g ->
                        ChipPill(text = g, selected = state.gender == g, onClick = { viewModel.update { it.copy(gender = g) } })
                    }
                }
                Spacer(Modifier.height(12.dp))
                AppTextField(value = Formatters.date(state.dateOfDeath), onValueChange = { }, label = stringResource(R.string.death_edit_date_of_death), readOnly = true)
                Spacer(Modifier.height(12.dp))
                AppTextField(value = state.burialLocation, onValueChange = { v -> viewModel.update { it.copy(burialLocation = v) } }, label = stringResource(R.string.death_edit_burial_location))
                Spacer(Modifier.height(12.dp))
                AppTextField(value = state.causeOfDeath, onValueChange = { v -> viewModel.update { it.copy(causeOfDeath = v) } }, label = stringResource(R.string.death_edit_cause_of_death))
                Spacer(Modifier.height(12.dp))
                AppTextField(value = state.remarks, onValueChange = { v -> viewModel.update { it.copy(remarks = v) } }, label = stringResource(R.string.death_edit_remarks), maxLines = 3, singleLine = false)
                if (!state.error.isNullOrBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(state.error ?: "", color = colors.error, style = MaterialTheme.typography.bodySmall)
                }
                Spacer(Modifier.height(20.dp))
                AppButton(text = if (state.isSaving) stringResource(R.string.death_saving) else stringResource(R.string.death_save), onClick = { viewModel.save() }, isLoading = state.isSaving)
                if (state.id.isNotBlank() && state.name.isNotBlank()) {
                    Spacer(Modifier.height(10.dp))
                    AppButton(
                        text = stringResource(R.string.death_generate_certificate),
                        onClick = { onGenerateCertificate(viewModel.current()) },
                        leadingIcon = androidx.compose.material.icons.Icons.Rounded.PictureAsPdf
                    )
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}