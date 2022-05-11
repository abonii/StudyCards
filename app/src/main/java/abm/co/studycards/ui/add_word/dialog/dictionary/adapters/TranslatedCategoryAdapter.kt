package abm.co.studycards.ui.add_word.dialog.dictionary.adapters

import abm.co.studycards.data.model.oxford.LexicalEntry
import abm.co.studycards.databinding.ItemTranslatedCategoryBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

class TranslatedCategoryAdapter(
    private val onExampleSelected: (example: String, isPressed: Boolean) -> Unit,
    private val onTranslationSelected: (example: String, isPressed: Boolean) -> Unit
) : ListAdapter<LexicalEntry, TranslatedCategoryViewHolder>(DIFF_UTIL) {


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
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<LexicalEntry>() {
            override fun areItemsTheSame(oldItem: LexicalEntry, newItem: LexicalEntry): Boolean =
                oldItem.text == newItem.text

            override fun areContentsTheSame(oldItem: LexicalEntry, newItem: LexicalEntry): Boolean =
                oldItem == newItem
        }
    }
}