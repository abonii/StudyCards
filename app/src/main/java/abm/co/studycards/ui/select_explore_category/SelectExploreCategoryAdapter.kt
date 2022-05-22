package abm.co.studycards.ui.select_explore_category

import abm.co.studycards.R
import abm.co.studycards.data.model.vocabulary.Category
import abm.co.studycards.databinding.ItemSelectExploreCategoryBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class SelectExploreCategoryAdapter(
    var onCategoryClicked: (category: Category) -> Unit
) :
    ListAdapter<Category, SelectExploreCategoryAdapter.ViewHolder>(DIFF_UTIL) {

    public override fun getItem(position: Int): Category {
        return super.getItem(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSelectExploreCategoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    inner class ViewHolder(private val binding: ItemSelectExploreCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                onCategoryClicked.invoke(getItem(absoluteAdapterPosition))
            }
        }

        fun bind(currentItem: Category) {
            binding.run {
                text.text = currentItem.mainName
                wordsCount.text = binding.root.context.resources.getQuantityString(
                    R.plurals.words,
                    currentItem.words.size,
                    currentItem.words.size
                )
            }
        }
    }

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<Category>() {
            override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean =
                oldItem.id == newItem.id && oldItem.mainName == newItem.mainName

            override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean =
                oldItem == newItem
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}