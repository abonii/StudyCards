package abm.co.studycards.ui.games.review

import abm.co.studycards.data.model.vocabulary.Word
import abm.co.studycards.data.model.vocabulary.translationsToString
import abm.co.studycards.databinding.ItemReviewBinding
import android.annotation.SuppressLint
import android.graphics.BlurMaskFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ReviewAdapter : RecyclerView.Adapter<ReviewAdapter.ViewHolder>() {
    var words: MutableList<Word?> = ArrayList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value.shuffled() as MutableList<Word?>
            field.add(null)
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(ItemReviewBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = words[position]
        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    override fun getItemCount(): Int {
        return words.size
    }

    inner class ViewHolder(val binding: ItemReviewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(currentItem: Word) {
            binding.word.text = currentItem.name
            Glide.with(binding.image)
                .load(currentItem.imageUrl)
                .into(binding.image)
            binding.translated.text = currentItem.translationsToString()
            val radius: Float = binding.translated.textSize / 3
            val filter = BlurMaskFilter(radius, BlurMaskFilter.Blur.NORMAL)
            binding.translated.paint.maskFilter = filter
            binding.translated.animate().setDuration(1200).alpha(1f).withEndAction {
                binding.translated.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                binding.translated.paint.maskFilter = null
            }
        }
    }
}