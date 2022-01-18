package abm.co.studycards.ui.vocabulary

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

private const val NUM_TABS = 3

class PagerAdapter constructor(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int = NUM_TABS

    override fun createFragment(position: Int): Fragment {
        return VocabularyTabFragment(VocabularyTabType.getType(position))
    }
}

enum class VocabularyTabType {
    KNOWN,
    DOUBTED,
    UNKNOWN;

    fun getNumber(): Int {
        return when (this) {
            KNOWN -> 0
            DOUBTED -> 1
            UNKNOWN -> 2
        }
    }

    companion object {
        fun getType(num: Int): VocabularyTabType {
            return when (num) {
                0 -> KNOWN
                1 -> DOUBTED
                else -> UNKNOWN
            }
        }
    }
}