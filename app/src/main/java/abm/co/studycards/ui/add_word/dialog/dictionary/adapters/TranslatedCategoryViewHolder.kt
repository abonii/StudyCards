package abm.co.studycards.ui.add_word.dialog.dictionary.adapters

import abm.co.studycards.data.model.oxford.LexicalEntry
import abm.co.studycards.databinding.ItemTranslatedCategoryBinding
import androidx.recyclerview.widget.RecyclerView

class TranslatedCategoryViewHolder(
    private val binding: ItemTranslatedCategoryBinding,
    onExampleSelected: (example: String, isPressed: Boolean) -> Unit,
    onTranslationSelected: (example: String, isPressed: Boolean) -> Unit
) :
    RecyclerView.ViewHolder(binding.root) {

    private var tAdapter: TranslatedWordAdapter? = null

    init {
        tAdapter = TranslatedWordAdapter(onExampleSelected ,onTranslationSelected)
    }

    fun bind(currentItem: LexicalEntry?) {
        tAdapter?.submitList(currentItem?.entries?.get(0)?.senses!!)
        binding.run {
            category.text = currentItem?.lexicalCategory?.text ?: ""
            listView.adapter = tAdapter
        }
    }
}