package abm.co.studycards.ui.select_language_anywhere

import abm.co.studycards.databinding.ItemParentSelectLanguageAnywhereBinding
import androidx.recyclerview.widget.RecyclerView

class ParentLanguageAnywhereViewHolder(
    val binding: ItemParentSelectLanguageAnywhereBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(title: String) {
        binding.title.text = title
    }
}