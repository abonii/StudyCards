package abm.co.studycards.ui.games.rightleft

import abm.co.studycards.R
import abm.co.studycards.data.model.vocabulary.Word
import abm.co.studycards.data.model.vocabulary.translationsToString
import abm.co.studycards.databinding.ItemCardBinding
import abm.co.studycards.util.GeneralBindingAdapters.setImageWithGlide
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import androidx.core.animation.doOnEnd
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView

class CardStackAdapter constructor(
    private val onShakeCard: () -> Unit,
    private val onAudioClicked: (Word) -> Unit
) : RecyclerView.Adapter<CardStackAdapter.ViewHolder>() {

    var words: List<Word> = ArrayList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(ItemCardBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = words[position]
        holder.bind(currentItem)
    }

    override fun getItemCount() = words.size

    inner class ViewHolder(val binding: ItemCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.audioPlay.setOnClickListener {
                onAudioClicked(words[absoluteAdapterPosition])
            }
            itemView.setOnClickListener {
                if (binding.translated.isVisible) {
                    onShakeCard()
                    val shake = AnimationUtils
                        .loadAnimation(binding.root.context, R.anim.shake)
                    binding.root.startAnimation(shake)
                } else {
                    onClickToShowBackSide()
                }
            }
        }

        fun bind(currentItem: Word) = binding.run {
            wordImage.setImageWithGlide(currentItem.imageUrl)
            wordImageContainer.isVisible = currentItem.imageUrl.isNotBlank()
            translated.text = currentItem.name
            word.text = currentItem.translationsToString()
            bindFrontLayout()
        }

        private fun onClickToShowBackSide(view: View = binding.innerCard) {
            val oa1 = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0f)
            val oa2 = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f)
            oa2.run {
                duration = 200
                interpolator = AccelerateDecelerateInterpolator()
            }
            oa1.run {
                interpolator = DecelerateInterpolator()
                duration = 200
                doOnEnd {
                    oa2.start()
                    bindBackLayout()
                }
                start()
            }
        }

        private fun bindBackLayout() {
            binding.translated.isVisible = true
        }

        private fun bindFrontLayout() {
            binding.translated.isVisible = false
        }
    }

}