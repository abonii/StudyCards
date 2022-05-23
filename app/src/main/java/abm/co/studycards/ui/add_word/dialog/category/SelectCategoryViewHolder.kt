package abm.co.studycards.ui.add_word.dialog.category

import abm.co.studycards.databinding.ItemSelectCategoryBinding
import abm.co.studycards.domain.model.CategorySelectable
import androidx.recyclerview.widget.RecyclerView

class SelectCategoryViewHolder(
    private val binding: ItemSelectCategoryBinding,
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

    fun bind(currentItem: CategorySelectable) {
        binding.run {
            category.text = currentItem.category.name
            if (currentItem.isSelected)
                radio.isChecked = true
            else {
                radioGroup.clearCheck()
            }
        }
    }

}