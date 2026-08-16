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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mahallu.manager.MahalluApplication
import com.mahallu.manager.core.ui.R as CoreUiR
import com.mahallu.manager.core.ui.theme.PrimaryIndigo
import com.mahallu.manager.feature.auth.ChangePasswordScreen
import com.mahallu.manager.feature.auth.LoginScreen
import com.mahallu.manager.feature.auth.LoginViewModel

@Composable
fun MahalluNavGraph() {
    val navController = rememberNavController()
    val authViewModel: LoginViewModel = hiltViewModel()
    val authState by authViewModel.authState.collectAsStateWithLifecycle()

    val appContext = LocalContext.current.applicationContext as MahalluApplication
    val appReady by appContext.appReady.collectAsStateWithLifecycle()

    if (authState.isInitializing || !appReady) {
        // Branded loading gate: keeps the splash look while the persisted
        // session is checked and the first-run DB seed finishes, so the login
        // screen never flashes for signed-in users and first-run seeding jank
        // is hidden behind this screen instead of janking the UI.
        Box(
            modifier = Modifier.fillMaxSize().background(Color(0xFFF8FAFC)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(CoreUiR.drawable.ic_logo),
                    contentDescription = null,
                    modifier = Modifier.size(200.dp)
                )
                Spacer(Modifier.height(24.dp))
                CircularProgressIndicator(
                    modifier = Modifier.size(28.dp),
                    color = PrimaryIndigo,
                    strokeWidth = 3.dp
                )
            }
        }
    } else {
        val startDestination = when {
            authState.isLoggedIn && authState.mustChangePassword -> "change_password?forced=true"
            authState.isLoggedIn -> "main"
            else -> "login"
        }

        NavHost(navController = navController, startDestination = startDestination) {
            composable("login") {
                LoginScreen(
                    onLoggedIn = {
                        if (authState.mustChangePassword) {
                            navController.navigate("change_password?forced=true") {
                                popUpTo("login") { inclusive = true }
                            }
                        } else {
                            navController.navigate("main") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    }
                )
            }

            composable("change_password?forced={forced}", arguments = listOf(
                androidx.navigation.navArgument("forced") {
                    type = androidx.navigation.NavType.StringType
                    defaultValue = "false"
                }
            )) {
                val forced = it.arguments?.getString("forced") == "true"
                ChangePasswordScreen(
                    forced = forced,
                    onBack = { navController.popBackStack() },
                    onChanged = {
                        navController.navigate("main") {
                            popUpTo(0) { inclusive = true }
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
