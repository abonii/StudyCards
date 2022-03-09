package abm.co.studycards.ui.vocabulary

import abm.co.studycards.util.Constants.VOCABULARY_NUM_TABS
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class PagerAdapter constructor(activity: FragmentActivity) :
    FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = VOCABULARY_NUM_TABS

    override fun createFragment(position: Int): Fragment {
        return VocabularyTabFragment.newInstance(position)
    }
}