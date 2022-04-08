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
//        val newWidth = (Resources.getSystem().displayMetrics.widthPixels -
//                (MARGIN_BTN_CARDS * CARD_COUNT_IN_ONE_PAGE).px() -
//                LEFT_PADDING.px() - MARGIN_BTN_CARDS.px()) / CARD_COUNT_IN_ONE_PAGE

//        binding.run {
//            holder.run {
//                layoutParams = layoutParams.apply {
//                    width = newWidth.toInt() + 4 //it's for margin
//                } // changed width
//            }
//            card.run {
//                layoutParams =
//                    layoutParams.apply {
//                        width = newWidth.toInt()
//                        height = (newWidth * 1.6).toInt()
//                    } //changed height
//            }
//        }
    }
}