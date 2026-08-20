package com.mahallu.manager.core.ui.util

import android.content.Context
import com.mahallu.manager.core.ui.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object Formatters {
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private val dateWithWeekdayFormat = SimpleDateFormat("EEE, MMM d yyyy", Locale.getDefault())
    private val dateTimeFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    private val isoDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val displayDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    fun currency(amount: Double): String = "₹" + indianGrouped(amount)

    fun currencyShort(amount: Double, context: Context? = null): String {
        val rupee = "₹"
        return when {
            amount >= 1_00_00_000 -> rupee + String.format(Locale.getDefault(), "%.1f", amount / 1_00_00_000) +
                (context?.getString(R.string.suffix_crore) ?: "Cr")
            amount >= 1_00_000 -> rupee + String.format(Locale.getDefault(), "%.1f", amount / 1_00_000) +
                (context?.getString(R.string.suffix_lakh) ?: "L")
            amount >= 1_000 -> rupee + String.format(Locale.getDefault(), "%.1f", amount / 1_000) +
                (context?.getString(R.string.suffix_thousand) ?: "K")
            else -> rupee + indianGrouped(amount)
        }
    }

    private fun indianGrouped(amount: Double): String {
        val negative = amount < 0
        val plain = String.format(Locale.US, "%.2f", Math.abs(amount))
        val intPart = plain.substringBefore('.')
        val frac = plain.substringAfter('.', "").trimEnd('0')
        val grouped = StringBuilder(intPart.takeLast(3))
        var idx = intPart.length - 3
        while (idx > 0) {
            val from = maxOf(0, idx - 2)
            grouped.insert(0, intPart.substring(from, idx) + ",")
            idx = from
        }
        val result = if (frac.isEmpty()) grouped.toString() else "$grouped.$frac"
        return if (negative) "-$result" else result
    }

    fun date(timestamp: Long): String = dateFormat.format(Date(timestamp))
    fun dateWithWeekday(timestamp: Long): String = dateWithWeekdayFormat.format(Date(timestamp))
    fun dateTime(timestamp: Long): String = dateTimeFormat.format(Date(timestamp))
    fun isoDate(timestamp: Long): String = isoDateFormat.format(Date(timestamp))
    fun displayDate(timestamp: Long): String = displayDateFormat.format(Date(timestamp))

    fun relativeDate(timestamp: Long, context: Context? = null): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        return when {
            diff < 60_000 -> context?.getString(R.string.time_just_now) ?: "Just now"
            diff < 3_600_000 -> context?.getString(R.string.time_ago_minutes, diff / 60_000) ?: "${diff / 60_000}m ago"
            diff < 86_400_000 -> context?.getString(R.string.time_ago_hours, diff / 3_600_000) ?: "${diff / 3_600_000}h ago"
            diff < 604_800_000 -> context?.getString(R.string.time_ago_days, diff / 86_400_000) ?: "${diff / 86_400_000}d ago"
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

    fun initials(name: String): String {
        val parts = name.trim().split("\\s+".toRegex()).filter { it.isNotEmpty() }
        return when {
            parts.isEmpty() -> "?"
            parts.size == 1 -> parts.first().take(2).uppercase(Locale.getDefault())
            else -> (parts.first().first().toString() + parts.last().first()).uppercase(Locale.getDefault())
        }
    }

    fun greeting(context: Context? = null): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 5..11 -> context?.getString(R.string.greeting_morning) ?: "Assalamu Alaikum"
            in 12..16 -> context?.getString(R.string.greeting_afternoon) ?: "Good Afternoon"
            in 17..20 -> context?.getString(R.string.greeting_evening) ?: "Good Evening"
            else -> context?.getString(R.string.greeting_night) ?: "Good Night"
        }
    }
}