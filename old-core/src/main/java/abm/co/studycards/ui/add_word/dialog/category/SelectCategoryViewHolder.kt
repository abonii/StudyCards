package abm.co.studycards.ui.add_word.dialog.category

import abm.co.studycards.databinding.ItemSelectCategoryBinding
import abm.co.studycards.domain.model.Category
import androidx.recyclerview.widget.RecyclerView

class SelectCategoryViewHolder(
    val binding: ItemSelectCategoryBinding,
    onClick: (Int) -> Unit
) :
    RecyclerView.ViewHolder(binding.root) {
    init {
        binding.radio.setOnClickListener {
            onClick(absoluteAdapterPosition)
        }
        binding.category.setOnClickListener {
            onClick(absoluteAdapterPosition)
        }
    }

    fun bind(currentItem: Category) {
        binding.run {
            category.text = currentItem.name
        }
    }

}