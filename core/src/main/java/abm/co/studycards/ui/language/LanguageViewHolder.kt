package abm.co.studycards.ui.language

import abm.co.studycards.R
import abm.co.studycards.domain.model.Language
import abm.co.studycards.databinding.ItemLanguageBinding
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class LanguageViewHolder(
    private val binding: ItemLanguageBinding,
    onClicked:(Int)->Unit
) :
    RecyclerView.ViewHolder(binding.root) {

    init {
        binding.root.setOnClickListener {
            onClicked(absoluteAdapterPosition)
        }
    }

    fun bind(currentItem: Language) {
        binding.run {
            item.run {
                text = currentItem.getLanguageName(root.context)
            }
            image.setImageDrawable(
                currentItem.getDrawable(binding.root.context)
            )
        }
    }

    fun defaultCardStroke(view: TextView = binding.item) {
        view.setTextColor(ContextCompat.getColor(view.context, R.color.textColor))
    }

    fun selectedCardStroke() {
        binding.item.setTextColor(ContextCompat.getColor(binding.root.context, R.color.colorPrimaryDark))
    }
}