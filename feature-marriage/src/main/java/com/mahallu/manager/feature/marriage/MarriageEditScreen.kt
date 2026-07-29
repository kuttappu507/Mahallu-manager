package com.mahallu.manager.feature.marriage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import com.mahallu.manager.core.ui.components.TopAppBar
import com.mahallu.manager.core.ui.theme.LocalMahalluColors
import com.mahallu.manager.core.ui.util.Formatters

@Composable
fun MarriageEditScreen(
    onDone: () -> Unit,
    viewModel: MarriageEditViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = LocalMahalluColors.current

    LaunchedEffect(state.saved) { if (state.saved) onDone() }

    androidx.compose.foundation.layout.Box(modifier = Modifier.fillMaxSize().background(colors.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = if (state.id.isBlank()) "Register Marriage" else "Edit Marriage",
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
                AppTextField(value = state.registrationNumber, onValueChange = { v -> viewModel.update { it.copy(registrationNumber = v) } }, label = "Registration Number", readOnly = true)
                Spacer(Modifier.height(12.dp))
                Text("Bride", style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
                Spacer(Modifier.height(8.dp))
                AppTextField(value = state.brideName, onValueChange = { v -> viewModel.update { it.copy(brideName = v) } }, label = "Bride Name", isRequired = true)
                Spacer(Modifier.height(8.dp))
                AppTextField(value = state.brideFatherName, onValueChange = { v -> viewModel.update { it.copy(brideFatherName = v) } }, label = "Father Name")
                Spacer(Modifier.height(8.dp))
                AppTextField(value = state.brideAge, onValueChange = { v -> viewModel.update { it.copy(brideAge = v) } }, label = "Age", keyboardType = KeyboardType.Number)
                Spacer(Modifier.height(16.dp))
                Text("Groom", style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
                Spacer(Modifier.height(8.dp))
                AppTextField(value = state.groomName, onValueChange = { v -> viewModel.update { it.copy(groomName = v) } }, label = "Groom Name", isRequired = true)
                Spacer(Modifier.height(8.dp))
                AppTextField(value = state.groomFatherName, onValueChange = { v -> viewModel.update { it.copy(groomFatherName = v) } }, label = "Father Name")
                Spacer(Modifier.height(8.dp))
                AppTextField(value = state.groomAge, onValueChange = { v -> viewModel.update { it.copy(groomAge = v) } }, label = "Age", keyboardType = KeyboardType.Number)
                Spacer(Modifier.height(16.dp))
                Text("Witnesses", style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
                Spacer(Modifier.height(8.dp))
                AppTextField(value = state.witnessOneName, onValueChange = { v -> viewModel.update { it.copy(witnessOneName = v) } }, label = "Witness 1")
                Spacer(Modifier.height(8.dp))
                AppTextField(value = state.witnessTwoName, onValueChange = { v -> viewModel.update { it.copy(witnessTwoName = v) } }, label = "Witness 2")
                Spacer(Modifier.height(16.dp))
                AppTextField(value = state.maharAmount, onValueChange = { v -> viewModel.update { it.copy(maharAmount = v) } }, label = "Mahar Amount", keyboardType = KeyboardType.Decimal)
                Spacer(Modifier.height(12.dp))
                AppTextField(value = Formatters.date(state.nikahDate), onValueChange = { }, label = "Nikah Date", readOnly = true)
                Spacer(Modifier.height(12.dp))
                AppTextField(value = state.nikahLocation, onValueChange = { v -> viewModel.update { it.copy(nikahLocation = v) } }, label = "Nikah Location")
                Spacer(Modifier.height(12.dp))
                AppTextField(value = state.remarks, onValueChange = { v -> viewModel.update { it.copy(remarks = v) } }, label = "Remarks", maxLines = 3, singleLine = false)
                if (!state.error.isNullOrBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(state.error ?: "", color = colors.error, style = MaterialTheme.typography.bodySmall)
                }
                Spacer(Modifier.height(20.dp))
                AppButton(text = if (state.isSaving) "Saving..." else "Save Marriage", onClick = { viewModel.save() }, isLoading = state.isSaving)
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}