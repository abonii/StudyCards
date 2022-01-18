package abm.co.studycards.ui.home

import abm.co.studycards.data.model.vocabulary.Category
import abm.co.studycards.databinding.ItemCategoryBinding
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class CategoryAdapter(private val listener: CategoryAdapterListener,
                      options: FirebaseRecyclerOptions<Category>
) : FirebaseRecyclerAdapter<Category,CategoryAdapter.ViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Category) {
        holder.bind(model)
    }

    inner class ViewHolder(private val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(currentItem: Category) {
            binding.apply {
                root.setOnClickListener {
                    listener.onCategoryClicked(currentItem)
                }
                text.text = currentItem.mainName
                play.setOnClickListener {
                    listener.onPlay(currentItem)
                }
                wordsCount.text = (currentItem.words?.size ?: 0).toString() + " word"
            }
        }
    }
    interface CategoryAdapterListener {
        fun onCategoryClicked(category: Category)
        fun onPlay(category: Category)
        fun onSelectItem(isShortClickActivated: Boolean, selectedItemsCount: Int = 0)
    }
}