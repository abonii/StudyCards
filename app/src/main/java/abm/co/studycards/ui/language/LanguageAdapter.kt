package abm.co.studycards.ui.language

import abm.co.studycards.R
import abm.co.studycards.data.model.Language
import abm.co.studycards.databinding.ItemLanguageBinding
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class LanguageAdapter(
    val context: Context,
    val listener: OnClickWithPosition,
    val isTargetLanguage: Boolean
) : RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder>() {

    var selectedItemPos = -1
    var lastItemSelectedPos = -1
    lateinit var lastItemCard: TextView
    lateinit var currentItemCard: TextView
    var items: List<Language> = ArrayList()

    inner class LanguageViewHolder(private val binding: ItemLanguageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                listener.onClickWithPosition(items[absoluteAdapterPosition], isTargetLanguage)
                currentItemCard = binding.item
                changeSelectedPosition()
                lastItemCard = currentItemCard
            }
        }

        fun bind(currentItem: Language) {
            binding.apply {
                item.apply {
                    text = currentItem.getLanguageName(root.context)
                }
                image.setImageDrawable(
                    currentItem.getDrawable(context)
                )
            }
        }

        private fun changeSelectedPosition() {
            selectedItemPos = absoluteAdapterPosition
            if (lastItemSelectedPos == selectedItemPos) {
                if (lastItemCard.currentTextColor == Color.BLUE) {
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

        fun defaultCardStroke(view: TextView = binding.item) {
            view.setTextColor(ContextCompat.getColor(context, R.color.textColor))
        }

        fun selectedCardStroke() {
            binding.item.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
        val binding =
            ItemLanguageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LanguageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
        val currentItem = items[position]
        if (position == selectedItemPos)
            holder.selectedCardStroke()
        else
            holder.defaultCardStroke()
        holder.bind(currentItem)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addItems(it: List<Language>) {
        this.items = it
        notifyDataSetChanged()
    }

    override fun getItemCount() = items.size

    interface OnClickWithPosition {
        fun onClickWithPosition(lang: Language, isTargetLanguage: Boolean)
    }

}