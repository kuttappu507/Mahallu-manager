package com.mahallu.manager.core.ui.util

import android.content.Context
import android.content.Intent
import android.net.Uri

fun dial(context: Context, number: String) {
    runCatching {
        context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${number.trim()}")))
    }
}

fun whatsapp(context: Context, number: String) {
    runCatching {
        val normalized = number.replace(Regex("[^\\d]"), "").trimStart('0')
        val uri = if (normalized.startsWith("91")) normalized else "91$normalized"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$uri"))
        intent.setPackage("com.whatsapp")
        context.startActivity(intent)
    }
}
