package abm.co.feature.book.utils

import abm.co.feature.book.reader.model.ImageEntityUI
import android.os.Build
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.view.View
import android.widget.TextView
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

fun List<ImageEntityUI>.toImageGetter() = Html.ImageGetter { src ->
    return@ImageGetter try {
        val imageEntity = this.find { it.path == src }
        imageEntity?.let {
            toDecodedImage(imageEntity.encodedImage)
        }
    } catch (e: Exception) {
        null
    }
}

fun TextView.setSelectableTranslationText(
    wholeText: CharSequence,
    texts: List<String> = wholeText.split(" ", "\n"),
    onWordClick: (String) -> Unit,
    color: Color
) {
    text = wholeText
    var wholeText2 = wholeText
    texts.dropLastWhile { it.isBlank() }.forEach { word ->
        wholeText2 = makeClickable(
            word = word,
            onWordClick = onWordClick,
            wholeText = wholeText2,
            color = color
        )
    }
}

fun TextView.makeClickable(
    word: String,
    wholeText: CharSequence,
    onWordClick: (String) -> Unit,
    color: Color
): CharSequence {
    val spannableString = SpannableString(this.text)
    var startIndexOfLink = 0
    if (word.isNotEmpty()) {
        val clickableSpan = object : TouchableSpan(color.toArgb()) {
            override fun onClick(view: View) {
                onWordClick(word)
            }
        }
        startIndexOfLink = wholeText.indexOf(
            string = word, startIndex = startIndexOfLink
        ).coerceAtLeast(minimumValue = 0)
        spannableString.setSpan(
            clickableSpan,
            startIndexOfLink,
            (startIndexOfLink + word.length).coerceAtMost(maximumValue = wholeText.length - 1),
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    this.movementMethod = LinkTouchMovementMethod()
    this.setText(spannableString, TextView.BufferType.SPANNABLE)
    return wholeText.replaceRange(startIndexOfLink until startIndexOfLink + word.length,
        buildString {
            repeat(word.length) {
                this.append("_")
            }
        })
}

fun CharSequence.toHtml(imageGetter: Html.ImageGetter? = null): CharSequence =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(
            this.toString(), Html.FROM_HTML_MODE_COMPACT, imageGetter, null
        )
    } else {
        Html.fromHtml(this.toString())
    }

