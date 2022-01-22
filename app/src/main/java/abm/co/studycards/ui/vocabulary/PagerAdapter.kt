package abm.co.studycards.ui.vocabulary

import abm.co.studycards.R
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

private const val NUM_TABS = 3

class PagerAdapter constructor(activity: FragmentActivity) :
    FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = NUM_TABS

    override fun createFragment(position: Int): Fragment {
        return VocabularyTabFragment.newInstance(position)
    }
}
//enum class VocabularyTabType {
//    KNOWN,
//    DOUBTED,
//    UNKNOWN;
//
//    companion object {
//        fun getType(num: Int): VocabularyTabType {
//            return when (num) {
//                0 -> KNOWN
//                1 -> DOUBTED
//                else -> UNKNOWN
//            }
//        }
//    }
//    fun getName(context: Context):String{
//        return when(this){
//            KNOWN -> context.getString(R.string.know)
//            DOUBTED -> context.getString(R.string.uncertain)
//            UNKNOWN -> context.getString(R.string.unknown)
//        }
//    }
//}