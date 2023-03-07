package abm.co.studycards.ui.add_to_yourself

import abm.co.studycards.databinding.ItemExploreWordSelectionBinding
import abm.co.studycards.domain.model.WordX
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ExploreWordForSelectingViewHolder(
    private val binding: ItemExploreWordSelectionBinding,
    onWordChecked: (Int, Boolean) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.checkbox.setOnCheckedChangeListener { _, b ->
            onWordChecked(absoluteAdapterPosition, b)
        }
    }

    fun bind(currentItem: WordX) = binding.run {
        checkbox.isChecked = currentItem.isChecked
        name.text = currentItem.word.name
        translation.text = currentItem.word.translations
        Glide.with(image)
            .load(currentItem.word.imageUrl)
            .into(image)

    }

    companion object {
        fun create(parent: ViewGroup, onWordChecked: (Int, Boolean) -> Unit) =
            ExploreWordForSelectingViewHolder(
                ItemExploreWordSelectionBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ), onWordChecked
            )
    }
}