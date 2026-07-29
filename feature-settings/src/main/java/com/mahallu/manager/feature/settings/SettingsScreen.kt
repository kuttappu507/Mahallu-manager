package com.mahallu.manager.feature.settings

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
fun SettingsScreen(onBack: () -> Unit, viewModel: SettingsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = LocalMahalluColors.current

    var name by remember(state.mahalluName) { mutableStateOf(state.mahalluName) }
    var address by remember(state.mahalluAddress) { mutableStateOf(state.mahalluAddress) }
    var phone by remember(state.mahalluPhone) { mutableStateOf(state.mahalluPhone) }
    var email by remember(state.mahalluEmail) { mutableStateOf(state.mahalluEmail) }

    Box(modifier = Modifier.fillMaxSize().background(colors.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(title = "Settings", showBack = true, onBackClick = onBack)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text("Mahallu Information", style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
                Spacer(Modifier.height(8.dp))
                AppTextField(value = name, onValueChange = { name = it }, label = "Mahallu Name")
                Spacer(Modifier.height(8.dp))
                AppTextField(value = address, onValueChange = { address = it }, label = "Address", maxLines = 3, singleLine = false)
                Spacer(Modifier.height(8.dp))
                AppTextField(value = phone, onValueChange = { phone = it }, label = "Phone", keyboardType = KeyboardType.Phone)
                Spacer(Modifier.height(8.dp))
                AppTextField(value = email, onValueChange = { email = it }, label = "Email", keyboardType = KeyboardType.Email)
                Spacer(Modifier.height(12.dp))
                AppButton(text = "Save Mahallu Info", onClick = { viewModel.updateMahallu(name, address, phone, email) })
                Spacer(Modifier.height(24.dp))

                Text("Theme", style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("system", "light", "dark").forEach { t ->
                        ChipPill(text = t.replaceFirstChar { it.uppercase() }, selected = state.themeMode == t, onClick = { viewModel.setTheme(t) })
                    }
                }
                Spacer(Modifier.height(24.dp))

                Text("Backup", style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
                Spacer(Modifier.height(8.dp))
                AppCard(modifier = Modifier.fillMaxWidth(), contentPadding = androidx.compose.foundation.layout.PaddingValues(14.dp)) {
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Auto daily backup", style = MaterialTheme.typography.bodyMedium, color = colors.textPrimary)
                            androidx.compose.material3.Switch(checked = state.backupAutoEnabled, onCheckedChange = { viewModel.setAutoBackup(it) })
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Last backup: " + if (state.lastBackupAt > 0) Formatters.date(state.lastBackupAt) else "Never",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textSecondary
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
                Text("Account", style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
                Spacer(Modifier.height(8.dp))
                AppCard(modifier = Modifier.fillMaxWidth(), contentPadding = androidx.compose.foundation.layout.PaddingValues(14.dp)) {
                    Column {
                        Text(state.userName, style = MaterialTheme.typography.titleSmall, color = colors.textPrimary)
                        Text(state.userRole, style = MaterialTheme.typography.labelMedium, color = colors.primaryIndigo)
                    }
                }
                Spacer(Modifier.height(24.dp))
                Text("About", style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Mahallu Manager v1.0.0\nModern offline-first Mahallu management.\n© 2025 Mahallu Manager.",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary
                )
                Spacer(Modifier.height(40.dp))
            }
        }
    }
}