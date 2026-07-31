package com.mahallu.manager.core.ui.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File

object PdfShare {
    /**
     * Open a generated PDF file with the system's default PDF viewer.
     * Uses FileProvider to grant temporary URI permission to the viewer.
     */
    fun open(context: Context, file: File) {
        if (!file.exists()) {
            Toast.makeText(context, "PDF not found: ${file.absolutePath}", Toast.LENGTH_SHORT).show()
            return
        }
        val authority = "${context.packageName}.fileprovider"
        val uri: Uri = try {
            FileProvider.getUriForFile(context, authority, file)
        } catch (t: Throwable) {
            Toast.makeText(context, "Cannot share PDF: ${t.message}", Toast.LENGTH_LONG).show()
            return
        }
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(Intent.createChooser(intent, "Open PDF with"))
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(context, "No PDF viewer installed", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Share a generated PDF file via the system share sheet.
     */
    fun share(context: Context, file: File) {
        if (!file.exists()) {
            Toast.makeText(context, "PDF not found", Toast.LENGTH_SHORT).show()
            return
        }
        val authority = "${context.packageName}.fileprovider"
        val uri = try {
            FileProvider.getUriForFile(context, authority, file)
        } catch (t: Throwable) {
            Toast.makeText(context, "Cannot share: ${t.message}", Toast.LENGTH_LONG).show()
            return
        }
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share PDF"))
    }
}
