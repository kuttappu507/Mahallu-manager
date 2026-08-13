package com.mahallu.manager.feature.certificates.pdf

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import dagger.hilt.android.qualifiers.ApplicationContext
import feature.certificates.feature.certificates.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

data class PdfTextLine(val text: String, val sizeSp: Float = 12f, val bold: Boolean = false, val align: Align = Align.LEFT, val color: Int = Color.BLACK, val gapBefore: Float = 0f)
data class PdfTable(val headers: List<String>, val rows: List<List<String>>, val columnWeights: List<Float> = emptyList())
enum class Align { LEFT, CENTER, RIGHT }

/** Two side-by-side info panels (used for Groom / Bride details on the marriage certificate). */
data class PdfPanel(
    val title1: String,
    val rows1: List<Pair<String, String>>,
    val title2: String,
    val rows2: List<Pair<String, String>>
)

/** A titled section of label/value rows and/or plain text rows (Nikah details, Witnesses, Qazi...). */
data class PdfInfoBlock(
    val title: String,
    val keyValueRows: List<Pair<String, String>> = emptyList(),
    val textRows: List<String> = emptyList()
)

data class PdfDocumentSpec(
    val title: String,
    val subtitle: String? = null,
    val lines: List<PdfTextLine> = emptyList(),
    val table: PdfTable? = null,
    val footer: String? = null,
    val pageWidth: Int = 595, // A4 portrait
    val pageHeight: Int = 842,
    // Ornamental certificate layout (Design C)
    val ornament: Boolean = false,
    val ornamentColor: Int = Color.parseColor("#0F766E"),
    val goldColor: Int = Color.parseColor("#D9A441"),
    val goldTextColor: Int = Color.parseColor("#7A5C16"),
    val address: String? = null,
    val panels: List<PdfPanel> = emptyList(),
    val infoBlocks: List<PdfInfoBlock> = emptyList(),
    val signatureLabels: List<String> = emptyList(),
    val issuedLine: String? = null
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
        val ornament = spec.ornament
        val frameMargin = 24
        val textLeft = if (ornament) frameMargin + 14 else margin
        val contentWidth = pageWidth - textLeft * 2
        val contentBottom = if (ornament) pageHeight - (frameMargin + 18) else pageHeight - margin

        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#4F46E5")
            textSize = 22f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        }
        val subtitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.DKGRAY
            textSize = 12f
            typeface = Typeface.SERIF
        }
        val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#4F46E5")
            strokeWidth = 2f
        }
        val footerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.GRAY
            textSize = 9f
            typeface = Typeface.SERIF
        }

        // Ornamental certificate paints
        val orgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = spec.ornamentColor
            textSize = 17f
            letterSpacing = 0.18f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        }
        val addrPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#55666F")
            textSize = 13f
            typeface = Typeface.SERIF
        }
        val certTitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = spec.goldTextColor
            textSize = 28f
            letterSpacing = 0.08f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        }
        val panelHeaderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = spec.goldTextColor
            textSize = 14f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        }
        val sectionTitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = spec.goldTextColor
            textSize = 14f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        }
        val panelLabelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#55666F")
            textSize = 13f
            typeface = Typeface.SERIF
        }
        val panelValuePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#21303A")
            textSize = 13f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        }
        val sectionLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = spec.goldColor
            strokeWidth = 1f
        }
        val boxBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = spec.ornamentColor
            style = Paint.Style.STROKE
            strokeWidth = 1f
        }
        val panelBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#FBFDFC")
            style = Paint.Style.FILL
        }
        val dashPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#D8E2DF")
            strokeWidth = 1f
        }
        val signatureLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#21303A")
            strokeWidth = 1f
        }
        val signatureLabelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#21303A")
            textSize = 13.5f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        }
        val issuedPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = spec.ornamentColor
            textSize = 13.5f
            typeface = Typeface.SERIF
        }

        fun centeredX(text: String, paint: Paint): Float =
            (pageWidth / 2f) - (paint.measureText(text) / 2f)

        fun wrap(text: String, paint: Paint, maxWidth: Float): List<String> {
            if (maxWidth <= 0f || text.isEmpty()) return listOf(text)
            val out = mutableListOf<String>()
            var line = StringBuilder()
            val words = text.split(" ")
            for (word in words) {
                if (paint.measureText("$line $word") <= maxWidth || line.isEmpty()) {
                    if (line.isNotEmpty()) line.append(' ')
                    line.append(word)
                } else {
                    if (paint.measureText(word) > maxWidth) {
                        // Break an over-long word character-by-character
                        if (line.isNotEmpty()) { out.add(line.toString()); line = StringBuilder() }
                        val sb = StringBuilder()
                        for (ch in word) {
                            if (paint.measureText(sb.toString() + ch) <= maxWidth) {
                                sb.append(ch)
                            } else {
                                out.add(sb.toString())
                                sb.clear()
                                sb.append(ch)
                            }
                        }
                        if (sb.isNotEmpty()) line = StringBuilder(sb.toString())
                    } else {
                        out.add(line.toString())
                        line = StringBuilder(word)
                    }
                }
            }
            if (line.isNotEmpty()) out.add(line.toString())
            return out
        }

        fun clipToWidth(text: String, paint: Paint, maxWidth: Float): String {
            if (paint.measureText(text) <= maxWidth) return text
            var clipped = text
            while (clipped.length > 1 && paint.measureText("$clipped…") > maxWidth) {
                clipped = clipped.dropLast(1)
            }
            return "$clipped…"
        }

        fun drawFooter(canvas: android.graphics.Canvas) {
            val txt = spec.footer ?: context.getString(R.string.cert_pdf_default_footer, java.util.Date())
            val footerY = if (ornament) (pageHeight - 14f) else (pageHeight - margin / 2).toFloat()
            canvas.drawText(txt, margin.toFloat(), footerY, footerPaint)
        }

        fun drawCertificateFrame(canvas: android.graphics.Canvas) {
            val outer = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = spec.ornamentColor; style = Paint.Style.STROKE; strokeWidth = 1.4f
            }
            val inner = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = spec.ornamentColor; style = Paint.Style.STROKE; strokeWidth = 2.6f
            }
            val gold = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = spec.goldColor; style = Paint.Style.STROKE; strokeWidth = 1f
            }
            canvas.drawRect(frameMargin.toFloat(), frameMargin.toFloat(), (pageWidth - frameMargin).toFloat(), (pageHeight - frameMargin).toFloat(), outer)
            canvas.drawRect((frameMargin + 7).toFloat(), (frameMargin + 7).toFloat(), (pageWidth - frameMargin - 7).toFloat(), (pageHeight - frameMargin - 7).toFloat(), inner)
            canvas.drawRect((frameMargin + 12).toFloat(), (frameMargin + 12).toFloat(), (pageWidth - frameMargin - 12).toFloat(), (pageHeight - frameMargin - 12).toFloat(), gold)
            val diamond = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = spec.ornamentColor; style = Paint.Style.FILL
            }
            drawDiamond(canvas, frameMargin.toFloat(), frameMargin.toFloat(), 4f, diamond)
            drawDiamond(canvas, (pageWidth - frameMargin).toFloat(), frameMargin.toFloat(), 4f, diamond)
            drawDiamond(canvas, frameMargin.toFloat(), (pageHeight - frameMargin).toFloat(), 4f, diamond)
            drawDiamond(canvas, (pageWidth - frameMargin).toFloat(), (pageHeight - frameMargin).toFloat(), 4f, diamond)
        }

        var pageNumber = 1
        var page = doc.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create())
        var canvas = page.canvas
        var y = textLeft

        fun chrome() {
            if (ornament) drawCertificateFrame(canvas)
            drawFooter(canvas)
        }

        fun nextPage() {
            chrome()
            doc.finishPage(page)
            pageNumber++
            page = doc.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create())
            canvas = page.canvas
            y = textLeft
            chrome()
        }

        chrome()

        // Header
        if (ornament) {
            drawOrnament(canvas, pageWidth, y, spec.ornamentColor)
            y += 20
            if (!spec.subtitle.isNullOrBlank()) {
                val t = spec.subtitle.uppercase()
                orgPaint.textSize = 17f
                canvas.drawText(t, centeredX(t, orgPaint), (y + 17).toFloat(), orgPaint)
                y += 26
            }
            if (!spec.address.isNullOrBlank()) {
                canvas.drawText(spec.address, centeredX(spec.address, addrPaint), (y + 11.5f).toFloat(), addrPaint)
                y += 20
            }
            canvas.drawText(spec.title, centeredX(spec.title, certTitlePaint), (y + 24).toFloat(), certTitlePaint)
            y += 30
            canvas.drawLine(textLeft.toFloat(), y.toFloat(), (pageWidth - textLeft).toFloat(), y.toFloat(), sectionLinePaint)
            y += 16
        } else {
            canvas.drawText(spec.title, margin.toFloat(), (y + 22).toFloat(), titlePaint)
            y += 30
            if (!spec.subtitle.isNullOrBlank()) {
                canvas.drawText(spec.subtitle, margin.toFloat(), (y + 12).toFloat(), subtitlePaint)
                y += 16
            }
            canvas.drawLine(margin.toFloat(), y.toFloat(), (pageWidth - margin).toFloat(), y.toFloat(), linePaint)
            y += 18
        }

        // Body lines
        for (line in spec.lines) {
            val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = line.color
                textSize = line.sizeSp
                typeface = if (line.bold) Typeface.create(Typeface.SERIF, Typeface.BOLD) else Typeface.SERIF
            }
            y += line.gapBefore.toInt()
            val wrapped = wrap(line.text, paint, contentWidth.toFloat())
            for (chunk in wrapped) {
                if (y > contentBottom) nextPage()
                val x = when (line.align) {
                    Align.LEFT -> textLeft.toFloat()
                    Align.CENTER -> (pageWidth / 2f) - (paint.measureText(chunk) / 2f)
                    Align.RIGHT -> (pageWidth - textLeft).toFloat() - paint.measureText(chunk)
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
            }
            val headerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.WHITE
                textSize = 10f
                typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
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
                if (y + needed > contentBottom) {
                    nextPage()
                    return true
                }
                return false
            }

            newPageIfNeeded(headerHeight.toInt())
            var xPos = textLeft.toFloat()
            canvas.drawRect(textLeft.toFloat(), y.toFloat(), (pageWidth - textLeft).toFloat(), (y + headerHeight).toFloat(), headerBg)
            for (i in tbl.headers.indices) {
                val rect = Rect(xPos.toInt(), y.toInt(), (xPos + colWidths[i]).toInt(), (y + headerHeight).toInt())
                canvas.drawText(tbl.headers[i], (rect.left + cellPadding), (rect.bottom - cellPadding - 2), headerPaint)
                xPos += colWidths[i]
            }
            canvas.drawRect(textLeft.toFloat(), y.toFloat(), (pageWidth - textLeft).toFloat(), (y + headerHeight).toFloat(), border)
            y += headerHeight.toInt()

            for ((idx, row) in tbl.rows.withIndex()) {
                newPageIfNeeded(rowHeight.toInt())
                if (idx % 2 == 0) {
                    canvas.drawRect(textLeft.toFloat(), y.toFloat(), (pageWidth - textLeft).toFloat(), (y + rowHeight).toFloat(), rowBg)
                }
                xPos = textLeft.toFloat()
                for (i in row.indices) {
                    val text = row[i]
                    val clipped = clipToWidth(text, cellPaint, colWidths[i] - cellPadding * 2)
                    val rect = Rect(xPos.toInt(), y.toInt(), (xPos + colWidths[i]).toInt(), (y + rowHeight).toInt())
                    canvas.drawText(clipped, (rect.left + cellPadding), (rect.bottom - cellPadding - 2), cellPaint)
                    xPos += colWidths[i]
                }
                canvas.drawRect(textLeft.toFloat(), y.toFloat(), (pageWidth - textLeft).toFloat(), (y + rowHeight).toFloat(), border)
                y += rowHeight.toInt()
            }
        }

        // Panels (two side-by-side info boxes)
        for (panel in spec.panels) {
            val gap = 14f
            val colW = (contentWidth - gap) / 2f
            val headerH = 30f
            val rowH = 26f
            val bottomPad = 14f

            // Measure per-column height with value wrapping so nothing overflows.
            fun columnHeight(rows: List<Pair<String, String>>): Float {
                var h = headerH
                for ((label, value) in rows) {
                    val labelW = panelLabelPaint.measureText("$label:")
                    val avail = colW - labelW - 20f
                    val lines = wrap(value, panelValuePaint, avail)
                    h += lines.size * rowH
                }
                return h + bottomPad
            }
            val boxH = maxOf(columnHeight(panel.rows1), columnHeight(panel.rows2))
            if (y + boxH.toInt() + 24 > contentBottom) nextPage()

            fun drawColumn(px: Float, title: String, rows: List<Pair<String, String>>) {
                canvas.drawRoundRect(px, y.toFloat(), (px + colW).toFloat(), (y + boxH).toFloat(), 8f, 8f, panelBgPaint)
                canvas.drawRoundRect(px, y.toFloat(), (px + colW).toFloat(), (y + boxH).toFloat(), 8f, 8f, boxBorderPaint)
                val titleUp = title.uppercase()
                val tw = panelHeaderPaint.measureText(titleUp)
                canvas.drawText(titleUp, px + colW / 2f - tw / 2f, y + 17f, panelHeaderPaint)
                canvas.drawLine(px + 8f, y + 24f, px + colW - 8f, y + 24f, sectionLinePaint)
                var ry = y + 24f
                for ((label, value) in rows) {
                    val labelW = panelLabelPaint.measureText("$label:")
                    val avail = colW - labelW - 20f
                    val lines = wrap(value, panelValuePaint, avail)
                    ry += rowH
                    canvas.drawText("$label:", px + 10f, ry + 3f, panelLabelPaint)
                    if (lines.size == 1) {
                        val vx = px + colW - 10f - panelValuePaint.measureText(lines[0])
                        canvas.drawText(lines[0], vx, ry + 3f, panelValuePaint)
                    } else {
                        canvas.drawText(lines[0], px + 10f + labelW, ry + 3f, panelValuePaint)
                        for (i in 1 until lines.size) {
                            ry += rowH
                            canvas.drawText(lines[i], px + 10f, ry + 3f, panelValuePaint)
                        }
                    }
                    canvas.drawLine(px + 8f, ry + 9f, px + colW - 8f, ry + 9f, dashPaint)
                }
            }
            drawColumn(textLeft.toFloat(), panel.title1, panel.rows1)
            drawColumn(textLeft + colW + gap, panel.title2, panel.rows2)
            y += boxH.toInt() + 36
        }

        // Info blocks (titled sections)
        for (block in spec.infoBlocks) {
            if (y + 50 > contentBottom) nextPage()
            canvas.drawText(block.title, textLeft.toFloat(), y + 12f, sectionTitlePaint)
            canvas.drawLine(textLeft.toFloat(), y + 16f, (textLeft + contentWidth).toFloat(), y + 16f, sectionLinePaint)
            y += 30
            for ((label, value) in block.keyValueRows) {
                val labelW = panelLabelPaint.measureText("$label:")
                val avail = contentWidth - labelW - 20f
                val lines = wrap(value, panelValuePaint, avail)
                var drawn = 0
                for (chunk in lines) {
                    if (y + 26 > contentBottom) nextPage()
                    if (drawn == 0) {
                        canvas.drawText("$label:", textLeft.toFloat(), y + 12f, panelLabelPaint)
                        canvas.drawText(chunk, textLeft.toFloat() + labelW, y + 12f, panelValuePaint)
                    } else {
                        canvas.drawText(chunk, textLeft.toFloat(), y + 12f, panelValuePaint)
                    }
                    drawn++
                    y += 26
                }
                canvas.drawLine(textLeft.toFloat(), y - 8f, (textLeft + contentWidth).toFloat(), y - 8f, dashPaint)
            }
            for (t in block.textRows) {
                val wrappedT = wrap(t, panelValuePaint, contentWidth.toFloat())
                for (chunk in wrappedT) {
                    if (y + 24 > contentBottom) nextPage()
                    canvas.drawText(chunk, textLeft.toFloat(), y + 12f, panelValuePaint)
                    y += 24
                }
            }
            y += 24
        }

        // Signature row
        if (spec.signatureLabels.isNotEmpty()) {
            // Flex spacer: push the signature block toward the page bottom so
            // the ornamental certificate fills the A4 page like the preview.
            if (ornament) {
                val tailNeed = if (!spec.issuedLine.isNullOrBlank()) 84 else 44
                val spare = contentBottom - y - tailNeed
                if (spare > 40) {
                    y += (spare * 0.55).toInt()
                }
            }
            if (y + 44 > contentBottom) nextPage()
            y += 30
            val n = spec.signatureLabels.size
            val slotW = contentWidth / n
            for ((i, label) in spec.signatureLabels.withIndex()) {
                val sx = textLeft + i * slotW
                val lineStart = sx + 24f
                val lineEnd = sx + slotW - 24f
                canvas.drawLine(lineStart, y.toFloat(), lineEnd, y.toFloat(), signatureLinePaint)
                val lw = signatureLabelPaint.measureText(label)
                canvas.drawText(label, sx + slotW / 2f - lw / 2f, y + 18f, signatureLabelPaint)
            }
            y += 38
        }

        // Issued date/time line
        if (!spec.issuedLine.isNullOrBlank()) {
            if (y + 28 > contentBottom) nextPage()
            y += 16
            canvas.drawText(spec.issuedLine, centeredX(spec.issuedLine, issuedPaint), y.toFloat(), issuedPaint)
            y += 24
        }

        chrome()
        doc.finishPage(page)

        FileOutputStream(outFile).use { doc.writeTo(it) }
        doc.close()
        outFile
    }

    private fun drawOrnament(canvas: android.graphics.Canvas, pageWidth: Int, y: Int, teal: Int) {
        val lp = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = teal
            strokeWidth = 1.4f
        }
        val diamond = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = teal
            style = Paint.Style.FILL
        }
        val cx = pageWidth / 2f
        val lineHalf = 55f
        canvas.drawLine(cx - lineHalf, y.toFloat() + 6, cx - 13, y.toFloat() + 6, lp)
        canvas.drawLine(cx + 13, y.toFloat() + 6, cx + lineHalf, y.toFloat() + 6, lp)
        drawDiamond(canvas, cx, y.toFloat() + 6, 6f, diamond)
        drawDiamond(canvas, cx - 24, y.toFloat() + 6, 3.5f, diamond)
        drawDiamond(canvas, cx + 24, y.toFloat() + 6, 3.5f, diamond)
    }

    private fun drawDiamond(canvas: android.graphics.Canvas, cx: Float, cy: Float, r: Float, paint: Paint) {
        val p = Path()
        p.moveTo(cx, cy - r)
        p.lineTo(cx + r, cy)
        p.lineTo(cx, cy + r)
        p.lineTo(cx - r, cy)
        p.close()
        canvas.drawPath(p, paint)
    }
}
