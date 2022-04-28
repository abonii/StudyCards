//package abm.co.studycards.ui.select_language_anywhere
//
//import abm.co.studycards.R
//import abm.co.studycards.data.model.Language
//import abm.co.studycards.databinding.ItemLanguageAnyWhereBinding
//import android.annotation.SuppressLint
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.core.content.ContextCompat
//import androidx.recyclerview.widget.RecyclerView
//
//
//class LanguageAnyWhereAdapter(
//    private val onItemClicked: (LanguageSelectable) -> Unit
//) : RecyclerView.Adapter<LanguageAnyWhereAdapter.ViewHolder>() {
//
//    var items: List<LanguageSelectable> = ArrayList()
//        @SuppressLint("NotifyDataSetChanged")
//        set(value) {
//            field = value
//            notifyDataSetChanged()
//        }
//    var selectedItemPos = -1
//    var lastItemSelectedPos = -1
//
//    inner class ViewHolder(
//        private val binding: ItemLanguageAnyWhereBinding,
//        onItemClicked: (Int) -> Unit
//    ) : RecyclerView.ViewHolder(binding.root) {
//        private val selectedColor =
//            ContextCompat.getColor(binding.root.context, R.color.colorPrimary)
//        private val defaultTextColor =
//            ContextCompat.getColor(binding.root.context, R.color.secondTextColor)
//        private val optionDefaultColor =
//            ContextCompat.getColor(binding.root.context, R.color.optionDefaultColor)
//
//        init {
//            itemView.setOnClickListener {
//                onItemClicked(absoluteAdapterPosition)
//                changeSelectedPosition()
//
//            }
//        }
//
//        fun bind(item: Language) {
//            binding.apply {
//                name.text = item.getLanguageName(root.context)
//                image.setImageDrawable(
//                    item.getDrawable(root.context)
//                )
//            }
//        }
//
//        private fun changeSelectedPosition() {
//            selectedItemPos = absoluteAdapterPosition
//            when {
//                lastItemSelectedPos != -1 && lastItemSelectedPos != selectedItemPos -> {
//                    notifyItemChanged(lastItemSelectedPos, 2)
//                }
//            }
//            notifyItemChanged(selectedItemPos, 0)
//        }
//
//        fun defaultCardStroke(ownBinding: ItemLanguageAnyWhereBinding = binding) {
//            ownBinding.name.setTextColor(defaultTextColor)
//            ownBinding.card.strokeColor = optionDefaultColor
//        }
//
//        fun selectedCardStroke() {
//            binding.name.setTextColor(selectedColor)
//            binding.card.strokeColor = selectedColor
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val binding = ItemLanguageAnyWhereBinding.inflate(
//            LayoutInflater.from(parent.context), parent, false
//        )
//        return ViewHolder(binding) {
//            onItemClicked(items[it])
//        }
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val item = items[position]
//        if (item.isSelected) {
//            holder.selectedCardStroke()
//            lastItemSelectedPos = position
//        } else
//            holder.defaultCardStroke()
//        holder.bind(item.language)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
//        if (payloads.isNotEmpty()) {
//            when (payloads[0]) {
//                2 -> {
//                    items[position].isSelected = false
//                }
//            }
//        }
//        super.onBindViewHolder(holder, position, payloads)
//    }
//
//    override fun getItemCount(): Int = items.size
//
//}