package abm.co.studycards.ui.vocabulary

import abm.co.studycards.domain.model.LearnOrKnown
import abm.co.studycards.databinding.ItemVocabularyTabBinding
import abm.co.studycards.domain.model.Word
import abm.co.studycards.domain.model.translationsToString
import abm.co.studycards.util.GeneralBindingAdapters.setImageWithGlide
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.chauthai.swipereveallayout.SwipeRevealLayout
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.github.florent37.expansionpanel.viewgroup.ExpansionLayoutCollection

/**
 * currentType: at position 0 is current fragment type f.e: UNKNOWN
 * at position 1 is first button's type f.e: UNCERTAIN
 * at position 2 is second button's type f.e: KNOWN.
 * */
class VocabularyAdapter(
    private val onChangeType: (word: Word, type: LearnOrKnown) -> Unit,
    currentType: LearnOrKnown
) : ListAdapter<Word, VocabularyAdapter.VocabularyViewHolder>(DIFF_UTIL) {

    val firstButton = currentType.getFirstBtn()
    val secondButton = currentType.getSecondBtn()

    var onLongClickWord: ((word: Word) -> Unit)? = null

    private val viewBinderHelper = ViewBinderHelper().apply {
        setOpenOnlyOne(true)
    }

    private val expansionsCollection = ExpansionLayoutCollection()

    inner class VocabularyViewHolder(val binding: ItemVocabularyTabBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val swipeRevealLayout = binding.swipeRevealLayout
        private val expansionLayout = binding.expansionLayout
        private val swipeListener = object : SwipeRevealLayout.SwipeListener {
            override fun onClosed(view: SwipeRevealLayout?) {}

            override fun onOpened(view: SwipeRevealLayout?) {
                if (expansionLayout.isExpanded) expansionLayout.collapse(true)
                swipeRevealLayout.open(true)
            }

            override fun onSlide(view: SwipeRevealLayout?, slideOffset: Float) {}

        }

        init {
            binding.firstBtn.setOnClickListener {
                onChangeType(getItem(absoluteAdapterPosition), firstButton)
            }
            binding.secondBtn.setOnClickListener {
                onChangeType(getItem(absoluteAdapterPosition), secondButton)
            }
            binding.expansionHeader.setOnLongClickListener {
                onLongClickWord?.invoke(getItem(absoluteAdapterPosition))
                true
            }
            expansionLayout.addListener { _, _ ->
                if (swipeRevealLayout.isOpened) expansionLayout.collapse(true)
            }
            swipeRevealLayout.setSwipeListener(swipeListener)
        }

        fun bind(currentItem: Word) = binding.run {
            changeButtons()
            expansionsCollection.add(expansionLayout)
            word.text = currentItem.name
            translation.text = currentItem.translationsToString()
            image.isVisible = currentItem.imageUrl.isNotEmpty()
            if (currentItem.imageUrl.isNotEmpty()) {
                image.setImageWithGlide(currentItem.imageUrl)
            }
        }

        private fun changeButtons() = binding.run {
            firstBtn.setBackgroundColor(firstButton.getColor(root.context))
            firstBtn.text = firstButton.getName(root.context)
            secondBtn.text = secondButton.getName(root.context)
            secondBtn.setBackgroundColor(secondButton.getColor(root.context))
        }
    }

    public override fun getItem(position: Int): Word {
        return super.getItem(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VocabularyViewHolder {
        val binding =
            ItemVocabularyTabBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VocabularyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VocabularyViewHolder, position: Int) {
        val item = getItem(position)
        viewBinderHelper.bind(holder.swipeRevealLayout, item.wordId)
        holder.bind(item)
    }

    fun saveStates(outState: Bundle?) {
        viewBinderHelper.saveStates(outState)
    }

    fun restoreStates(inState: Bundle?) {
        viewBinderHelper.restoreStates(inState)
    }

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<Word>() {
            override fun areItemsTheSame(oldItem: Word, newItem: Word): Boolean =
                oldItem.wordId == newItem.wordId && oldItem.learnOrKnown == newItem.learnOrKnown
                        && oldItem.name == newItem.name && oldItem.translations == newItem.translations

            override fun areContentsTheSame(oldItem: Word, newItem: Word): Boolean =
                oldItem == newItem
        }
    }

}