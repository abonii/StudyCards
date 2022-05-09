package abm.co.studycards.ui.select_language_anywhere

import abm.co.studycards.databinding.ItemParentSelectLanguageAnywhereBinding
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView

class ParentLanguageAnywhereViewHolder(
    val binding: ItemParentSelectLanguageAnywhereBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(@StringRes title: Int) {
        binding.title.text = binding.root.context.getString(title)
    }
}