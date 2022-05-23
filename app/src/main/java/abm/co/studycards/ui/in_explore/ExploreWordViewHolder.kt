package abm.co.studycards.ui.in_explore

import abm.co.studycards.databinding.ItemExploreWordBinding
import abm.co.studycards.domain.model.Word
import abm.co.studycards.domain.model.translationsToString
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ExploreWordViewHolder(
    private val binding: ItemExploreWordBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(currentItem: Word) = binding.run {
        name.text = currentItem.name
        translation.text = currentItem.translationsToString()
        Glide.with(image)
            .load(currentItem.imageUrl)
            .into(image)

    }

    companion object {
        fun create(parent: ViewGroup) = ExploreWordViewHolder(
            ItemExploreWordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }
}