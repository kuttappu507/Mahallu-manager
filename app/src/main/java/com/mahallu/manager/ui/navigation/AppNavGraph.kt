package com.mahallu.manager.ui.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mahallu.manager.ui.screen.DashboardScreen
import com.mahallu.manager.ui.screen.FamilyListScreen
import com.mahallu.manager.ui.screen.LoginScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Dashboard : Screen("dashboard")
    object Families : Screen("families")
    object FamilyDetail : Screen("family/{familyId}") {
        fun createRoute(familyId: Int) = "family/$familyId"
    }
    object Members : Screen("members")
}

@Composable
fun AppNavGraph(navController: NavHostController, startDestination: String) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Dashboard.route) {
            DashboardScreen()
        }
        
        composable(Screen.Families.route) {
            FamilyListScreen(
                onAddFamily = { },
                onEditFamily = { family ->
                    navController.navigate(Screen.FamilyDetail.createRoute(family.id))
                }
            )
        }
        
        composable(
            route = Screen.FamilyDetail.route,
            arguments = listOf(
                navArgument("familyId") { type = NavType.IntType }
            )
        ) {
            // Family detail screen placeholder
        }
    }
}
