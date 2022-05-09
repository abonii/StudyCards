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
    private val primaryColor =
        ContextCompat.getColor(binding.root.context, R.color.colorPrimary)
    private val whiteColor =
        ContextCompat.getColor(binding.root.context, R.color.white)
    private val itselfColor =
        ContextCompat.getColor(binding.root.context, R.color.color_itself)
    private val greenColor =
        ContextCompat.getColor(binding.root.context, R.color.green)
    private val redColor =
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
        word.setTextColor(whiteColor)
        card.strokeColor = greenColor
        card.changeBackgroundChangesAndFlip(currentColor = itselfColor,
            color = greenColor).run {
            doOnEnd {
                disappearCards(card)
            }
        }
    }

    fun bindDefaultWord() = binding.run {
        card.setCardBackgroundColor(itselfColor)
        word.setTextColor(primaryColor)
        card.strokeColor = itselfColor
    }

    private fun bindUnCorrectWord() = binding.run {
        card.setCardBackgroundColor(itselfColor)
        word.setTextColor(primaryColor)
        card.strokeColor = redColor
    }

    fun bindSelectedWord() = binding.run {
        card.setCardBackgroundColor(itselfColor)
        word.setTextColor(primaryColor)
        card.strokeColor = primaryColor
    }

    fun shakeUnCorrectWord() = binding.run {
        val shake: Animation =
            AnimationUtils.loadAnimation(card.context, R.anim.shake)
        card.startAnimation(shake)
        shake.setAnimationListener(object:Animation.AnimationListener{
            override fun onAnimationStart(p0: Animation?) {
                bindUnCorrectWord()
            }

            override fun onAnimationEnd(p0: Animation?) {
                bindDefaultWord()
            }

            override fun onAnimationRepeat(p0: Animation?) {
            }
        })
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