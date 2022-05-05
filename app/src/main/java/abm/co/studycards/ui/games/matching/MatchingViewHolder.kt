package abm.co.studycards.ui.games.matching

import abm.co.studycards.R
import abm.co.studycards.data.model.vocabulary.Word
import abm.co.studycards.data.model.vocabulary.translationsToString
import abm.co.studycards.databinding.ItemMatchingCardBinding
import abm.co.studycards.util.changeBackgroundChangesAndFlip
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class MatchingViewHolder(
    val binding: ItemMatchingCardBinding,
    private val isTranslatedWords: Boolean,
    clickPosition: (Int) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    private val defaultTextColor =
        ContextCompat.getColor(binding.root.context, R.color.colorPrimary)
    private val correctTextColor =
        ContextCompat.getColor(binding.root.context, R.color.white)
    private val correctBackgroundColor =
        ContextCompat.getColor(binding.root.context, R.color.green)
    private val defaultBackgroundColor =
        ContextCompat.getColor(binding.root.context, R.color.color_itself)
    private val selectedStrokeColor =
        ContextCompat.getColor(binding.root.context, R.color.red)

    init {
        itemView.setOnClickListener {
            clickPosition(absoluteAdapterPosition)
        }
    }

    fun bind(currentItem: Word) = binding.run {
        word.text = if (!isTranslatedWords) {
            currentItem.translationsToString()
        } else currentItem.name
    }

    fun bindCorrectWord() = binding.run {
        word.setTextColor(correctTextColor)
        root.strokeColor = correctBackgroundColor
        root.changeBackgroundChangesAndFlip(currentColor = defaultBackgroundColor,
            color = correctBackgroundColor).run {
            doOnEnd {
                disappearCards(root)
            }
        }
    }

    fun bindDefaultWord() = binding.run {
        root.setCardBackgroundColor(defaultBackgroundColor)
        word.setTextColor(defaultTextColor)
        root.strokeColor = defaultBackgroundColor
    }

    fun bindSelectedWord() = binding.run {
        root.setCardBackgroundColor(defaultBackgroundColor)
        word.setTextColor(defaultTextColor)
        root.strokeColor = selectedStrokeColor
    }

    fun shakeUnCorrectWord() = binding.run {
        bindDefaultWord()
        val shake: Animation =
            AnimationUtils.loadAnimation(root.context, R.anim.shake)
        root.startAnimation(shake)
    }

    private fun disappearCards(card: View) {
        val scaleDown = ObjectAnimator.ofPropertyValuesHolder(
            card,
            PropertyValuesHolder.ofFloat("scaleX", 0f),
            PropertyValuesHolder.ofFloat("scaleY", 0f)
        )
        card.animate()
        scaleDown.duration = 600
        scaleDown.start()
        scaleDown.doOnEnd {
            card.visibility = View.GONE
        }
    }
}