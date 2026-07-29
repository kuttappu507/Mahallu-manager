package com.mahallu.manager.feature.auth

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Mosque
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mahallu.manager.core.ui.components.AppButton
import com.mahallu.manager.core.ui.components.AppTextField
import com.mahallu.manager.core.ui.components.PasswordTextField
import com.mahallu.manager.core.ui.theme.LocalMahalluColors

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
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(72.dp))
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(colors.primaryIndigo.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Mosque,
                    contentDescription = null,
                    tint = colors.primaryIndigo,
                    modifier = Modifier.size(48.dp)
                )
            }
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Mahallu",
                style = MaterialTheme.typography.displaySmall,
                color = colors.textPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Management System",
                style = MaterialTheme.typography.titleMedium,
                color = colors.textSecondary
            )
            Spacer(Modifier.height(48.dp))
            Text(
                text = "Welcome Back",
                style = MaterialTheme.typography.headlineSmall,
                color = colors.textPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Sign in to continue",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary
            )
            Spacer(Modifier.height(28.dp))

            AppTextField(
                value = username,
                onValueChange = { username = it; viewModel.clearError() },
                label = "Username / Mobile",
                placeholder = "Enter username or mobile",
                leadingIcon = Icons.Rounded.Person,
                keyboardType = KeyboardType.Text,
                isRequired = true
            )
            Spacer(Modifier.height(14.dp))
            PasswordTextField(
                value = password,
                onValueChange = { password = it; viewModel.clearError() },
                label = "Password",
                isRequired = true
            )

            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it },
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "Remember me",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textSecondary
                    )
                }
                Text(
                    text = "Forgot Password?",
                    style = MaterialTheme.typography.labelLarge,
                    color = colors.accentCoral,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (!authState.error.isNullOrBlank()) {
                Spacer(Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
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

            Spacer(Modifier.height(24.dp))
            AppButton(
                text = if (authState.isLoading) "Signing in..." else "Sign In",
                onClick = { viewModel.login(username, password, rememberMe) {} },
                isLoading = authState.isLoading,
                enabled = !authState.isLoading
            )

            Spacer(Modifier.height(24.dp))
            Text(
                text = "Don't have an account? Contact Administrator",
                style = MaterialTheme.typography.labelMedium,
                color = colors.textSecondary,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(20.dp))
            Text(
                text = "Demo: admin / admin123 • secretary / secretary123",
                style = MaterialTheme.typography.labelSmall,
                color = colors.textTertiary
            )
        }
    }
}