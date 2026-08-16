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
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import android.widget.Toast
import com.mahallu.manager.core.ui.components.AppButton
import com.mahallu.manager.core.ui.components.AppCard
import com.mahallu.manager.core.ui.components.AppTextField
import com.mahallu.manager.core.ui.components.ChipPill
import com.mahallu.manager.core.ui.components.TopAppBar
import com.mahallu.manager.core.ui.theme.LocalMahalluColors
import com.mahallu.manager.core.ui.util.Formatters
import feature.settings.feature.settings.R

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onChangePassword: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = LocalMahalluColors.current
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.toastMessages.collect { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    var name by remember(state.mahalluName) { mutableStateOf(state.mahalluName) }
    var address by remember(state.mahalluAddress) { mutableStateOf(state.mahalluAddress) }
    var phone by remember(state.mahalluPhone) { mutableStateOf(state.mahalluPhone) }
    var email by remember(state.mahalluEmail) { mutableStateOf(state.mahalluEmail) }

    Box(modifier = Modifier.fillMaxSize().background(colors.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(title = stringResource(R.string.settings_title), showBack = true, onBackClick = onBack)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(stringResource(R.string.settings_mahallu_info), style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
                Spacer(Modifier.height(8.dp))
                AppTextField(value = name, onValueChange = { name = it }, label = stringResource(R.string.settings_mahallu_name))
                Spacer(Modifier.height(8.dp))
                AppTextField(value = address, onValueChange = { address = it }, label = stringResource(R.string.settings_address), maxLines = 3, singleLine = false)
                Spacer(Modifier.height(8.dp))
                AppTextField(value = phone, onValueChange = { phone = it }, label = stringResource(R.string.settings_phone), keyboardType = KeyboardType.Phone)
                Spacer(Modifier.height(8.dp))
                AppTextField(value = email, onValueChange = { email = it }, label = stringResource(R.string.settings_email), keyboardType = KeyboardType.Email)
                Spacer(Modifier.height(12.dp))
                AppButton(text = stringResource(R.string.settings_save_mahallu), onClick = { viewModel.updateMahallu(name, address, phone, email) })
                Spacer(Modifier.height(24.dp))

                Text(stringResource(R.string.settings_theme), style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("system", "light", "dark").forEach { t ->
                        ChipPill(text = themeLabel(t), selected = state.themeMode == t, onClick = { viewModel.setTheme(t) })
                    }
                }
                Spacer(Modifier.height(24.dp))

                Text(stringResource(R.string.settings_language), style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("en", "ml").forEach { l ->
                        ChipPill(text = languageLabel(l), selected = state.language == l, onClick = { viewModel.setLanguage(l) })
                    }
                }
                Spacer(Modifier.height(24.dp))

                Text(stringResource(R.string.settings_backup), style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
                Spacer(Modifier.height(8.dp))
                AppCard(modifier = Modifier.fillMaxWidth(), contentPadding = androidx.compose.foundation.layout.PaddingValues(14.dp)) {
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(stringResource(R.string.settings_auto_daily_backup), style = MaterialTheme.typography.bodyMedium, color = colors.textPrimary)
                            androidx.compose.material3.Switch(checked = state.backupAutoEnabled, onCheckedChange = { viewModel.setAutoBackup(it) })
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.settings_last_backup, if (state.lastBackupAt > 0) Formatters.date(state.lastBackupAt) else stringResource(R.string.backup_never)),
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textSecondary
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
                Text(stringResource(R.string.settings_account), style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
                Spacer(Modifier.height(8.dp))
                AppCard(modifier = Modifier.fillMaxWidth(), contentPadding = androidx.compose.foundation.layout.PaddingValues(14.dp)) {
                    Column {
                        Text(state.userName, style = MaterialTheme.typography.titleSmall, color = colors.textPrimary)
                        Text(state.userRole, style = MaterialTheme.typography.labelMedium, color = colors.primaryIndigo)
                        Spacer(Modifier.height(10.dp))
                        AppButton(
                            text = stringResource(R.string.settings_change_password),
                            onClick = onChangePassword,
                            style = com.mahallu.manager.core.ui.components.AppButtonStyle.Outline
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
                Text(stringResource(R.string.settings_about), style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.settings_about_text),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary
                )
                Spacer(Modifier.height(40.dp))
            }
        }
    }
}
@Composable
private fun themeLabel(mode: String): String = when (mode) {
    "system" -> stringResource(R.string.settings_theme_system)
    "light" -> stringResource(R.string.settings_theme_light)
    "dark" -> stringResource(R.string.settings_theme_dark)
    else -> mode
}

@Composable
private fun languageLabel(lang: String): String = when (lang) {
    "ml" -> stringResource(R.string.settings_language_malayalam)
    else -> stringResource(R.string.settings_language_english)
}
