package com.mahallu.manager.feature.marriage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
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
import com.mahallu.manager.core.ui.components.SearchableSelectField
import com.mahallu.manager.core.ui.components.TopAppBar
import com.mahallu.manager.core.ui.theme.LocalMahalluColors
import com.mahallu.manager.core.ui.util.Formatters
import feature.marriage.feature.marriage.R

@Composable
fun MarriageEditScreen(
    onDone: () -> Unit,
    onGenerateCertificate: (MarriageEditState) -> Unit = {},
    viewModel: MarriageEditViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = LocalMahalluColors.current

    LaunchedEffect(state.saved) { if (state.saved) onDone() }

    androidx.compose.foundation.layout.Box(modifier = Modifier.fillMaxSize().background(colors.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = if (state.id.isBlank()) stringResource(R.string.marriage_register_new) else stringResource(R.string.marriage_edit_title),
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
                AppTextField(value = state.registrationNumber, onValueChange = { v -> viewModel.update { it.copy(registrationNumber = v) } }, label = stringResource(R.string.marriage_registration_number), readOnly = true)
                Spacer(Modifier.height(12.dp))
                Text(stringResource(R.string.marriage_bride), style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
                Spacer(Modifier.height(8.dp))
                SearchableSelectField(
                    label = stringResource(R.string.marriage_select_member),
                    selectedLabel = state.brideName,
                    placeholder = stringResource(R.string.marriage_pick_bride),
                    options = state.members.map { it.id to it.name },
                    onSelect = { viewModel.selectBride(it) }
                )
                Spacer(Modifier.height(8.dp))
                AppTextField(value = state.brideName, onValueChange = { v -> viewModel.update { it.copy(brideName = v) } }, label = stringResource(R.string.marriage_bride_name), isRequired = true)
                Spacer(Modifier.height(8.dp))
                AppTextField(value = state.brideFatherName, onValueChange = { v -> viewModel.update { it.copy(brideFatherName = v) } }, label = stringResource(R.string.marriage_father_name))
                Spacer(Modifier.height(8.dp))
                AppTextField(value = state.brideAge, onValueChange = { v -> viewModel.update { it.copy(brideAge = v) } }, label = stringResource(R.string.marriage_age), keyboardType = KeyboardType.Number)
                Spacer(Modifier.height(16.dp))
                Text(stringResource(R.string.marriage_groom), style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
                Spacer(Modifier.height(8.dp))
                SearchableSelectField(
                    label = stringResource(R.string.marriage_select_member),
                    selectedLabel = state.groomName,
                    placeholder = stringResource(R.string.marriage_pick_groom),
                    options = state.members.map { it.id to it.name },
                    onSelect = { viewModel.selectGroom(it) }
                )
                Spacer(Modifier.height(8.dp))
                AppTextField(value = state.groomName, onValueChange = { v -> viewModel.update { it.copy(groomName = v) } }, label = stringResource(R.string.marriage_groom_name), isRequired = true)
                Spacer(Modifier.height(8.dp))
                AppTextField(value = state.groomFatherName, onValueChange = { v -> viewModel.update { it.copy(groomFatherName = v) } }, label = stringResource(R.string.marriage_father_name))
                Spacer(Modifier.height(8.dp))
                AppTextField(value = state.groomAge, onValueChange = { v -> viewModel.update { it.copy(groomAge = v) } }, label = stringResource(R.string.marriage_age), keyboardType = KeyboardType.Number)
                Spacer(Modifier.height(16.dp))
                Text(stringResource(R.string.marriage_witnesses), style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
                Spacer(Modifier.height(8.dp))
                AppTextField(value = state.witnessOneName, onValueChange = { v -> viewModel.update { it.copy(witnessOneName = v) } }, label = stringResource(R.string.marriage_witness_1))
                Spacer(Modifier.height(8.dp))
                AppTextField(value = state.witnessTwoName, onValueChange = { v -> viewModel.update { it.copy(witnessTwoName = v) } }, label = stringResource(R.string.marriage_witness_2))
                Spacer(Modifier.height(16.dp))
                AppTextField(value = state.maharAmount, onValueChange = { v -> viewModel.update { it.copy(maharAmount = v) } }, label = stringResource(R.string.marriage_mahar_amount), keyboardType = KeyboardType.Decimal)
                Spacer(Modifier.height(12.dp))
                AppTextField(value = Formatters.date(state.nikahDate), onValueChange = { }, label = stringResource(R.string.marriage_nikah_date), readOnly = true)
                Spacer(Modifier.height(12.dp))
                AppTextField(value = state.nikahLocation, onValueChange = { v -> viewModel.update { it.copy(nikahLocation = v) } }, label = stringResource(R.string.marriage_nikah_location))
                Spacer(Modifier.height(12.dp))
                AppTextField(value = state.remarks, onValueChange = { v -> viewModel.update { it.copy(remarks = v) } }, label = stringResource(R.string.marriage_remarks), maxLines = 3, singleLine = false)
                if (!state.error.isNullOrBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(state.error ?: "", color = colors.error, style = MaterialTheme.typography.bodySmall)
                }
                Spacer(Modifier.height(20.dp))
                AppButton(text = if (state.isSaving) stringResource(R.string.marriage_saving) else stringResource(R.string.marriage_save), onClick = { viewModel.save() }, isLoading = state.isSaving)
                if (state.id.isNotBlank() && state.brideName.isNotBlank() && state.groomName.isNotBlank()) {
                    Spacer(Modifier.height(10.dp))
                    AppButton(
                        text = stringResource(R.string.marriage_generate_certificate),
                        onClick = { onGenerateCertificate(viewModel.current()) },
                        leadingIcon = androidx.compose.material.icons.Icons.Rounded.PictureAsPdf
                    )
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}