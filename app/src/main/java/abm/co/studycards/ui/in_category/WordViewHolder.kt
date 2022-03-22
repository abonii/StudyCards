package abm.co.studycards.ui.in_category

import abm.co.studycards.R
import abm.co.studycards.data.model.LearnOrKnown
import abm.co.studycards.data.model.vocabulary.Word
import abm.co.studycards.databinding.ItemWordBinding
import abm.co.studycards.util.getMyColor
import android.content.res.ColorStateList
import androidx.recyclerview.widget.RecyclerView

class WordViewHolder(
    private val binding: ItemWordBinding,
    onItemClick: (Int) -> Unit,
    onAudioClicked: (Int) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init {
        itemView.setOnClickListener {
            onItemClick(absoluteAdapterPosition)
        }
        binding.audio.setOnClickListener {
            onAudioClicked(absoluteAdapterPosition)
        }
    }

    fun bind(currentItem: Word) = binding.run {
        stick.backgroundTintList = getStickBackgroundTint(currentItem)
        text.text = currentItem.name
        translation.text = currentItem.translations.joinToString(", ")
    }

    private fun getStickBackgroundTint(currentItem: Word): ColorStateList {
        val color = when (currentItem.learnOrKnown) {
            LearnOrKnown.UNDEFINED.getType() -> {
                binding.root.getMyColor(R.color.colorPrimary)
            }
            LearnOrKnown.UNKNOWN.getType() -> {
                binding.root.getMyColor(R.color.swiping_n_know)
            }
            LearnOrKnown.UNCERTAIN.getType() -> {
                binding.root.getMyColor(R.color.swiping_doubted)
            }
            else -> {
                binding.root.getMyColor(R.color.swiping_know)
            }
        }

        return ColorStateList.valueOf(color)
    }
}