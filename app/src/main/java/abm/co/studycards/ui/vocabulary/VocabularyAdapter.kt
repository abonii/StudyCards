package abm.co.studycards.ui.vocabulary

import abm.co.studycards.data.model.vocabulary.Word
import abm.co.studycards.databinding.ItemVocabularyTabBinding
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.github.florent37.expansionpanel.viewgroup.ExpansionLayoutCollection

class VocabularyAdapter : ListAdapter<Word, VocabularyAdapter.ViewHolder>(DIFF_UTIL) {

    private val expansionsCollection = ExpansionLayoutCollection()

    inner class ViewHolder(val binding: ItemVocabularyTabBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(currentItem: Word) {
            expansionsCollection.add(binding.expansionLayout)
            binding.word.text = currentItem.name
            binding.translation.text = currentItem.translations.joinToString(", ")
            if (currentItem.imageUrl.isBlank()) {
                Glide
                    .with(binding.image)
                    .load(currentItem.imageUrl)
                    .addListener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean,
                        ): Boolean {
                            binding.image.isVisible = false
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean,
                        ): Boolean {
                            binding.image.isVisible = true
                            return false
                        }

                    })
                    .into(binding.image)
            }
        }
    }

    public override fun getItem(position: Int): Word {
        return super.getItem(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemVocabularyTabBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
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