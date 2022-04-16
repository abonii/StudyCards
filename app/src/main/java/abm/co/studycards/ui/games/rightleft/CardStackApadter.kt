package abm.co.studycards.ui.games.rightleft

import abm.co.studycards.R
import abm.co.studycards.data.model.vocabulary.Word
import abm.co.studycards.data.model.vocabulary.translationsToString
import abm.co.studycards.databinding.ItemCardBinding
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
import com.bumptech.glide.Glide

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

        fun bind(currentItem: Word) {
            Glide.with(binding.image)
                .load(currentItem.imageUrl)
                .into(binding.image)
            binding.word.text = currentItem.name
            binding.translated.text = currentItem.translationsToString()
            bindFrontLayout()
        }

        private fun onClickToShowBackSide(view: View = binding.innerCard) {
            val oa1 = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0f)
            val oa2 = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f)
            oa1.interpolator = DecelerateInterpolator()
            oa2.interpolator = AccelerateDecelerateInterpolator()
            oa1.doOnEnd {
                bindBackLayout()
                oa2.start()
            }
            oa1.start()
        }

        private fun bindBackLayout() {
            binding.image.isVisible = true
            binding.translated.isVisible = true
        }

        private fun bindFrontLayout() {
            binding.image.isVisible = false
            binding.translated.isVisible = false
        }
    }

}