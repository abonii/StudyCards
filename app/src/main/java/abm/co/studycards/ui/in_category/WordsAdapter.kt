package abm.co.studycards.ui.in_category

import abm.co.studycards.R
import abm.co.studycards.data.model.LearnOrKnown
import abm.co.studycards.data.model.vocabulary.Word
import abm.co.studycards.databinding.ItemWordBinding
import abm.co.studycards.util.getMyColor
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class WordsAdapter(
    private val listener: OnItemClick,
    private val isTranscriptionSupported: Boolean
) : ListAdapter<Word, WordsAdapter.ViewHolder>(DIFF_UTIL) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    public override fun getItem(position: Int): Word {
        return super.getItem(position)
    }

    inner class ViewHolder(private val binding: ItemWordBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                listener.onItemClick(getItem(absoluteAdapterPosition))
            }
        }

        fun bind(currentItem: Word) {
            binding.apply {
                stick.backgroundTintList = getStickBackgroundTint(currentItem)
                audio.setOnClickListener {
                    listener.onAudioClicked(currentItem)
                }
                text.text = currentItem.name
                translation.text = currentItem.translations.joinToString(", ")
                audio.isVisible = isTranscriptionSupported
            }
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemWordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    interface OnItemClick {
        fun onItemClick(vocabulary: Word)
        fun onSelectItems(isShortClickActivated: Boolean, selectedItemsCount: Int = 0)
        fun onAudioClicked(word: Word)
    }

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<Word>() {
            override fun areItemsTheSame(oldItem: Word, newItem: Word): Boolean =
                oldItem.wordId == newItem.wordId && oldItem.learnOrKnown == newItem.learnOrKnown
                        && oldItem.name == newItem.name && oldItem.translations == newItem.translations

            override fun areContentsTheSame(oldItem: Word, newItem: Word): Boolean =
                oldItem == newItem
        }
    }
}