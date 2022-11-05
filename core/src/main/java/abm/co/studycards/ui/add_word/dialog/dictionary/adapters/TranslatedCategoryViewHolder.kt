package abm.co.studycards.ui.add_word.dialog.dictionary.adapters

import abm.co.studycards.databinding.ItemTranslatedCategoryBinding
import abm.co.studycards.domain.model.LexicalCategory
import androidx.recyclerview.widget.RecyclerView

class TranslatedCategoryViewHolder(
    private val binding: ItemTranslatedCategoryBinding,
    onExampleSelected: (example: String, isPressed: Boolean) -> Unit,
    onTranslationSelected: (example: String, isPressed: Boolean) -> Unit
) :
    RecyclerView.ViewHolder(binding.root) {

    private var tAdapter: TranslatedWordAdapter? = null

    init {
        tAdapter = TranslatedWordAdapter(onExampleSelected, onTranslationSelected)
    }

    fun bind(currentItem: LexicalCategory) {
        tAdapter?.submitList(currentItem.details)
        binding.run {
            category.text = currentItem.lexicalName
            listView.adapter = tAdapter
        }
    }
}