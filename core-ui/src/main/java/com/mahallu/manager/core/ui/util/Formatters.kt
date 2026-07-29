package com.mahallu.manager.core.ui.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object Formatters {
    private val indianRupeeFormat: NumberFormat = NumberFormat.getNumberInstance(Locale("en", "IN"))
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private val dateTimeFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    private val isoDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val displayDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    fun currency(amount: Double): String = "₹" + indianRupeeFormat.format(amount)

    fun currencyShort(amount: Double): String {
        return when {
            amount >= 1_00_00_000 -> "₹" + String.format(Locale.getDefault(), "%.1fCr", amount / 1_00_00_000)
            amount >= 1_00_000 -> "₹" + String.format(Locale.getDefault(), "%.1fL", amount / 1_00_000)
            amount >= 1_000 -> "₹" + String.format(Locale.getDefault(), "%.1fK", amount / 1_000)
            else -> "₹" + indianRupeeFormat.format(amount)
        }
    }

    fun date(timestamp: Long): String = dateFormat.format(Date(timestamp))
    fun dateTime(timestamp: Long): String = dateTimeFormat.format(Date(timestamp))
    fun isoDate(timestamp: Long): String = isoDateFormat.format(Date(timestamp))
    fun displayDate(timestamp: Long): String = displayDateFormat.format(Date(timestamp))

    fun relativeDate(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        return when {
            diff < 60_000 -> "Just now"
            diff < 3_600_000 -> "${diff / 60_000}m ago"
            diff < 86_400_000 -> "${diff / 3_600_000}h ago"
            diff < 604_800_000 -> "${diff / 86_400_000}d ago"
            else -> date(timestamp)
        }
    }

    fun calculateAge(dobMillis: Long): Int {
        val dob = Calendar.getInstance().apply { timeInMillis = dobMillis }
        val today = Calendar.getInstance()
        var age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) age--
        return age
    }

    fun greeting(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 5..11 -> "Assalamu Alaikum"
            in 12..16 -> "Good Afternoon"
            in 17..20 -> "Good Evening"
            else -> "Good Night"
        }
    }
}