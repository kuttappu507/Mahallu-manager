package com.mahallu.manager.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mahallu.manager.core.ui.components.AppButton
import com.mahallu.manager.core.ui.components.PasswordTextField
import com.mahallu.manager.core.ui.components.TopAppBar
import com.mahallu.manager.core.ui.theme.LocalMahalluColors
import com.mahallu.manager.core.ui.theme.RadiusLg
import com.mahallu.manager.feature.auth.R

@Composable
fun ChangePasswordScreen(
    forced: Boolean = false,
    onBack: () -> Unit,
    onChanged: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.authState.collectAsStateWithLifecycle()
    val colors = LocalMahalluColors.current

    var current by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize().background(colors.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = stringResource(R.string.change_password_title),
                showBack = !forced,
                onBackClick = onBack
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                if (forced) {
                    Spacer(Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(RadiusLg.value.dp))
                            .background(colors.warning.copy(alpha = 0.12f))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.change_password_forced_subtitle),
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Spacer(Modifier.height(20.dp))
                PasswordTextField(
                    value = current,
                    onValueChange = { current = it; viewModel.clearError() },
                    label = stringResource(R.string.change_password_current)
                )
                Spacer(Modifier.height(12.dp))
                PasswordTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it; viewModel.clearError() },
                    label = stringResource(R.string.change_password_new)
                )
                Spacer(Modifier.height(12.dp))
                PasswordTextField(
                    value = confirm,
                    onValueChange = { confirm = it; viewModel.clearError() },
                    label = stringResource(R.string.change_password_confirm)
                )

                if (!state.error.isNullOrBlank()) {
                    Spacer(Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(RadiusLg.value.dp))
                            .background(colors.errorLight)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = state.error ?: "",
                            color = colors.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))
                AppButton(
                    text = if (state.isLoading) stringResource(R.string.change_password_saving) else stringResource(R.string.change_password_button),
                    onClick = {
                        if (newPassword != confirm) {
                            viewModel.setError(stringResource(R.string.change_password_error_mismatch))
                        } else {
                            viewModel.changePassword(current, newPassword) { onChanged() }
                        }
                    },
                    isLoading = state.isLoading,
                    enabled = !state.isLoading
                )
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}
