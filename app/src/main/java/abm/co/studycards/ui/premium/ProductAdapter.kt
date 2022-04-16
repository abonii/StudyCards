package abm.co.studycards.ui.premium

import abm.co.studycards.databinding.ItemProductsListBinding
import abm.co.studycards.util.dp
import android.annotation.SuppressLint
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
//        val isSelected = products[position].second
        holder.bind(item)
    }

    override fun getItemCount() = products.size

    inner class ProductViewHolder(
        private val binding: ItemProductsListBinding,
        private val onProductClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                onProductClick.invoke(absoluteAdapterPosition)
            }
            itemView.layoutParams = itemView.layoutParams.apply {
                width = newWidth
            }
        }

        fun bind(product: SkuDetails) {
            with(binding) {
                productName.text = product.title
                price.text = product.price
            }
        }
    }
}