package abm.co.studycards.ui.add_word.dialog.category

import abm.co.studycards.databinding.ItemSelectCategoryBinding
import abm.co.studycards.domain.model.CategorySelectable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

class SelectCategoryAdapter :
    ListAdapter<CategorySelectable, SelectCategoryViewHolder>(DIFF_UTIL) {

    var selectedItemPos = -1
    private var lastItemSelectedPos = -1

    override fun onBindViewHolder(
        holder: SelectCategoryViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty()) {
            when (payloads[0]) {
                0 -> {
                    val item = getItem(position)
                    item.isSelected = !item.isSelected
                }
                2 -> {
                    val item = getItem(position)
                    item.isSelected = false
                }
            }
        }
        super.onBindViewHolder(holder, position, payloads)
    }

    private fun changeSelectedPosition(position: Int) {
        selectedItemPos = position
        when {
            lastItemSelectedPos != -1 && lastItemSelectedPos != selectedItemPos -> {
                notifyItemChanged(lastItemSelectedPos, 2)
            }
        }
        notifyItemChanged(selectedItemPos, 0)
        lastItemSelectedPos = position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectCategoryViewHolder {
        val binding =
            ItemSelectCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SelectCategoryViewHolder(binding) {
            changeSelectedPosition(it)
        }
    }

    override fun onBindViewHolder(holder: SelectCategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<CategorySelectable>() {
            override fun areItemsTheSame(
                oldItem: CategorySelectable,
                newItem: CategorySelectable
            ): Boolean =
                oldItem.category.id == newItem.category.id

            override fun areContentsTheSame(
                oldItem: CategorySelectable,
                newItem: CategorySelectable
            ): Boolean =
                oldItem == newItem
        }
    }

}