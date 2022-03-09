package abm.co.studycards.ui.settings

import abm.co.studycards.R
import abm.co.studycards.data.model.Language
import abm.co.studycards.databinding.ItemLanguageOfTheAppBinding
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter

class LanguageOfTheAppAdapter(
    context: Context,
    private val items: List<Language>,
    private val listener: OnClick
) :
    ArrayAdapter<Language>(context, R.layout.item_language_of_the_app) {

    inner class ViewHolder(private val binding: ItemLanguageOfTheAppBinding) {
        fun bind(currentItem: Language) {

            binding.apply {
                root.setOnClickListener {
                    listener.onSelectSystemLanguage(currentItem)
                }
                image.setImageDrawable(
                    currentItem.getDrawable(root.context)
                )
                name.text = currentItem.getLanguageName(root.context)
            }
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var rowView = convertView
        val viewHolder: ViewHolder
        if (rowView == null) {
            val binding = ItemLanguageOfTheAppBinding
                .inflate(
                    LayoutInflater
                        .from(parent.context), parent, false
                )
            rowView = binding.root
            viewHolder = ViewHolder(binding)
            rowView.tag = viewHolder
        } else {
            viewHolder = rowView.tag as ViewHolder
        }
        val currentItem = items[position]
        viewHolder.bind(currentItem)
        return rowView
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(index: Int): Language {
        return this.items[index]
    }

    interface OnClick {
        fun onSelectSystemLanguage(language: Language)
    }
}