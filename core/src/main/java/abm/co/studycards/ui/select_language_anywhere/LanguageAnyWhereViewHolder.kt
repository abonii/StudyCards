package abm.co.studycards.ui.select_language_anywhere

import abm.co.studycards.R
import abm.co.studycards.databinding.ItemLanguageAnyWhereBinding
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class LanguageAnyWhereViewHolder(
    private val binding: ItemLanguageAnyWhereBinding,
    onItemClicked: (Int) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private val selectedBackgroundColor =
        ContextCompat.getColor(binding.root.context, R.color.select_languages_card_background)
    private val defaultBackgroundColor =
        ContextCompat.getColor(binding.root.context, R.color.background)

    init {
        itemView.setOnClickListener {
            onItemClicked(absoluteAdapterPosition)
        }
    }

    fun bind(item: LanguageSelectable) = binding.run {
        if (item.isSelected) {
            selectedCardStroke()
        } else
            defaultCardStroke()
        name.text = item.language.getLanguageName(root.context)
        image.setImageDrawable(item.language.getDrawable(root.context))
    }

    private fun defaultCardStroke() = binding.run {
        card.setCardBackgroundColor(defaultBackgroundColor)
        card.strokeColor = defaultBackgroundColor
        checkbox.isChecked = false
    }

    private fun selectedCardStroke() = binding.run {
        card.strokeColor = selectedBackgroundColor
        card.setCardBackgroundColor(selectedBackgroundColor)
        checkbox.isChecked = true
    }
}