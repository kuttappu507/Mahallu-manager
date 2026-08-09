package com.mahallu.manager.feature.donations

import androidx.annotation.StringRes
import feature.donations.feature.donations.R

fun donationCategoryLabelRes(category: String): Int = when (category) {
    "GENERAL" -> R.string.category_general
    "MASJID" -> R.string.category_masjid
    "BUILDING" -> R.string.category_building
    "EDUCATION" -> R.string.category_education
    "MEDICAL" -> R.string.category_medical
    "WELFARE" -> R.string.category_welfare
    "OTHER" -> R.string.category_other
    else -> R.string.category_general
}

fun donationPaymentLabelRes(payment: String): Int = when (payment) {
    "CASH" -> R.string.payment_cash
    "UPI" -> R.string.payment_upi
    "BANK" -> R.string.payment_bank
    "CHEQUE" -> R.string.payment_cheque
    "OTHER" -> R.string.payment_other
    else -> R.string.payment_other
}
