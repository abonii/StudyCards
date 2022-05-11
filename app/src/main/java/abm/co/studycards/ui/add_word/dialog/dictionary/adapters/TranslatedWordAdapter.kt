package abm.co.studycards.ui.add_word.dialog.dictionary.adapters

import abm.co.studycards.data.model.oxford.Sense
import abm.co.studycards.databinding.ItemTranslatedWordBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

class TranslatedWordAdapter(
    private var onExampleSelected: (example: String, isPressed: Boolean) -> Unit,
    private var onTranslationSelected: (example: String, isPressed: Boolean) -> Unit
) :
    ListAdapter<Sense, TranslatedWordViewHolder>(DIFF_UTIL) {
    private var countNumber = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TranslatedWordViewHolder {
        val binding = ItemTranslatedWordBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TranslatedWordViewHolder(binding, onExampleSelected, onTranslationSelected) {
            return@TranslatedWordViewHolder if (it) {
                "${countNumber++}."
            } else ""
        }
    }

    override fun onBindViewHolder(holder: TranslatedWordViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<Sense>() {
            override fun areItemsTheSame(oldItem: Sense, newItem: Sense): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Sense, newItem: Sense): Boolean =
                oldItem == newItem
        }
    }
}

