package abm.co.studycards.ui.settings

import abm.co.studycards.data.model.Language
import abm.co.studycards.databinding.ItemLanguageOfTheAppBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class LanguageOfTheAppAdapter(
    private val items: List<Language>,
    private val onSelectSystemLanguage: (Language) -> Unit
) : RecyclerView.Adapter<LanguageOfTheAppAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemLanguageOfTheAppBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                onSelectSystemLanguage(items[absoluteAdapterPosition])
            }
        }

        fun bind(currentItem: Language) {
            binding.run {
                image.setImageDrawable(
                    currentItem.getDrawable(root.context)
                )
                name.text = currentItem.getLanguageName(root.context)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLanguageOfTheAppBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

}