package com.mahallu.manager.domain.usecase.certificate

import android.content.Context
import android.graphics.pdf.PdfDocument
import android.os.ParcelFileDescriptor
import com.mahallu.manager.domain.model.*
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class GenerateMembershipCertificateUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend operator fun invoke(member: Member, outputFilePath: String): Boolean {
        return try {
            val file = File(outputFilePath)
            val fd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_CREATE or ParcelFileDescriptor.MODE_WRITE)
            val document = PdfDocument()
            
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = document.startPage(pageInfo)
            val canvas = page.canvas
            
            // Draw certificate content
            val paint = android.graphics.Paint().apply {
                color = android.graphics.Color.BLACK
                textSize = 24f
                textAlign = android.graphics.Paint.Align.CENTER
            }
            
            canvas.drawText("MEMBERSHIP CERTIFICATE", 297f, 100f, paint)
            paint.textSize = 16f
            canvas.drawText("This is to certify that", 297f, 150f, paint)
            paint.textSize = 20f
            canvas.drawText(member.name, 297f, 190f, paint)
            paint.textSize = 16f
            canvas.drawText("is a registered member of our Mahallu", 297f, 230f, paint)
            canvas.drawText("Member ID: ${member.memberId}", 297f, 270f, paint)
            canvas.drawText("Date: ${java.text.SimpleDateFormat("dd MMMM yyyy", java.util.Locale.getDefault()).format(java.util.Date())}", 297f, 310f, paint)
            
            document.finishPage(page)
            document.writeTo(FileOutputStream(fd.fileDescriptor))
            document.close()
            fd.close()
            
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}

class GenerateMarriageCertificateUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend operator fun invoke(marriage: Marriage, outputFilePath: String): Boolean {
        return try {
            val file = File(outputFilePath)
            val fd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_CREATE or ParcelFileDescriptor.MODE_WRITE)
            val document = PdfDocument()
            
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = document.startPage(pageInfo)
            val canvas = page.canvas
            
            val paint = android.graphics.Paint().apply {
                color = android.graphics.Color.BLACK
                textSize = 24f
                textAlign = android.graphics.Paint.Align.CENTER
            }
            
            canvas.drawText("MARRIAGE CERTIFICATE", 297f, 100f, paint)
            paint.textSize = 16f
            canvas.drawText("Registration No: ${marriage.registrationNumber}", 297f, 150f, paint)
            canvas.drawText("Groom: ${marriage.groomName}", 297f, 190f, paint)
            canvas.drawText("Bride: ${marriage.brideName}", 297f, 230f, paint)
            canvas.drawText("Date of Nikah: ${marriage.nikahDate}", 297f, 270f, paint)
            
            document.finishPage(page)
            document.writeTo(FileOutputStream(fd.fileDescriptor))
            document.close()
            fd.close()
            
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}

class GenerateDeathCertificateUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend operator fun invoke(death: Death, outputFilePath: String): Boolean {
        return try {
            val file = File(outputFilePath)
            val fd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_CREATE or ParcelFileDescriptor.MODE_WRITE)
            val document = PdfDocument()
            
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = document.startPage(pageInfo)
            val canvas = page.canvas
            
            val paint = android.graphics.Paint().apply {
                color = android.graphics.Color.BLACK
                textSize = 24f
                textAlign = android.graphics.Paint.Align.CENTER
            }
            
            canvas.drawText("DEATH CERTIFICATE", 297f, 100f, paint)
            paint.textSize = 16f
            canvas.drawText("Name: ${death.name}", 297f, 150f, paint)
            canvas.drawText("Father's Name: ${death.fatherName}", 297f, 190f, paint)
            canvas.drawText("Date of Death: ${death.dateOfDeath}", 297f, 230f, paint)
            canvas.drawText("Burial Date: ${death.burialDate}", 297f, 270f, paint)
            
            document.finishPage(page)
            document.writeTo(FileOutputStream(fd.fileDescriptor))
            document.close()
            fd.close()
            
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}

class GenerateResidenceCertificateUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend operator fun invoke(family: Family, member: Member, outputFilePath: String): Boolean {
        return try {
            val file = File(outputFilePath)
            val fd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_CREATE or ParcelFileDescriptor.MODE_WRITE)
            val document = PdfDocument()
            
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = document.startPage(pageInfo)
            val canvas = page.canvas
            
            val paint = android.graphics.Paint().apply {
                color = android.graphics.Color.BLACK
                textSize = 24f
                textAlign = android.graphics.Paint.Align.CENTER
            }
            
            canvas.drawText("RESIDENCE CERTIFICATE", 297f, 100f, paint)
            paint.textSize = 16f
            canvas.drawText("This is to certify that", 297f, 150f, paint)
            paint.textSize = 20f
            canvas.drawText(member.name, 297f, 190f, paint)
            paint.textSize = 16f
            canvas.drawText("resides at", 297f, 230f, paint)
            canvas.drawText("${family.houseName}, ${family.address}", 297f, 270f, paint)
            canvas.drawText("Family No: ${family.familyNumber}", 297f, 310f, paint)
            
            document.finishPage(page)
            document.writeTo(FileOutputStream(fd.fileDescriptor))
            document.close()
            fd.close()
            
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
