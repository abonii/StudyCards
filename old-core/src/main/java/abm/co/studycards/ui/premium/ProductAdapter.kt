package abm.co.studycards.ui.premium

import abm.co.studycards.R
import abm.co.studycards.databinding.ItemProductsListBinding
import abm.co.studycards.util.Constants.APP_NAME
import abm.co.studycards.util.dp
import abm.co.studycards.util.getMyColor
import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.billingclient.api.SkuDetails

class ProductAdapter(
    private val onProductClick: (SkuDetails) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    var products: List<SkuDetails> = ArrayList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var newWidth = 120.dp()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductsListBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ProductViewHolder(binding) {
            onProductClick(products[it])
        }
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val item = products[position]
        holder.bind(item)
    }

    override fun getItemCount() = products.size

    inner class ProductViewHolder(
        private val binding: ItemProductsListBinding,
        private val onProductClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.card.setOnClickListener {
                onProductClick(absoluteAdapterPosition)
            }
            binding.card.setOnLongClickListener {
                onProductClick(absoluteAdapterPosition)
                true
            }
            itemView.layoutParams = itemView.layoutParams.apply {
                width = newWidth
                height = (newWidth * 1.1).toInt()
            }
        }

        fun bind(product: SkuDetails) = binding.run {
            productName.text = product.title
                .replace("($APP_NAME)", "").trim()
            price.text = product.price
            if (absoluteAdapterPosition % 2 == 0) {
                setBackgroundAndTextColor(
                    root.getMyColor(R.color.textColor),
                    root.getMyColor(R.color.background),
                    root.getMyColor(R.color.grey)
                )
            } else {
                setBackgroundAndTextColor(
                    root.getMyColor(R.color.white),
                    root.getMyColor(R.color.colorPrimary),
                    root.getMyColor(R.color.colorPrimary)
                )
            }

        }

        private fun setBackgroundAndTextColor(
            textColor: Int,
            backgroundColor: Int,
            strokeColor: Int
        ) =
            binding.run {
                productName.setTextColor(textColor)
                price.setTextColor(textColor)
                card.setCardBackgroundColor(ColorStateList.valueOf(backgroundColor))
                card.strokeColor = strokeColor
            }

    }
}