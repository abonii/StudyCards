package abm.co.studycards.ui.learn.rightleft

import abm.co.studycards.R
import abm.co.studycards.data.model.vocabulary.Word
import abm.co.studycards.databinding.ItemCardFinallyBinding
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


class CardStackAdapter constructor(val listener: OnClick) :
    RecyclerView.Adapter<CardStackAdapter.ViewHolder>() {
    var words: List<Word> = ArrayList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(ItemCardFinallyBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = words[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int {
        return words.size
    }

    inner class ViewHolder(val binding: ItemCardFinallyBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(currentItem: Word) {
            Glide.with(binding.front.image)
                .load(currentItem.imageUrl)
                .into(binding.front.image)
            binding.front.word.text = currentItem.name
            binding.front.translated.text = currentItem.translations.joinToString(", ")
            binding.root.setOnClickListener {
                if (binding.front.translated.isVisible) {
                    listener.onClick()
                    val shake = AnimationUtils
                        .loadAnimation(binding.root.context, R.anim.shake)
                    binding.root.startAnimation(shake)
                } else {
                    onClickToShowBackSide()
                }
            }
            bindFrontLayout()
        }

        private fun onClickToShowBackSide(view: View = binding.front.root) {
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
            binding.front.image.isVisible = true
            binding.front.translated.isVisible = true
        }

        private fun bindFrontLayout() {
            binding.front.image.isVisible = false
            binding.front.translated.isVisible = false
        }

//        private fun onShakeVisibilityOn() {
//            binding.front.myBottomOverlay.apply {
//                isVisible = true
//                animate().alpha(1f).setDuration(1500).withEndAction {
//                    isVisible = false
//                }.start()
//            }
//            binding.front.myLeftOverlay.apply {
//                isVisible = true
//                animate().alpha(1f).setDuration(1500).withEndAction {
//                    isVisible = false
//                }.start()
//            }
//            binding.front.myRightOverlay.apply {
//                isVisible = true
//                animate().alpha(1f).setDuration(1500).withEndAction {
//                    isVisible = false
//                }.start()
//            }
//            val shake = AnimationUtils
//                .loadAnimation(binding.root.context, R.anim.shake)
//            binding.root.startAnimation(shake)
//        }
    }

    interface OnClick {
        fun onClick()
    }
}