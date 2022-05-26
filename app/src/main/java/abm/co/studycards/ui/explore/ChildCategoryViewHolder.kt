package abm.co.studycards.ui.explore

import abm.co.studycards.databinding.ItemChildCategoryBinding
import abm.co.studycards.domain.model.Category
import abm.co.studycards.util.GeneralBindingAdapters.setImageWithGlide
import androidx.recyclerview.widget.RecyclerView

class ChildCategoryViewHolder(val binding: ItemChildCategoryBinding, onClickItem: (Int) -> Unit) :
    RecyclerView.ViewHolder(binding.root) {

    init {
        itemView.setOnClickListener {
            onClickItem(absoluteAdapterPosition)
        }
    }

    fun bind(currentItem: Category) {
        binding.title.text = currentItem.name
        binding.image.setImageWithGlide(currentItem.imageUrl)
    }
}