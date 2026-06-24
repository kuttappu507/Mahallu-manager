package com.mahallu.feature.members.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.mahallu.feature.members.ui.screens.MemberListScreen

const val MEMBER_LIST_ROUTE = "member_list"
const val MEMBER_DETAIL_ROUTE = "member_detail/{memberId}"
const val MEMBER_ADD_ROUTE = "member_add"
const val MEMBER_EDIT_ROUTE = "member_edit/{memberId}"

fun NavController.navigateToMemberList(navOptions: NavOptions? = null) {
    navigate(MEMBER_LIST_ROUTE, navOptions)
}

fun NavController.navigateToMemberDetail(memberId: Int, navOptions: NavOptions? = null) {
    navigate("member_detail/$memberId", navOptions)
}

fun NavController.navigateToMemberAdd(navOptions: NavOptions? = null) {
    navigate(MEMBER_ADD_ROUTE, navOptions)
}

fun NavController.navigateToMemberEdit(memberId: Int, navOptions: NavOptions? = null) {
    navigate("member_edit/$memberId", navOptions)
}

fun NavGraphBuilder.memberListScreen(
    onMemberClick: (Int) -> Unit,
    onAddMember: () -> Unit,
    onNavigateBack: () -> Unit
) {
    composable(route = MEMBER_LIST_ROUTE) {
        MemberListScreen(
            onMemberClick = onMemberClick,
            onAddMember = onAddMember,
            onNavigateBack = onNavigateBack
        )
    }
}
