package abm.co.studycards.ui.select_language_anywhere

import abm.co.studycards.R
import abm.co.studycards.databinding.ItemLanguageAnyWhereBinding
import abm.co.studycards.databinding.ItemParentSelectLanguageAnywhereBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class LanguageAnyWhereParentAdapter :
    ListAdapter<LanguageVHUI, RecyclerView.ViewHolder>(DIFF_UTIL) {

    var selectedItemPos = -1
    var lastItemSelectedPos = -1
    private var currentTime: Long = 0


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_parent_select_language_anywhere -> {
                val binding =
                    ItemParentSelectLanguageAnywhereBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent, false
                    )
                ParentLanguageAnywhereViewHolder(binding)
            }
            R.layout.item_language_any_where -> {
                val binding = ItemLanguageAnyWhereBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                LanguageAnyWhereViewHolder(binding) {
                    if (currentTime + 100 < System.currentTimeMillis())
                        changeSelectedPosition(it)
                    currentTime = System.currentTimeMillis()
                }
            }
            else -> throw RuntimeException("Language ViewHolder are not created")
        }
    }

    private fun changeSelectedPosition(position: Int) {
        selectedItemPos = position
        when {
            lastItemSelectedPos != -1 && lastItemSelectedPos != selectedItemPos -> {
                notifyItemChanged(lastItemSelectedPos, 2)
            }
        }
        notifyItemChanged(selectedItemPos, 0)
        lastItemSelectedPos = position
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val currentItem = getItem(position)) {
            is LanguageVHUI.TitleLanguages -> {
                (holder as ParentLanguageAnywhereViewHolder).bind(currentItem.value)
            }
            is LanguageVHUI.Language -> {
                (holder as LanguageAnyWhereViewHolder).bind(currentItem.value)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is LanguageVHUI.TitleLanguages -> {
                R.layout.item_parent_select_language_anywhere
            }
            is LanguageVHUI.Language -> {
                R.layout.item_language_any_where
            }
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty()) {
            when (payloads[0]) {
                0 -> {
                    val lang = (getItem(position) as LanguageVHUI.Language).value
                    lang.isSelected = !lang.isSelected
                }
                2 -> {
                    val lang = (getItem(position) as LanguageVHUI.Language).value
                    lang.isSelected = false
                }
            }
        }
        super.onBindViewHolder(holder, position, payloads)
    }

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<LanguageVHUI>() {
            override fun areItemsTheSame(
                oldItem: LanguageVHUI,
                newItem: LanguageVHUI
            ): Boolean =
                (oldItem is LanguageVHUI.Language &&
                        newItem is LanguageVHUI.Language &&
                        oldItem.value.language.code == newItem.value.language.code)
                        || (oldItem is LanguageVHUI.TitleLanguages &&
                                newItem is LanguageVHUI.TitleLanguages &&
                                oldItem.value == newItem.value)


            override fun areContentsTheSame(
                oldItem: LanguageVHUI,
                newItem: LanguageVHUI
            ): Boolean =
                oldItem == newItem
        }
    }
}
