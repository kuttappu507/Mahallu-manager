package com.mahallu.manager.feature.reports.pdf

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.pdf.PdfDocument
import dagger.hilt.android.qualifiers.ApplicationContext
import feature.reports.feature.reports.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

data class PdfTextLine(val text: String, val sizeSp: Float = 12f, val bold: Boolean = false, val align: Align = Align.LEFT, val color: Int = Color.BLACK, val gapBefore: Float = 0f)
data class PdfTable(val headers: List<String>, val rows: List<List<String>>, val columnWeights: List<Float> = emptyList())
enum class Align { LEFT, CENTER, RIGHT }

data class PdfDocumentSpec(
    val title: String,
    val subtitle: String? = null,
    val lines: List<PdfTextLine> = emptyList(),
    val table: PdfTable? = null,
    val footer: String? = null,
    val pageWidth: Int = 595, // A4 portrait
    val pageHeight: Int = 842
)

@Singleton
class PdfGenerator @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun generate(fileName: String, spec: PdfDocumentSpec): File = withContext(Dispatchers.IO) {
        val outDir = File(context.getExternalFilesDir(null), "pdfs").apply { mkdirs() }
        val outFile = File(outDir, fileName)
        val doc = PdfDocument()

        val margin = 40
        val pageWidth = spec.pageWidth
        val pageHeight = spec.pageHeight
        val contentWidth = pageWidth - margin * 2

        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#4F46E5")
            textSize = 22f
            isFakeBoldText = true
        }
        val subtitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.DKGRAY
            textSize = 12f
        }
        val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#4F46E5")
            strokeWidth = 2f
        }
        val footerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.GRAY
            textSize = 9f
        }

        var pageNumber = 1
        var page = doc.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create())
        var canvas = page.canvas
        var y = margin

        // Header
        canvas.drawText(spec.title, margin.toFloat(), (y + 22).toFloat(), titlePaint)
        y += 30
        if (!spec.subtitle.isNullOrBlank()) {
            canvas.drawText(spec.subtitle, margin.toFloat(), (y + 12).toFloat(), subtitlePaint)
            y += 16
        }
        canvas.drawLine(margin.toFloat(), y.toFloat(), (pageWidth - margin).toFloat(), y.toFloat(), linePaint)
        y += 18

        // Body lines
        for (line in spec.lines) {
            val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = line.color
                textSize = line.sizeSp
                isFakeBoldText = line.bold
            }
            y += line.gapBefore.toInt()
            val wrapped = wrap(line.text, paint, contentWidth.toFloat())
            for (chunk in wrapped) {
                if (y > pageHeight - margin - 30) {
                    drawFooter(canvas, pageWidth, pageHeight, margin, spec.footer, footerPaint)
                    doc.finishPage(page)
                    pageNumber++
                    page = doc.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create())
                    canvas = page.canvas
                    y = margin
                }
                val x = when (line.align) {
                    Align.LEFT -> margin.toFloat()
                    Align.CENTER -> (pageWidth / 2f) - (paint.measureText(chunk) / 2f)
                    Align.RIGHT -> (pageWidth - margin).toFloat() - paint.measureText(chunk)
                }
                canvas.drawText(chunk, x, (y + line.sizeSp).toFloat(), paint)
                y += (line.sizeSp + 6).toInt()
            }
        }

        // Table
        if (spec.table != null) {
            val tbl = spec.table
            val weights = if (tbl.columnWeights.isEmpty()) {
                List(tbl.headers.size) { 1f / tbl.headers.size }
            } else tbl.columnWeights

            val cellPadding = 4f
            val cellPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                textSize = 9.5f
                isFakeBoldText = false
            }
            val headerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.WHITE
                textSize = 10f
                isFakeBoldText = true
            }
            val headerBg = Paint().apply { color = Color.parseColor("#4F46E5") }
            val rowBg = Paint().apply { color = Color.parseColor("#F8FAFC") }
            val border = Paint().apply {
                color = Color.parseColor("#E5E7EB")
                style = Paint.Style.STROKE
                strokeWidth = 0.5f
            }

            val colWidths = weights.map { it * contentWidth }
            val headerHeight = 24f
            val rowHeight = 22f

            fun newPageIfNeeded(needed: Int): Boolean {
                if (y + needed > pageHeight - margin - 30) {
                    drawFooter(canvas, pageWidth, pageHeight, margin, spec.footer, footerPaint)
                    doc.finishPage(page)
                    pageNumber++
                    page = doc.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create())
                    canvas = page.canvas
                    y = margin
                    return true
                }
                return false
            }

            // Header row
            newPageIfNeeded(headerHeight.toInt())
            var xPos = margin.toFloat()
            canvas.drawRect(margin.toFloat(), y.toFloat(), (pageWidth - margin).toFloat(), (y + headerHeight).toFloat(), headerBg)
            for (i in tbl.headers.indices) {
                val rect = Rect(xPos.toInt(), y.toInt(), (xPos + colWidths[i]).toInt(), (y + headerHeight).toInt())
                canvas.drawText(tbl.headers[i], (rect.left + cellPadding), (rect.bottom - cellPadding - 2), headerPaint)
                xPos += colWidths[i]
            }
            canvas.drawRect(margin.toFloat(), y.toFloat(), (pageWidth - margin).toFloat(), (y + headerHeight).toFloat(), border)
            y += headerHeight.toInt()

            for ((idx, row) in tbl.rows.withIndex()) {
                newPageIfNeeded(rowHeight.toInt())
                if (idx % 2 == 0) {
                    canvas.drawRect(margin.toFloat(), y.toFloat(), (pageWidth - margin).toFloat(), (y + rowHeight).toFloat(), rowBg)
                }
                xPos = margin.toFloat()
                for (i in row.indices) {
                    val text = row[i]
                    val clipped = clipToWidth(text, cellPaint, colWidths[i] - cellPadding * 2)
                    val rect = Rect(xPos.toInt(), y.toInt(), (xPos + colWidths[i]).toInt(), (y + rowHeight).toInt())
                    canvas.drawText(clipped, (rect.left + cellPadding), (rect.bottom - cellPadding - 2), cellPaint)
                    xPos += colWidths[i]
                }
                canvas.drawRect(margin.toFloat(), y.toFloat(), (pageWidth - margin).toFloat(), (y + rowHeight).toFloat(), border)
                y += rowHeight.toInt()
            }
        }

        drawFooter(canvas, pageWidth, pageHeight, margin, spec.footer, footerPaint)
        doc.finishPage(page)

        FileOutputStream(outFile).use { doc.writeTo(it) }
        doc.close()
        outFile
    }

    private fun drawFooter(canvas: android.graphics.Canvas, pageWidth: Int, pageHeight: Int, margin: Int, footer: String?, paint: Paint) {
        val txt = footer ?: context.getString(R.string.reports_footer_generated, java.util.Date().toString())
        canvas.drawText(txt, margin.toFloat(), (pageHeight - margin / 2).toFloat(), paint)
    }

    private fun wrap(text: String, paint: Paint, maxWidth: Float): List<String> {
        val words = text.split(" ")
        val out = mutableListOf<String>()
        var current = StringBuilder()
        for (w in words) {
            val candidate = if (current.isEmpty()) w else "$current $w"
            if (paint.measureText(candidate) > maxWidth && current.isNotEmpty()) {
                out.add(current.toString())
                current = StringBuilder(w)
            } else {
                current = StringBuilder(candidate)
            }
        }
        if (current.isNotEmpty()) out.add(current.toString())
        return out
    }

    private fun clipToWidth(text: String, paint: Paint, maxWidth: Float): String {
        if (paint.measureText(text) <= maxWidth) return text
        var end = text.length
        while (end > 0 && paint.measureText(text.substring(0, end) + "…") > maxWidth) end--
        return text.substring(0, end) + "…"
    }
}