package abm.co.studycards.ui.learn.guessing

import abm.co.studycards.data.model.vocabulary.Word
import abm.co.studycards.databinding.ItemOptionBinding
import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class OptionsAdapter(
    private val listener: OnClickCard,
) : RecyclerView.Adapter<OptionsAdapter.ViewHolder>() {
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
        holder.bind(currentItem, listener)
    }

    override fun getItemCount(): Int {
        return words.size
    }

    inner class ViewHolder(val binding: ItemOptionBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            views.add(binding.option)
        }

        fun bind(currentItem: Word, listener: OnClickCard) {
            binding.option.text = currentItem.translations.joinToString(", ")
            binding.option.setBackgroundColor(Color.TRANSPARENT)
            binding.root.setOnClickListener {
                listener.onClick(binding.option, currentItem.wordId)
            }
        }
    }

    interface OnClickCard {
        fun onClick(
            view: View,
            currentItemId: String,
        )
    }

}