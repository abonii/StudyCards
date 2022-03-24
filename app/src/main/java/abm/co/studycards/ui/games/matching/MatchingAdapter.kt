package abm.co.studycards.ui.games.matching

import abm.co.studycards.data.model.vocabulary.Word
import abm.co.studycards.data.model.vocabulary.translationsToString
import abm.co.studycards.databinding.ItemMatchingCardBinding
import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

class MatchingAdapter(
    private val onClickCard: (String, Boolean) -> Boolean,
    private val isTranslatedWords: Boolean,
) : RecyclerView.Adapter<MatchingAdapter.ViewHolder>() {
    var words: List<Word> = ArrayList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var selectedItemPos = -1
    var lastItemSelectedPos = -1
    lateinit var lastItemCard: MaterialCardView
    lateinit var currentItemCard: MaterialCardView


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            ItemMatchingCardBinding.inflate(inflater, parent, false),
            isTranslatedWords
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = words[position]

        if (position == selectedItemPos)
            holder.selectedCardStroke()
        else holder.defaultCardStroke()
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int {
        return words.size
    }

    inner class ViewHolder(
        val binding: ItemMatchingCardBinding,
        private val isTranslatedWords: Boolean
    ) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                currentItemCard = it as MaterialCardView
                if (onClickCard(words[absoluteAdapterPosition].wordId, isTranslatedWords))
                    changeSelectedPosition()
                lastItemCard = currentItemCard
            }
        }

        fun bind(currentItem: Word) {
            binding.word.text =
                if (!isTranslatedWords) currentItem.name else currentItem.translationsToString()
        }

        private fun changeSelectedPosition() {
            selectedItemPos = absoluteAdapterPosition
            if (lastItemSelectedPos == selectedItemPos) {
                if (lastItemCard.strokeColorStateList == ColorStateList.valueOf(Color.RED)) {
                    defaultCardStroke()
                } else {
                    selectedCardStroke()
                }
                return
            }
            lastItemSelectedPos = if (lastItemSelectedPos == -1)
                selectedItemPos
            else {
                defaultCardStroke(lastItemCard)
                selectedItemPos
            }
            notifyItemChanged(selectedItemPos, 0)
        }

        fun defaultCardStroke(view: MaterialCardView = binding.root) {
            view.strokeColor = Color.TRANSPARENT
        }

        fun selectedCardStroke() {
            binding.root.strokeColor = Color.RED
        }
    }


}