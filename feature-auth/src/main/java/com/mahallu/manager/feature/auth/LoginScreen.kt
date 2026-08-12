package com.mahallu.manager.feature.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mahallu.manager.core.ui.components.AnimatedReveal
import com.mahallu.manager.core.ui.components.AppButton
import com.mahallu.manager.core.ui.components.AppButtonStyle
import com.mahallu.manager.core.ui.components.AppTextField
import com.mahallu.manager.core.ui.components.PasswordTextField
import com.mahallu.manager.core.ui.theme.LocalMahalluColors
import com.mahallu.manager.core.ui.theme.PrimaryIndigo
import com.mahallu.manager.core.ui.theme.RadiusLg
import com.mahallu.manager.core.ui.R as CoreUiR
import com.mahallu.manager.feature.auth.R

@Composable
fun LoginScreen(
    onLoggedIn: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val colors = LocalMahalluColors.current

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(true) }

    LaunchedEffect(authState.isLoggedIn) {
        if (authState.isLoggedIn) onLoggedIn()
    }

    Box(modifier = Modifier.fillMaxSize().background(colors.background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(56.dp))

            // Light gradient mosque tile with dashed ring + deco
            AnimatedReveal(index = 0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .shadow(18.dp, RoundedCornerShape(28.dp), ambientColor = Color(0xFF4F46E5).copy(alpha = 0.22f))
                        .clip(RoundedCornerShape(28.dp))
                        .background(Brush.linearGradient(listOf(Color(0xFFEEF2FF), Color(0xFFE0E7FF)))),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(172.dp)
                            .border(2.dp, PrimaryIndigo.copy(alpha = 0.30f), CircleShape)
                    )
                    Image(
                        painter = painterResource(CoreUiR.drawable.ic_logo),
                        contentDescription = null,
                        modifier = Modifier.size(150.dp)
                    )
                }
            }

            Spacer(Modifier.height(28.dp))
            AnimatedReveal(index = 1) {
                Text(
                    text = stringResource(R.string.login_greeting),
                    style = MaterialTheme.typography.headlineLarge,
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
            AnimatedReveal(index = 1) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.login_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(Modifier.height(24.dp))

            AnimatedReveal(index = 1) {
                AppTextField(
                    value = username,
                    onValueChange = { username = it; viewModel.clearError() },
                    label = stringResource(R.string.login_email_member_label),
                    placeholder = stringResource(R.string.login_email_member_placeholder),
                    leadingIcon = Icons.Rounded.Person,
                    keyboardType = KeyboardType.Text,
                    isRequired = true
                )
            }
            Spacer(Modifier.height(14.dp))
            AnimatedReveal(index = 2) {
                PasswordTextField(
                    value = password,
                    onValueChange = { password = it; viewModel.clearError() },
                    label = stringResource(R.string.login_password),
                    isRequired = true
                )
            }

            Spacer(Modifier.height(12.dp))
            AnimatedReveal(index = 2) {
                Text(
                    text = stringResource(R.string.login_forgot_password),
                    style = MaterialTheme.typography.labelLarge,
                    color = colors.primaryIndigo,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.End
                )
            }

            if (!authState.error.isNullOrBlank()) {
                Spacer(Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(RadiusLg.value.dp))
                        .background(colors.errorLight)
                        .padding(12.dp)
                ) {
                    Text(
                        text = authState.error ?: "",
                        color = colors.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(Modifier.height(22.dp))
            AnimatedReveal(index = 3) {
                AppButton(
                    text = if (authState.isLoading) stringResource(R.string.login_signing_in) else stringResource(R.string.login_sign_in),
                    onClick = { viewModel.login(username, password, rememberMe) {} },
                    isLoading = authState.isLoading,
                    enabled = !authState.isLoading
                )
            }

            Spacer(Modifier.height(18.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.weight(1f).height(1.dp).background(colors.border))
                Text(
                    text = stringResource(R.string.login_or),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textTertiary,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                Box(modifier = Modifier.weight(1f).height(1.dp).background(colors.border))
            }

            Spacer(Modifier.height(12.dp))
            AnimatedReveal(index = 4) {
                AppButton(
                    text = stringResource(R.string.login_continue_as_guest),
                    onClick = { viewModel.login("secretary", "secretary123", rememberMe) {} },
                    style = AppButtonStyle.Outline
                )
            }

            Spacer(Modifier.height(26.dp))
            AnimatedReveal(index = 4) {
                Text(
                    text = stringResource(R.string.login_version_tagline),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textTertiary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
