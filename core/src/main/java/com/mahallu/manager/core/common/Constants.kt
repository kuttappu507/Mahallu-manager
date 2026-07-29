package com.mahallu.manager.core.common

object Constants {
    const val APP_NAME = "Mahallu Manager"
    const val DATABASE_NAME = "mahallu_db"
    const val PREFS_NAME = "mahallu_prefs"
    const val SESSION_NAME = "mahallu_session"

    // Roles
    const val ROLE_ADMIN = "ADMINISTRATOR"
    const val ROLE_PRESIDENT = "PRESIDENT"
    const val ROLE_SECRETARY = "SECRETARY"
    const val ROLE_TREASURER = "TREASURER"
    const val ROLE_IMAM = "IMAM"
    const val ROLE_STAFF = "STAFF"
    const val ROLE_AUDITOR = "AUDITOR"

    // Routes
    object Routes {
        const val SPLASH = "splash"
        const val LOGIN = "login"
        const val DASHBOARD = "dashboard"
        const val FAMILIES = "families"
        const val FAMILY_DETAIL = "family/{familyId}"
        const val FAMILY_EDIT = "family/edit?id={familyId}"
        const val MEMBERS = "members"
        const val MEMBER_DETAIL = "member/{memberId}"
        const val MEMBER_EDIT = "member/edit?id={memberId}"
        const val SUBSCRIPTIONS = "subscriptions"
        const val COLLECTION_ENTRY = "collection/entry?memberId={memberId}"
        const val DONATIONS = "donations"
        const val DONATION_ENTRY = "donation/entry"
        const val FINANCE = "finance"
        const val MARRIAGES = "marriages"
        const val DEATHS = "deaths"
        const val WELFARE = "welfare"
        const val CERTIFICATES = "certificates"
        const val REPORTS = "reports"
        const val SETTINGS = "settings"
        const val BACKUP = "backup"
        const val MORE = "more"
        const val SEARCH = "search"
        const val RECEIPT_PREVIEW = "receipt/{id}"
    }
}