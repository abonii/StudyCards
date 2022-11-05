package abm.co.studycards.ui.games.matching

import abm.co.studycards.databinding.ItemMatchingCardBinding
import abm.co.studycards.domain.model.Word
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class MatchingAdapter(
    private val onClickCard: (String, Boolean) -> Boolean?,
    private val isTranslatedWords: Boolean,
) : RecyclerView.Adapter<MatchingViewHolder>() {

    var items: List<WordMatching> = ArrayList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var selectedItemPos = -1
    var lastItemSelectedPos = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchingViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MatchingViewHolder(
            ItemMatchingCardBinding.inflate(inflater, parent, false),
            isTranslatedWords
        ) { pos ->
            changeSelectedPosition(pos)
            val wordId = if (!items[pos].isSelected) {
                items[pos].word.wordId
            } else ""
            val isSameTwoWords = onClickCard(wordId, isTranslatedWords)
            sameTwoWords(isSameTwoWords, pos)
            lastItemSelectedPos = pos
        }
    }

    private fun sameTwoWords(isIt: Boolean?, pos: Int) {
        if (isIt != null) {
            if (isIt) notifyItemChanged(pos, CORRECT)
            else notifyItemChanged(pos, NOT_CORRECT)
        } else {
            notifyItemChanged(selectedItemPos, CLICKED)
        }
    }

    override fun onBindViewHolder(holder: MatchingViewHolder, position: Int) {
        val currentItem = items[position]
        val correct = currentItem.isSelectedCorrect
        val selected = currentItem.isSelected
        holder.bind(currentItem.word)
        if (correct != null) {
            if (correct)
                holder.bindCorrectWord()
            else {
                holder.shakeUnCorrectWord()
                items[position].isSelectedCorrect = null
            }
        } else {
            if (selected) {
                holder.bindSelectedWord()
            } else {
                holder.bindDefaultWord()
            }
        }
    }

    private fun changeSelectedPosition(position: Int) {
        selectedItemPos = position
        when {
            lastItemSelectedPos != -1 && lastItemSelectedPos != selectedItemPos -> {
                notifyItemChanged(lastItemSelectedPos, NOT_SELECTED)
            }
        }
    }

    override fun onBindViewHolder(
        holder: MatchingViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty()) {
            when (payloads.first()) {
                SELECTED -> {//clicked item
                    items[position].isSelected = true
                }
                NOT_SELECTED -> {//clicked item twice
                    items[position].isSelected = false
                }
                CORRECT -> {//clicked item correct
                    items[position].isSelectedCorrect = true
                }
                NOT_CORRECT -> {//shake unCorrect item
                    items[position].isSelectedCorrect = false
                    items[position].isSelected = false
                }
                CLICKED -> {//shake unCorrect item
                    items[position].isSelected = !items[position].isSelected
                }
            }
        }
        super.onBindViewHolder(holder, position, payloads)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    companion object {
        const val SELECTED = 0
        const val NOT_SELECTED = 1
        const val CORRECT = 2
        const val NOT_CORRECT = 3
        const val CLICKED = 4
    }
}

data class WordMatching(val word: Word, var isSelected: Boolean, var isSelectedCorrect: Boolean?)