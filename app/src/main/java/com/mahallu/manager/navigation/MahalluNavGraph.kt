package com.mahallu.manager.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mahallu.manager.core.ui.R as CoreUiR
import com.mahallu.manager.core.ui.theme.PrimaryIndigo
import com.mahallu.manager.feature.auth.LoginScreen
import com.mahallu.manager.feature.auth.LoginViewModel

@Composable
fun MahalluNavGraph() {
    val navController = rememberNavController()
    val authViewModel: LoginViewModel = hiltViewModel()
    val authState by authViewModel.authState.collectAsStateWithLifecycle()

    if (authState.isInitializing) {
        // Branded loading gate: keeps the splash look while the persisted session
        // is checked, so the login screen never flashes for already signed-in users.
        Box(
            modifier = Modifier.fillMaxSize().background(PrimaryIndigo),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(CoreUiR.drawable.ic_logo_mosque),
                    contentDescription = null,
                    modifier = Modifier.size(120.dp)
                )
                Spacer(Modifier.height(20.dp))
                CircularProgressIndicator(
                    modifier = Modifier.size(26.dp),
                    color = Color.White.copy(alpha = 0.9f),
                    strokeWidth = 3.dp
                )
            }
        }
    } else {
        val startDestination = if (authState.isLoggedIn) "main" else "login"

        NavHost(navController = navController, startDestination = startDestination) {
            composable("login") {
                LoginScreen(
                    onLoggedIn = {
                        navController.navigate("main") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                )
            }

            composable("main") {
                MainShell(
                    onLogout = {
                        authViewModel.logout()
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
