package abm.co.studycards.ui.add_word.dialog.translated.adapters

import abm.co.studycards.data.model.oxford.LexicalEntry
import abm.co.studycards.databinding.ItemTranslatedCategoryBinding
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TranslatedCategoryAdapter(
    private val listener: TranslatedWordAdapter.OnCheckBoxClicked
) :
    RecyclerView.Adapter<TranslatedCategoryAdapter.ViewHolder>() {

    var items: List<LexicalEntry>? = ArrayList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class ViewHolder(private val binding: ItemTranslatedCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            currentItem: LexicalEntry?
        ) {
            val tAdapter = TranslatedWordAdapter(listener)
            tAdapter.items = currentItem?.entries?.get(0)?.senses!!
            binding.apply {
                category.text = currentItem.lexicalCategory?.text
                listView.adapter = tAdapter
                listView.layoutManager = LinearLayoutManager(binding.root.context)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTranslatedCategoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = items?.get(position)
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int {
        return items?.size ?: 0
    }
}