package com.mahallu.manager.feature.families

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.MaterialTheme
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

@Composable
fun FamilyEditScreen(
    onDone: () -> Unit,
    viewModel: FamilyEditViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = LocalMahalluColors.current

    LaunchedEffect(state.saved) { if (state.saved) onDone() }

    Box(modifier = Modifier.fillMaxSize().background(colors.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = if (state.id.isBlank()) "Add Family" else "Edit Family",
                showBack = true,
                onBackClick = onDone,
                trailingActions = {
                    androidx.compose.material3.IconButton(onClick = { viewModel.save() }) {
                        androidx.compose.material3.Icon(
                            Icons.Rounded.Check,
                            contentDescription = "Save",
                            tint = colors.primaryIndigo
                        )
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
                    value = state.familyNumber,
                    onValueChange = { v -> viewModel.update { it.copy(familyNumber = v) } },
                    label = "Family Number",
                    placeholder = "Auto-generated",
                    helperText = "Auto-generated if left blank"
                )
                Spacer(Modifier.height(12.dp))
                AppTextField(
                    value = state.houseName,
                    onValueChange = { v -> viewModel.update { it.copy(houseName = v) } },
                    label = "House Name",
                    placeholder = "e.g. Abdul Rahman House",
                    isRequired = true
                )
                Spacer(Modifier.height(12.dp))
                AppTextField(
                    value = state.houseNumber,
                    onValueChange = { v -> viewModel.update { it.copy(houseNumber = v) } },
                    label = "House Number",
                    placeholder = "12"
                )
                Spacer(Modifier.height(12.dp))
                AppTextField(
                    value = state.ward,
                    onValueChange = { v -> viewModel.update { it.copy(ward = v) } },
                    label = "Ward",
                    placeholder = "Ward 3"
                )
                Spacer(Modifier.height(12.dp))
                AppTextField(
                    value = state.area,
                    onValueChange = { v -> viewModel.update { it.copy(area = v) } },
                    label = "Area",
                    placeholder = "North Area"
                )
                Spacer(Modifier.height(12.dp))
                AppTextField(
                    value = state.address,
                    onValueChange = { v -> viewModel.update { it.copy(address = v) } },
                    label = "Address",
                    placeholder = "Full address",
                    isRequired = true,
                    maxLines = 3,
                    singleLine = false
                )
                Spacer(Modifier.height(12.dp))
                AppTextField(
                    value = state.pincode,
                    onValueChange = { v -> viewModel.update { it.copy(pincode = v) } },
                    label = "Pincode",
                    placeholder = "676505",
                    keyboardType = KeyboardType.Number
                )
                Spacer(Modifier.height(12.dp))
                AppTextField(
                    value = state.primaryMobile,
                    onValueChange = { v -> viewModel.update { it.copy(primaryMobile = v) } },
                    label = "Primary Mobile",
                    placeholder = "+91 9876543210",
                    keyboardType = KeyboardType.Phone
                )
                Spacer(Modifier.height(12.dp))
                AppTextField(
                    value = state.secondaryMobile,
                    onValueChange = { v -> viewModel.update { it.copy(secondaryMobile = v) } },
                    label = "Secondary Mobile",
                    placeholder = "+91 9876543211",
                    keyboardType = KeyboardType.Phone
                )
                Spacer(Modifier.height(12.dp))
                AppTextField(
                    value = state.email,
                    onValueChange = { v -> viewModel.update { it.copy(email = v) } },
                    label = "Email",
                    placeholder = "email@example.com",
                    keyboardType = KeyboardType.Email
                )
                Spacer(Modifier.height(12.dp))
                AppTextField(
                    value = state.notes,
                    onValueChange = { v -> viewModel.update { it.copy(notes = v) } },
                    label = "Notes",
                    placeholder = "Any additional information",
                    maxLines = 4,
                    singleLine = false
                )
                Spacer(Modifier.height(16.dp))

                androidx.compose.material3.Text(
                    text = "Status",
                    style = MaterialTheme.typography.labelLarge,
                    color = colors.textSecondary
                )
                Spacer(Modifier.height(6.dp))
                androidx.compose.foundation.layout.Row(
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
                ) {
                    listOf("ACTIVE", "INACTIVE", "ARCHIVED").forEach { s ->
                        ChipPill(
                            text = s,
                            selected = state.status == s,
                            onClick = { viewModel.update { it.copy(status = s) } }
                        )
                    }
                }

                if (!state.error.isNullOrBlank()) {
                    Spacer(Modifier.height(12.dp))
                    androidx.compose.material3.Text(
                        text = state.error ?: "",
                        color = colors.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(Modifier.height(24.dp))
                AppButton(
                    text = if (state.isSaving) "Saving..." else "Save Family",
                    onClick = { viewModel.save() },
                    isLoading = state.isSaving
                )
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}