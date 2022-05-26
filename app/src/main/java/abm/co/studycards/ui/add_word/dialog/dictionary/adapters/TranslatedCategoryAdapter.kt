package abm.co.studycards.ui.add_word.dialog.dictionary.adapters

import abm.co.studycards.databinding.ItemTranslatedCategoryBinding
import abm.co.studycards.domain.model.LexicalCategory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

class TranslatedCategoryAdapter(
    private val onExampleSelected: (example: String, isPressed: Boolean) -> Unit,
    private val onTranslationSelected: (example: String, isPressed: Boolean) -> Unit
) : ListAdapter<LexicalCategory, TranslatedCategoryViewHolder>(DIFF_UTIL) {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TranslatedCategoryViewHolder {
        val binding = ItemTranslatedCategoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TranslatedCategoryViewHolder(binding, onExampleSelected, onTranslationSelected)
    }

    override fun onBindViewHolder(holder: TranslatedCategoryViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<LexicalCategory>() {
            override fun areItemsTheSame(
                oldItem: LexicalCategory,
                newItem: LexicalCategory
            ): Boolean =
                oldItem.lexicalName == newItem.lexicalName

            override fun areContentsTheSame(
                oldItem: LexicalCategory,
                newItem: LexicalCategory
            ): Boolean =
                oldItem == newItem
        }
    }
}