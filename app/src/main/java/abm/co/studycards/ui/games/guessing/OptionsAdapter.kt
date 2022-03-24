package abm.co.studycards.ui.games.guessing

import abm.co.studycards.data.model.vocabulary.Word
import abm.co.studycards.databinding.ItemOptionBinding
import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class OptionsAdapter(private val onClickCard: (View, String) -> Unit) :
    RecyclerView.Adapter<OptionsAdapter.ViewHolder>() {

    var words: List<Word> = ArrayList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value.shuffled()
            notifyDataSetChanged()
        }
    val views = ArrayList<View>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(ItemOptionBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = words[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int {
        return words.size
    }

    inner class ViewHolder(val binding: ItemOptionBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                onClickCard(binding.option, words[absoluteAdapterPosition].wordId)
            }
            views.add(binding.option)
        }

        fun bind(currentItem: Word) {
            binding.option.text = currentItem.translations.joinToString(", ")
            binding.option.setBackgroundColor(Color.TRANSPARENT)
        }
    }

}