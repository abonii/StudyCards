package abm.co.studycards.ui.add_word.dialog.category

import abm.co.studycards.data.model.vocabulary.Category
import abm.co.studycards.databinding.ItemSelectCategoryBinding
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class SelectCategoryAdapter(
    private val listener: SelectCategoryAdapterListener, options: FirebaseRecyclerOptions<Category>
) : FirebaseRecyclerAdapter<Category, SelectCategoryAdapter.ViewHolder>(options) {

    var checkedId: String? = null
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class ViewHolder(private val binding: ItemSelectCategoryBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(currentItem: Category) {
            binding.run {
                category.text = currentItem.mainName
                radio.setOnClickListener {
                    listener.onRadioClicked(currentItem)
                }
                category.setOnClickListener {
                    listener.onRadioClicked(currentItem)
                }
                if (currentItem.id == checkedId)
                    radio.isChecked = true
                else {
                    radioGroup.clearCheck()
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemSelectCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Category) {
        holder.bind(model)
    }

    interface SelectCategoryAdapterListener {
        fun onRadioClicked(currentItem: Category)
    }
}