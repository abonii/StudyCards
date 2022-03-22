package abm.co.studycards.ui.games.matching

import abm.co.studycards.data.model.vocabulary.Word
import abm.co.studycards.databinding.ItemMatchingCardBinding
import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

class MatchingAdapter(
    private val listener: OnClickCard,
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
        return ViewHolder(ItemMatchingCardBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = words[position]

        if (position == selectedItemPos)
            holder.selectedCardStroke()
        else
            holder.defaultCardStroke()

        if (isTranslatedWords) {
            holder.bindTranslated(currentItem, listener)
        } else {
            holder.bind(currentItem, listener)
        }
    }

    override fun getItemCount(): Int {
        return words.size
    }

    inner class ViewHolder(val binding: ItemMatchingCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(currentItem: Word, listener: OnClickCard) {
            binding.word.text = currentItem.name
            binding.root.setOnClickListener {
                currentItemCard = it as MaterialCardView
                if (listener.onClick(currentItem.wordId, false))
                    changeSelectedPosition()
                lastItemCard = currentItemCard
            }

        }

        fun bindTranslated(currentItem: Word, listener: OnClickCard) {
            binding.word.text = currentItem.translations.joinToString(", ")
            binding.root.setOnClickListener {
                currentItemCard = it as MaterialCardView
                if (listener.onClick(currentItem.wordId, true))
                    changeSelectedPosition()
                lastItemCard = currentItemCard
            }
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

    interface OnClickCard {
        fun onClick(
            currentItemId: String,
            isTranslatedWord: Boolean,
        ): Boolean
    }

}