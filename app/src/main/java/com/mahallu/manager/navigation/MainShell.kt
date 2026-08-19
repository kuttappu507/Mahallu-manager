package com.mahallu.manager.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.FamilyRestroom
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mahallu.manager.core.ui.components.AppBottomNavBar
import com.mahallu.manager.core.ui.theme.LocalMahalluColors
import com.mahallu.manager.feature.auth.ChangePasswordScreen
import com.mahallu.manager.feature.certificates.CertificateListScreen
import com.mahallu.manager.feature.certificates.CertificatePrefillData
import com.mahallu.manager.feature.certificates.CertificatePrefillHolder
import com.mahallu.manager.feature.certificates.DeathCertificateScreen
import com.mahallu.manager.feature.certificates.MarriageCertificateScreen
import com.mahallu.manager.feature.certificates.MembershipCertificateScreen
import com.mahallu.manager.feature.certificates.ResidenceCertificateScreen
import com.mahallu.manager.feature.dashboard.AnnouncementScreen
import com.mahallu.manager.feature.dashboard.DashboardScreen
import com.mahallu.manager.feature.death.DeathEditScreen
import com.mahallu.manager.feature.death.DeathListScreen
import com.mahallu.manager.feature.donations.DonationDetailScreen
import com.mahallu.manager.feature.donations.DonationEntryScreen
import com.mahallu.manager.feature.donations.DonationsScreen
import com.mahallu.manager.feature.families.FamilyDetailScreen
import com.mahallu.manager.feature.families.FamilyEditScreen
import com.mahallu.manager.feature.families.FamiliesScreen
import com.mahallu.manager.feature.finance.FinanceScreen
import com.mahallu.manager.feature.finance.IncomeExpenseEntryScreen
import com.mahallu.manager.feature.marriage.MarriageEditScreen
import com.mahallu.manager.feature.marriage.MarriageListScreen
import com.mahallu.manager.feature.members.MemberDetailScreen
import com.mahallu.manager.feature.members.MemberEditScreen
import com.mahallu.manager.feature.members.MembersScreen
import com.mahallu.manager.feature.reports.ReportsScreen
import com.mahallu.manager.feature.search.GlobalSearchScreen
import com.mahallu.manager.feature.settings.BackupScreen
import com.mahallu.manager.feature.settings.MoreScreen
import com.mahallu.manager.feature.settings.SettingsScreen
import com.mahallu.manager.feature.subscriptions.CollectionDetailScreen
import com.mahallu.manager.feature.subscriptions.CollectionEntryScreen
import com.mahallu.manager.feature.subscriptions.SubscriptionsScreen
import com.mahallu.manager.feature.welfare.WelfareEditScreen
import com.mahallu.manager.feature.welfare.WelfareScreen

private val TAB_ROUTES = setOf("dashboard", "families", "members", "finance", "more")

private fun isTabRoute(route: String?): Boolean = route in TAB_ROUTES

// Bottom tabs are state-restored views, not pushed pages. Keeping them free of
// enter/exit animation prevents a heavy Room-backed screen from fading while it
// is also being restored/recomposed, which looked like a freeze followed by a
// snap. Detail/form destinations retain the subtle motion below.
private fun tabEnter(): EnterTransition = EnterTransition.None
private fun tabExit(): ExitTransition = ExitTransition.None

private fun pushEnter(): EnterTransition =
    fadeIn(tween(160)) + scaleIn(initialScale = 0.98f, animationSpec = tween(160))

private fun pushExit(): ExitTransition = fadeOut(tween(90))

@Composable
fun MainShell(
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route ?: "dashboard"

    val tabs = listOf(
        BottomTab("dashboard", "Home", Icons.Rounded.Dashboard),
        BottomTab("families", "Families", Icons.Rounded.FamilyRestroom),
        BottomTab("members", "Members", Icons.Rounded.Groups),
        BottomTab("finance", "Finance", Icons.Rounded.AccountBalanceWallet),
        BottomTab("more", "More", Icons.Rounded.MoreHoriz)
    )

    val showBottomBar = tabs.any { it.route == currentRoute }

    val navigateToTab: (String) -> Unit = { route ->
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = expandVertically(expandFrom = Alignment.Bottom, animationSpec = tween(180)) +
                    fadeIn(tween(180)),
                exit = shrinkVertically(shrinkTowards = Alignment.Bottom, animationSpec = tween(150)) +
                    fadeOut(tween(150))
            ) {
                AppBottomNavBar(
                    currentRoute = currentRoute,
                    onItemClick = { item -> navigateToTab(item.route) }
                )
            }
        },
        containerColor = LocalMahalluColors.current.background
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            NavHost(
                navController = navController,
                startDestination = "dashboard",
                enterTransition = {
                    if (isTabRoute(targetState.destination.route)) tabEnter() else pushEnter()
                },
                exitTransition = {
                    if (isTabRoute(initialState.destination.route)) tabExit() else pushExit()
                },
                popEnterTransition = {
                    if (isTabRoute(targetState.destination.route)) tabEnter() else pushEnter()
                },
                popExitTransition = {
                    if (isTabRoute(initialState.destination.route)) tabExit() else pushExit()
                }
            ) {
                composable("dashboard") {
                    DashboardScreen(
                        onNavigate = { route ->
                            if (tabs.any { it.route == route }) navigateToTab(route) else navController.navigate(route)
                        }
                    )
                }
                composable("families") {
                    FamiliesScreen(
                        onAddFamily = { navController.navigate("family_edit?id=") },
                        onFamilyClick = { id -> navController.navigate("family_detail/$id") }
                    )
                }
                composable("members") {
                    MembersScreen(
                        onAddMember = { navController.navigate("member_edit?id=") },
                        onMemberClick = { id -> navController.navigate("member_detail/$id") }
                    )
                }
                composable("finance") {
                    FinanceScreen(onAddEntry = { navController.navigate("finance_entry") })
                }
                composable("more") {
                    MoreScreen(
                        onNavigate = { route -> navController.navigate(route) },
                        onLogout = onLogout
                    )
                }

                composable("family_detail/{familyId}", arguments = listOf(navArgument("familyId") { type = NavType.StringType })) {
                    FamilyDetailScreen(
                        onBack = { navController.popBackStack() },
                        onEdit = { id -> navController.navigate("family_edit?id=$id") },
                        onMemberClick = { id -> navController.navigate("member_detail/$id") },
                        onStatement = { navController.navigate("finance") }
                    )
                }
                composable("family_edit?id={familyId}", arguments = listOf(navArgument("familyId") {
                    type = NavType.StringType; defaultValue = ""; nullable = true
                })) {
                    FamilyEditScreen(onDone = { navController.popBackStack() })
                }
                composable("member_detail/{memberId}", arguments = listOf(navArgument("memberId") { type = NavType.StringType })) {
                    MemberDetailScreen(
                        onBack = { navController.popBackStack() },
                        onEdit = { id -> navController.navigate("member_edit?id=$id") },
                        onAddCollection = { id -> navController.navigate("collection_entry?memberId=$id") },
                        onGenerateCertificate = { m ->
                            CertificatePrefillHolder.set(
                                CertificatePrefillData(
                                    memberName = m.name,
                                    fatherName = "",
                                    address = m.address.orEmpty(),
                                    memberNumber = m.memberNumber
                                )
                            )
                            navController.navigate("certificate/MEMBERSHIP")
                        }
                    )
                }
                composable("member_edit?id={memberId}", arguments = listOf(navArgument("memberId") {
                    type = NavType.StringType; defaultValue = ""; nullable = true
                })) {
                    MemberEditScreen(onDone = { navController.popBackStack() })
                }
                composable("collection_entry?memberId={memberId}", arguments = listOf(navArgument("memberId") {
                    type = NavType.StringType; nullable = true; defaultValue = ""
                })) {
                    CollectionEntryScreen(onDone = { navController.popBackStack() })
                }
                composable("collection_detail/{collectionId}", arguments = listOf(navArgument("collectionId") { type = NavType.StringType })) {
                    CollectionDetailScreen(onBack = { navController.popBackStack() })
                }
                composable("subscriptions") {
                    SubscriptionsScreen(
                        onBack = { navController.popBackStack() },
                        onAddCollection = { navController.navigate("collection_entry?memberId=") },
                        onOpenItem = { id -> navController.navigate("collection_detail/$id") }
                    )
                }
                composable("donations") {
                    DonationsScreen(
                        onBack = { navController.popBackStack() },
                        onAdd = { navController.navigate("donation_entry") },
                        onOpenItem = { id -> navController.navigate("donation_detail/$id") }
                    )
                }
                composable("donation_entry") {
                    DonationEntryScreen(onDone = { navController.popBackStack() })
                }
                composable("donation_detail/{donationId}", arguments = listOf(navArgument("donationId") { type = NavType.StringType })) {
                    DonationDetailScreen(onBack = { navController.popBackStack() })
                }
                composable("finance_entry") {
                    IncomeExpenseEntryScreen(onDone = { navController.popBackStack() })
                }
                composable("marriages") {
                    MarriageListScreen(
                        onBack = { navController.popBackStack() },
                        onAdd = { navController.navigate("marriage_edit?id=") },
                        onItemClick = { id -> navController.navigate("marriage_edit?id=$id") }
                    )
                }
                composable("marriage_edit?id={id}", arguments = listOf(navArgument("id") {
                    type = NavType.StringType; nullable = true; defaultValue = ""
                })) {
                    MarriageEditScreen(
                        onDone = { navController.popBackStack() },
                        onGenerateCertificate = { m ->
                            CertificatePrefillHolder.set(
                                CertificatePrefillData(
                                    brideName = m.brideName,
                                    groomName = m.groomName,
                                    fatherName = m.brideFatherName,
                                    address = m.nikahLocation,
                                    witnesses = listOf(m.witnessOneName, m.witnessTwoName).filter { it.isNotBlank() }.joinToString(", "),
                                    registrationNumber = m.registrationNumber,
                                    date = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault()).format(m.nikahDate),
                                    groomFatherName = m.groomFatherName,
                                    groomAge = m.groomAge,
                                    brideFatherName = m.brideFatherName,
                                    brideAge = m.brideAge,
                                    mahar = m.maharAmount.toDoubleOrNull()?.takeIf { it > 0 }?.let { "Rs. ${"%,.2f".format(it)}" },
                                    groomAddress = m.members.firstOrNull { it.id == m.groomId }?.address,
                                    brideAddress = m.members.firstOrNull { it.id == m.brideId }?.address
                                )
                            )
                            navController.navigate("certificate/MARRIAGE")
                        }
                    )
                }
                composable("deaths") {
                    DeathListScreen(
                        onBack = { navController.popBackStack() },
                        onAdd = { navController.navigate("death_edit?id=") },
                        onItemClick = { id -> navController.navigate("death_edit?id=$id") }
                    )
                }
                composable("death_edit?id={id}", arguments = listOf(navArgument("id") {
                    type = NavType.StringType; nullable = true; defaultValue = ""
                })) {
                    DeathEditScreen(
                        onDone = { navController.popBackStack() },
                        onGenerateCertificate = { d ->
                            CertificatePrefillHolder.set(
                                CertificatePrefillData(
                                    deceasedName = d.name,
                                    fatherName = d.fatherName,
                                    address = d.burialLocation,
                                    registrationNumber = d.registrationNumber,
                                    date = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault()).format(d.dateOfDeath)
                                )
                            )
                            navController.navigate("certificate/DEATH")
                        }
                    )
                }
                composable("welfare") {
                    WelfareScreen(
                        onBack = { navController.popBackStack() },
                        onAdd = { navController.navigate("welfare_edit?id=") },
                        onItemClick = { id -> navController.navigate("welfare_edit?id=$id") }
                    )
                }
                composable("welfare_edit?id={id}", arguments = listOf(navArgument("id") {
                    type = NavType.StringType; nullable = true; defaultValue = ""
                })) {
                    WelfareEditScreen(onDone = { navController.popBackStack() })
                }
                composable("certificates") {
                    CertificateListScreen(
                        onBack = { navController.popBackStack() },
                        onSelect = { type -> navController.navigate("certificate/$type") }
                    )
                }
                composable("certificate/{type}", arguments = listOf(navArgument("type") { type = NavType.StringType })) {
                    val type = it.arguments?.getString("type") ?: "MEMBERSHIP"
                    when (type) {
                        "MEMBERSHIP" -> MembershipCertificateScreen(onBack = { navController.popBackStack() })
                        "RESIDENCE" -> ResidenceCertificateScreen(onBack = { navController.popBackStack() })
                        "MARRIAGE" -> MarriageCertificateScreen(onBack = { navController.popBackStack() })
                        "DEATH" -> DeathCertificateScreen(onBack = { navController.popBackStack() })
                    }
                }
                composable("reports") {
                    ReportsScreen(onBack = { navController.popBackStack() })
                }
                composable("settings") {
                    SettingsScreen(
                        onBack = { navController.popBackStack() },
                        onChangePassword = { navController.navigate("change_password?forced=false") }
                    )
                }
                composable("change_password?forced={forced}", arguments = listOf(navArgument("forced") {
                    type = NavType.StringType; defaultValue = "false"
                })) {
                    ChangePasswordScreen(
                        forced = it.arguments?.getString("forced") == "true",
                        onBack = { navController.popBackStack() },
                        onChanged = { navController.popBackStack() }
                    )
                }
                composable("backup") {
                    BackupScreen(onBack = { navController.popBackStack() })
                }
                composable("search") {
                    GlobalSearchScreen(
                        onBack = { navController.popBackStack() },
                        onFamilyClick = { id -> navController.navigate("family_detail/$id") },
                        onMemberClick = { id -> navController.navigate("member_detail/$id") },
                        onMarriageClick = { id -> navController.navigate("marriage_edit?id=$id") },
                        onDeathClick = { id -> navController.navigate("death_edit?id=$id") },
                        onWelfareClick = { id -> navController.navigate("welfare_edit?id=$id") }
                    )
                }
                composable("announcements") {
                    AnnouncementScreen(
                        onDone = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}

data class BottomTab(val route: String, val label: String, val icon: ImageVector)