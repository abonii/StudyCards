//package abm.co.studycards.ui.select_language_anywhere
//
//import abm.co.sdulife.data.model.Option
//import abm.co.sdulife.data.model.Question
//import abm.co.sdulife.databinding.ItemQuestionBinding
//import android.annotation.SuppressLint
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//
//
//class LanguageAnyWhereParentAdapter(private val onOptionClicked: (Option, Question) -> Unit) :
//    RecyclerView.Adapter<LanguageAnyWhereParentAdapter.ViewHolder>() {
//
//    var items: List<Question> = ArrayList()
//        @SuppressLint("NotifyDataSetChanged")
//        set(value) {
//            field = value
//            notifyDataSetChanged()
//        }
//
//    inner class ViewHolder(
//        val binding: ItemQuestionBinding,
//        onItemSelected: (Option, Int) -> Unit
//    ) : RecyclerView.ViewHolder(binding.root) {
//        private var optionsAdapter: OptionsAdapter = OptionsAdapter {
//            onItemSelected(it, absoluteAdapterPosition)
//        }
//
//        fun bind(currentItem: Question) {
//            binding.questionName.text =currentItem.name
//            initRecyclerView(currentItem.options)
//        }
//
//        private fun initRecyclerView(options: List<Option>) {
//            optionsAdapter.items = options
//            bindRecyclerView()
//        }
//
//        private fun bindRecyclerView() {
//            binding.apply {
//                optionsRecyclerView.apply {
//                    setHasFixedSize(true)
//                    adapter = optionsAdapter
//                    layoutManager =
//                        LinearLayoutManager(binding.root.context)
//                }
//            }
//        }
//
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val binding =
//            ItemQuestionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return ViewHolder(binding){option, id ->
//            onOptionClicked(option, items[id])
//        }
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val currentItem = items[position]
//        holder.bind(currentItem)
//    }
//
//    override fun getItemCount() = items.size
//}