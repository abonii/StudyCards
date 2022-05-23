package abm.co.studycards.ui.language

import abm.co.studycards.domain.model.Language
import abm.co.studycards.databinding.ItemLanguageBinding
import abm.co.studycards.ui.select_language_anywhere.LanguageSelectable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

class LanguageAdapter(
    private val onClickWithCode: (lang: Language, isTargetLanguage: Boolean) -> Unit,
    private val isTargetLanguage: Boolean
) : ListAdapter<LanguageSelectable, LanguageViewHolder>(DIFF_UTIL) {

    private var selectedItemPos = -1
    private var lastItemSelectedPos = -1
    private var currentTime: Long = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
        val binding =
            ItemLanguageBinding.inflate(
                LayoutInflater.from
                    (parent.context), parent, false
            )
        return LanguageViewHolder(binding) { position ->
            if (currentTime + 100 < System.currentTimeMillis()){
                changeSelectedPosition(position)
                onClickWithCode(getItem(position).language, isTargetLanguage)
            }
            currentTime = System.currentTimeMillis()

        }
    }

    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem.isSelected)
            holder.selectedCardStroke()
        else
            holder.defaultCardStroke()
        holder.bind(currentItem.language)
    }

    override fun onBindViewHolder(
        holder: LanguageViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty()) {
            when (payloads.first()) {
                CLICKED -> {
                    getItem(position).isSelected = true
                }
                NOT_SELECTED -> {
                    getItem(position).isSelected = false
                }
            }
        }
        super.onBindViewHolder(holder, position, payloads)
    }

    private fun changeSelectedPosition(position: Int) {
        selectedItemPos = position
        if (lastItemSelectedPos != -1 && lastItemSelectedPos != selectedItemPos) {
            notifyItemChanged(lastItemSelectedPos, NOT_SELECTED)
        }
        notifyItemChanged(selectedItemPos, CLICKED)
        lastItemSelectedPos = position
    }

    companion object {
        const val CLICKED = 0
        const val NOT_SELECTED = 1
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<LanguageSelectable>() {
            override fun areItemsTheSame(
                oldItem: LanguageSelectable,
                newItem: LanguageSelectable
            ): Boolean =
                oldItem.language.code == newItem.language.code

            override fun areContentsTheSame(
                oldItem: LanguageSelectable,
                newItem: LanguageSelectable
            ): Boolean =
                oldItem == newItem
        }
    }
}