package abm.co.studycards.ui.home

import abm.co.studycards.R
import abm.co.studycards.data.model.vocabulary.Category
import abm.co.studycards.databinding.ItemCategoryBinding
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class CategoryAdapter(
    private val listener: CategoryAdapterListener
) : ListAdapter<Category, CategoryAdapter.ViewHolder>(DIFF_UTIL) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    inner class ViewHolder(private val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                listener.onCategoryClicked(getItem(absoluteAdapterPosition))
            }
            itemView.setOnLongClickListener {
                listener.onLongClickCategory(getItem(absoluteAdapterPosition))
                true
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(currentItem: Category) {
            binding.apply {
                text.text = currentItem.mainName
                play.setOnClickListener {
                    listener.onPlay(currentItem)
                }
                wordsCount.text = binding.root.context.resources.getQuantityString(R.plurals.words, currentItem.words.size, currentItem.words.size)
                    (currentItem.words.size).toString() + " word"
            }
        }
    }

    interface CategoryAdapterListener {
        fun onCategoryClicked(category: Category)
        fun onPlay(category: Category)
        fun onLongClickCategory(category: Category)
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