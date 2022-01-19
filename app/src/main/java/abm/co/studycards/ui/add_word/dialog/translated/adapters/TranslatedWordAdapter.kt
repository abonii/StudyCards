package abm.co.studycards.ui.add_word.dialog.translated.adapters

import abm.co.studycards.R
import abm.co.studycards.data.model.oxford.Example
import abm.co.studycards.data.model.oxford.Sense
import abm.co.studycards.data.model.oxford.Translation
import abm.co.studycards.databinding.ItemTranslatedWordBinding
import abm.co.studycards.helpers.LinkTouchMovementMethod
import abm.co.studycards.helpers.TouchableSpan
import abm.co.studycards.util.getMyColor
import android.annotation.SuppressLint
import android.text.SpannableString
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class TranslatedWordAdapter(val listener: OnCheckBoxClicked) :
    RecyclerView.Adapter<TranslatedWordAdapter.ViewHolder>() {

    var items: List<Sense> = ArrayList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var countNumber = 1

    inner class ViewHolder(private val binding: ItemTranslatedWordBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(currentItem: Sense) {
            binding.apply {
                wordTrans.apply {
                    text = makeTranslations(currentItem)
                    setSelectableTranslationText(this, currentItem)
                }
                examples.apply {
                    text = makeExamplesFromList(currentItem)
                    setSelectableExampleText(this, currentItem)
                }
                number.text = if (currentItem.translations == null) {
                    ""
                } else "${(countNumber++)}."
            }
        }

        private fun setSelectableExampleText(view: TextView, currentItem: Sense) {
            val currentExamples = currentItem.examples
            if (currentExamples != null) {
                for (t in currentExamples) {
                    view.makeClickable(makeOneExampleFromList(t), listener, true)
                }
            }
        }

        private fun setSelectableTranslationText(view: TextView, currentItem: Sense) {
            val currentTranslations = currentItem.translations
            if (currentTranslations != null) {
                for (t in currentTranslations) {
                    view.makeClickable(makeOneTranslationFromList(t), listener, false)
                }
            }
        }

        private fun makeTranslations(currentItem: Sense): String {
            if (currentItem.translations == null) {
                return "***"
            }
            return currentItem.translations.joinToString {
                "${it.text}"
            }
        }

        private fun makeExamplesFromList(currentItem: Sense): String? {
            return currentItem.examples?.joinToString(separator = "\n") {
                "${it.text} - ${it.translations?.get(0)?.text}"
            }
        }

        private fun makeOneExampleFromList(example: Example): String {
            return "${example.text} - ${example.translations?.get(0)?.text}"
        }

        private fun makeOneTranslationFromList(translation: Translation): String {
            return translation.text.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTranslatedWordBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = items[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    interface OnCheckBoxClicked {
        fun onExampleSelected(example: String, isPressed: Boolean)
        fun onTranslationSelected(translation: String, isPressed: Boolean)
    }
}

fun TextView.makeClickable(
    text: String,
    listener: TranslatedWordAdapter.OnCheckBoxClicked,
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
                if (isExample) {
                    listener.onExampleSelected(text, myPressed)
                } else {
                    listener.onTranslationSelected(text, myPressed)
                }
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

