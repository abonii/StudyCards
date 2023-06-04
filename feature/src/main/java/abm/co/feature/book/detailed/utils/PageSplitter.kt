package abm.co.feature.book.detailed.utils

import abm.co.feature.book.reader.model.ImageEntityUI
import android.text.SpannableStringBuilder
import android.text.TextPaint
import java.util.regex.Pattern

class ChapterWithTitle(
    val title: String,
    val url: String,
    val chapterText: CharSequence
)

class PageSplitter(
    private val pageWidth: Int,
    private val pageHeight: Int,
    private val images: List<ImageEntityUI>,
    private val textPaint: TextPaint,
    lineSpacingExtra: Int = 0,
    lineSpacingMultiplier: Float = 1f
) {
    private val pages: MutableList<ChapterWithTitle> = ArrayList()
    private var currentLine = StringBuilder()
    private var currentPage = StringBuilder()
    private var currentLineHeight = .0
    private var pageContentHeight = .0
    private var currentLineWidth = .0
    private val textLineHeight =
        (textPaint.getFontMetrics(null) * lineSpacingMultiplier + lineSpacingExtra).toDouble()
    private var currentTitle = ""
    private var currentURL = ""

    fun append(title: String, url: String, text: String) {
        currentTitle = title
        currentURL = url
        val paragraphsText = text.split("\n")
        paragraphsText.forEach { paragraph ->
            appendText(paragraph.trim())
            appendNewLine()
        }
        moveToNextPage(true)
    }

    private fun appendText(text: String) {
        val words = text.split(" ")
        words.forEach { word ->
            appendWord("$word ")
        }
    }

    private fun appendNewLine() {
        currentLine.append("<br>")
        appendLineToPage(textLineHeight)
        moveToNextPage()
    }

    private fun moveToNextPage(isPageEnd: Boolean = pageContentHeight + currentLineHeight > pageHeight) {
        if (isPageEnd) {
            pages.add(ChapterWithTitle(currentTitle, currentURL, currentPage))
            currentPage = StringBuilder()
            pageContentHeight = .0
        }
    }

    private fun appendWord(appendedText: String) {
        val textWidth = textPaint.measureText(appendedText)
        if (currentLineWidth + textWidth >= pageWidth) {
            appendLineToPage(textLineHeight)
            moveToNextPage()
        }
        appendTextToLine(appendedText, textWidth)
    }

    private fun appendImage(appendedText: String) {
        images.find {
            it.path == appendedText
        }?.encodedImage?.let {
            val bitmap = toDecodedImage(it).bitmap
            val height = bitmap.height
            val width = bitmap.width
            appendLineToPage(height.toDouble())
            moveToNextPage()
            appendTextToLine("", width.toFloat())
        }
    }

    private fun appendLineToPage(textLineHeight: Double) {
        currentPage.append(currentLine)
        pageContentHeight += currentLineHeight
        currentLine = StringBuilder()
        currentLineHeight = textLineHeight
        currentLineWidth = .0
    }

    private fun appendTextToLine(appendedText: String, textWidth: Float) {
        currentLineHeight = currentLineHeight.coerceAtLeast(textLineHeight)
        currentLine.append(appendedText)
        currentLineWidth += textWidth
    }

    fun getPages(): List<ChapterWithTitle> {
        val copyPages = ArrayList(pages)
        var lastPage = SpannableStringBuilder(currentPage)
        if (pageContentHeight + currentLineHeight > pageHeight) {
            copyPages.add(ChapterWithTitle(currentTitle, currentURL, lastPage))
            lastPage = SpannableStringBuilder()
        }
        lastPage.append(currentLine)
        copyPages.add(ChapterWithTitle(currentTitle, currentURL, lastPage))
        return copyPages
    }
}