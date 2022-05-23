package abm.co.studycards.ui.add_to_yourself

import abm.co.studycards.domain.model.WordX
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

class ExploreWordsForSelectingAdapter(private val onChecked: (WordX) -> Unit) :
    ListAdapter<WordX, ExploreWordForSelectingViewHolder>(DIFF_UTIL) {

    override fun onBindViewHolder(holder: ExploreWordForSelectingViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onBindViewHolder(
        holder: ExploreWordForSelectingViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty()) {
            when (payloads[0]) {
                false -> {
                    getItem(position).isChecked = false
                }
                true -> {
                    getItem(position).isChecked = true
                }
            }
        }
        super.onBindViewHolder(holder, position, payloads)
    }

    fun updateAllWords(isChecked: Boolean) {
        for (i in 0 until itemCount) {
            notifyItemChanged(i, isChecked)
        }
    }

    public override fun getItem(position: Int): WordX {
        return super.getItem(position)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ExploreWordForSelectingViewHolder {
        return ExploreWordForSelectingViewHolder.create(parent) { pos, checked ->
            val item = getItem(pos).apply {
                isChecked = checked
            }
            onChecked(item)
        }
    }

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<WordX>() {
            override fun areItemsTheSame(oldItem: WordX, newItem: WordX): Boolean =
                oldItem.word.wordId == newItem.word.wordId

            override fun areContentsTheSame(oldItem: WordX, newItem: WordX): Boolean =
                oldItem == newItem
        }
    }
}