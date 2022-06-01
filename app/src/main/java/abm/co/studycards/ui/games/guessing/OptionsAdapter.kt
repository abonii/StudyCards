package abm.co.studycards.ui.games.guessing

import abm.co.studycards.R
import abm.co.studycards.databinding.ItemOptionBinding
import abm.co.studycards.domain.model.Word
import abm.co.studycards.util.getMyColor
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class OptionsAdapter(private val onClickCard: (CardView, String) -> Unit) :
    RecyclerView.Adapter<OptionsAdapter.ViewHolder>() {

    var words: List<Word> = ArrayList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value.shuffled()
            notifyDataSetChanged()
        }
    val views = ArrayList<CardView>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(ItemOptionBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = words[position]
        holder.bind(currentItem)
    }

    override fun getItemCount() = words.size

    inner class ViewHolder(val binding: ItemOptionBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                onClickCard(binding.card, words[absoluteAdapterPosition].wordId)
            }
            views.add(binding.card)
        }

        fun bind(currentItem: Word) {
            binding.option.text = currentItem.translations
            binding.card.setCardBackgroundColor(binding.card.getMyColor(R.color.background))
        }
    }

}