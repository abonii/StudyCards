package abm.co.studycards.ui.add_word.dialog.dictionary.adapters

import abm.co.studycards.R
import abm.co.studycards.databinding.ItemTranslatedWordBinding
import abm.co.studycards.domain.model.CategoryDetails
import abm.co.studycards.util.helpers.LinkTouchMovementMethod
import abm.co.studycards.util.helpers.TouchableSpan
import abm.co.studycards.util.Constants.EXAMPLES_SEPARATOR
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
    private val countAdded: (Boolean) -> String
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(currentItem: CategoryDetails) = binding.run {
        wordTrans.run {
            text = makeTranslations(currentItem)
            setSelectableTranslationText(this, currentItem)
        }
        examples.run {
            text = currentItem.examples.joinToString(EXAMPLES_SEPARATOR)
            setSelectableExampleText(this, currentItem)
        }
        number.text = countAdded(currentItem.translations.isNotEmpty())
    }

    private fun setSelectableExampleText(view: TextView, currentItem: CategoryDetails) {
        val currentExamples = currentItem.examples
        if (currentExamples.isNotEmpty()) {
            for (t in currentExamples) {
                view.makeClickable(t, onExampleSelected, true)
            }
        }
    }

    private fun setSelectableTranslationText(view: TextView, currentItem: CategoryDetails) {
        val currentTranslations = currentItem.translations
        if (currentTranslations.isNotEmpty()) {
            for (t in currentTranslations) {
                view.makeClickable(
                    t,
                    onTranslationSelected,
                    false
                )
            }
        }
    }

    private fun makeTranslations(currentItem: CategoryDetails): String {
        if (currentItem.translations.isEmpty()) {
            return "______________"
        }
        return currentItem.translations.joinToString()
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