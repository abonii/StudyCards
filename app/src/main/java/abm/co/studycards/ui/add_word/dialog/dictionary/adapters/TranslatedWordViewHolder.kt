package abm.co.studycards.ui.add_word.dialog.dictionary.adapters

import abm.co.studycards.R
import abm.co.studycards.data.model.oxford.Example
import abm.co.studycards.data.model.oxford.Sense
import abm.co.studycards.data.model.oxford.Translation
import abm.co.studycards.databinding.ItemTranslatedWordBinding
import abm.co.studycards.helpers.LinkTouchMovementMethod
import abm.co.studycards.helpers.TouchableSpan
import abm.co.studycards.util.getMyColor
import android.text.SpannableString
import android.text.Spanned
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TranslatedWordViewHolder(
    private val binding: ItemTranslatedWordBinding,
    private val onExampleSelected: (example: String, isPressed: Boolean) -> Unit,
    private val onTranslationSelected: (example: String, isPressed: Boolean) -> Unit,
    private val countAdded:(Boolean)->String
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(currentItem: Sense) = binding.run {
        wordTrans.run {
            text = makeTranslations(currentItem)
            setSelectableTranslationText(this, currentItem)
        }
        examples.run {
            text = makeExamplesFromList(currentItem)
            setSelectableExampleText(this, currentItem)
        }
        number.text = countAdded(currentItem.translations != null)
    }

    private fun setSelectableExampleText(view: TextView, currentItem: Sense) {
        val currentExamples = currentItem.examples
        if (currentExamples != null) {
            for (t in currentExamples) {
                view.makeClickable(makeOneExampleFromList(t), onExampleSelected, true)
            }
        }
    }

    private fun setSelectableTranslationText(view: TextView, currentItem: Sense) {
        val currentTranslations = currentItem.translations
        if (currentTranslations != null) {
            for (t in currentTranslations) {
                view.makeClickable(
                    makeOneTranslationFromList(t),
                    onTranslationSelected,
                    false
                )
            }
        }
    }

    private fun makeTranslations(currentItem: Sense): String {
        if (currentItem.translations == null) {
            return "***"
        }
        return currentItem.translations.joinToString {
            "${it.getNormalTranslation()}"
        }
    }

    private fun makeExamplesFromList(currentItem: Sense): String? {
        return currentItem.examples?.joinToString(separator = "\n") {
            "${it.text} - ${it.translations?.get(0)?.getNormalTranslation()}"
        }
    }

    private fun makeOneExampleFromList(example: Example): String {
        return "${example.text} - ${example.translations?.get(0)?.getNormalTranslation()}"
    }

    private fun makeOneTranslationFromList(translation: Translation): String {
        return translation.getNormalTranslation().toString()
    }
}

fun TextView.makeClickable(
    text: String,
    onTextClick: (String, Boolean) -> Unit,
    isExample: Boolean
) {
    val spannableString = SpannableString(this.text)
    var startIndexOfLink = -1
    if (text.isNotEmpty()) {
        val defaultColor =
            if (isExample) getMyColor(R.color.secondTextColor)
            else getMyColor(R.color.textColor)
        val selectedColor =
            if (isExample) getMyColor(R.color.colorPrimary)
            else getMyColor(R.color.colorPrimaryDark)
        val clickableSpan = object : TouchableSpan(
            defaultColor,
            selectedColor
        ) {
            override fun onClick(view: View) {
                onTextClick(text, myPressed)
                setMyPressed()
            }

        }
        startIndexOfLink = this.text.toString().indexOf(text, startIndexOfLink + 1)
        spannableString.setSpan(
            clickableSpan, startIndexOfLink, startIndexOfLink + text.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    this.movementMethod =
        LinkTouchMovementMethod()
    this.setText(spannableString, TextView.BufferType.SPANNABLE)

}