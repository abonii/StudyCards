package abm.co.studycards.ui.explore

import abm.co.studycards.data.model.vocabulary.Category
import abm.co.studycards.databinding.ItemChildCategoryBinding
import androidx.recyclerview.widget.RecyclerView

class ChildCategoryViewHolder(val binding: ItemChildCategoryBinding, onClickItem: (Int) -> Unit) :
    RecyclerView.ViewHolder(binding.root) {

    init {
        itemView.setOnClickListener {
            onClickItem(absoluteAdapterPosition)
        }
    }

    fun bind(currentItem: Category) {
        binding.item = currentItem
    }
}