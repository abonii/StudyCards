package abm.co.studycards.ui.add_word.dialog.dictionary.adapters

import abm.co.studycards.databinding.ItemTranslatedWordBinding
import abm.co.studycards.domain.model.CategoryDetails
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

class TranslatedWordAdapter(
    private var onExampleSelected: (example: String, isPressed: Boolean) -> Unit,
    private var onTranslationSelected: (example: String, isPressed: Boolean) -> Unit
) :
    ListAdapter<CategoryDetails, TranslatedWordViewHolder>(DIFF_UTIL) {
    private var countNumber = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TranslatedWordViewHolder {
        val binding = ItemTranslatedWordBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TranslatedWordViewHolder(binding, onExampleSelected, onTranslationSelected) {
            return@TranslatedWordViewHolder "${countNumber++}."
        }
    }

    override fun onBindViewHolder(holder: TranslatedWordViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<CategoryDetails>() {
            override fun areItemsTheSame(
                oldItem: CategoryDetails,
                newItem: CategoryDetails
            ): Boolean =
                oldItem.translations == newItem.translations

            override fun areContentsTheSame(
                oldItem: CategoryDetails,
                newItem: CategoryDetails
            ): Boolean =
                oldItem == newItem
        }
    }
}

