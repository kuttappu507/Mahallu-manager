package com.mahallu.manager.feature.finance

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import feature.finance.feature.finance.R

@Composable
fun categoryLabel(category: String): String = when (category) {
    "SUBSCRIPTION" -> stringResource(R.string.finance_cat_subscription)
    "DONATION" -> stringResource(R.string.finance_cat_donation)
    "RENT" -> stringResource(R.string.finance_cat_rent)
    "OTHER_INCOME" -> stringResource(R.string.finance_cat_other_income)
    "SALARY" -> stringResource(R.string.finance_cat_salary)
    "ELECTRICITY" -> stringResource(R.string.finance_cat_electricity)
    "WATER" -> stringResource(R.string.finance_cat_water)
    "MAINTENANCE" -> stringResource(R.string.finance_cat_maintenance)
    "WELFARE" -> stringResource(R.string.finance_cat_welfare)
    "OTHER_EXPENSE" -> stringResource(R.string.finance_cat_other_expense)
    else -> category
}

@Composable
fun paymentLabel(payment: String): String = when (payment) {
    "CASH" -> stringResource(R.string.finance_pay_cash)
    "UPI" -> stringResource(R.string.finance_pay_upi)
    "BANK" -> stringResource(R.string.finance_pay_bank)
    "CHEQUE" -> stringResource(R.string.finance_pay_cheque)
    "OTHER" -> stringResource(R.string.finance_pay_other)
    else -> payment
}

@Composable
fun typeLabel(type: String): String = when (type) {
    "INCOME" -> stringResource(R.string.finance_type_income)
    "EXPENSE" -> stringResource(R.string.finance_type_expense)
    else -> type
}
