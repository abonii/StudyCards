package abm.co.studycards.ui.explore

import abm.co.studycards.data.model.vocabulary.Category
import abm.co.studycards.databinding.ItemParentExploreBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class ParentExploreAdapter(private val onClickItem: (Category) -> Unit) :
    ListAdapter<ParentExploreUI, ParentExploreAdapter.ViewHolder>(DIFF_UTIL) {

    inner class ViewHolder(
        val binding: ItemParentExploreBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(currentItem: ParentExploreUI) {
            binding.run {
                when (currentItem) {
                    is ParentExploreUI.SetUI -> {
                        val childAdapter =
                            ChildExploreAdapter(onClickItem).apply { submitList(currentItem.value) }
                        childRV.adapter = childAdapter
                        category.text = currentItem.title
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemParentExploreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<ParentExploreUI>() {
            override fun areItemsTheSame(
                oldItem: ParentExploreUI,
                newItem: ParentExploreUI
            ): Boolean =
                oldItem is ParentExploreUI.SetUI && newItem is ParentExploreUI.SetUI && oldItem.title == newItem.title


            override fun areContentsTheSame(
                oldItem: ParentExploreUI,
                newItem: ParentExploreUI
            ): Boolean =
                oldItem == newItem
        }
    }

}