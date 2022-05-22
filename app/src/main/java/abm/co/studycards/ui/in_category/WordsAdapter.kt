package abm.co.studycards.ui.in_category

import abm.co.studycards.data.model.vocabulary.Word
import abm.co.studycards.databinding.ItemWordBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

class WordsAdapter(
    private val onItemClick: (Word) -> Unit,
    private val onAudioClicked: (Word) -> Unit
) : ListAdapter<Word, WordViewHolder>(DIFF_UTIL) {

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    public override fun getItem(position: Int): Word {
        return super.getItem(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        return WordViewHolder(
            ItemWordBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            {
                onItemClick(getItem(it))
            }, {
                onAudioClicked(getItem(it))
            }
        )
    }

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<Word>() {
            override fun areItemsTheSame(oldItem: Word, newItem: Word): Boolean =
                oldItem.wordId == newItem.wordId

            override fun areContentsTheSame(oldItem: Word, newItem: Word): Boolean =
                oldItem == newItem
        }
    }
}