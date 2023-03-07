package abm.co.studycards.ui.explore

import abm.co.studycards.R
import abm.co.studycards.domain.model.Category
import abm.co.studycards.databinding.ItemChildCategoryBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class ChildExploreAdapter(private val onClickItem: (Category) -> Unit) :
    ListAdapter<ChildExploreVHUI, RecyclerView.ViewHolder>(DIFF_UTIL) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_child_category -> {
                val binding =
                    ItemChildCategoryBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                ChildCategoryViewHolder(binding) {
                    onClickItem((getItem(it) as ChildExploreVHUI.VHCategory).value)
                }
            }
            else -> {
                throw RuntimeException("not acceptable viewHolder type")
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val currentItem = getItem(position)) {
            is ChildExploreVHUI.VHCategory -> (holder as ChildCategoryViewHolder).bind(currentItem.value)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ChildExploreVHUI.VHCategory -> {
                R.layout.item_child_category
            }
        }
    }

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<ChildExploreVHUI>() {
            override fun areItemsTheSame(
                oldItem: ChildExploreVHUI,
                newItem: ChildExploreVHUI
            ): Boolean =
                oldItem is ChildExploreVHUI.VHCategory
                        && newItem is ChildExploreVHUI.VHCategory
                        && oldItem.value.id == newItem.value.id

            override fun areContentsTheSame(
                oldItem: ChildExploreVHUI,
                newItem: ChildExploreVHUI
            ): Boolean =
                oldItem == newItem
        }
    }
}

sealed class ChildExploreVHUI {
    class VHCategory(val value: Category) : ChildExploreVHUI()
}
