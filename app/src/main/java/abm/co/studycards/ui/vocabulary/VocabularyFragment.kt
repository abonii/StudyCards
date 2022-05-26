package abm.co.studycards.ui.vocabulary

import abm.co.studycards.R
import abm.co.studycards.databinding.FragmentVocabularyBinding
import abm.co.studycards.util.Constants.TAG_ERROR
import abm.co.studycards.util.base.BaseBindingFragment
import android.os.Bundle
import android.util.Log
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VocabularyFragment :
    BaseBindingFragment<FragmentVocabularyBinding>(R.layout.fragment_vocabulary) {

    private lateinit var fragmentsArray: Array<String>
    private var adapterP: PagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentsArray = resources.getStringArray(R.array.vocabulary_tabs)
    }

    override fun initUI(savedInstanceState: Bundle?) {
        setStatusBarColor()
        adapterP = PagerAdapter(requireActivity())
        binding.pager.run {
            adapter = adapterP
            isUserInputEnabled = false
        }
        TabLayoutMediator(binding.tabLayout, binding.pager, false, false) { tab, position ->
            tab.text = fragmentsArray[position]
        }.attach()
    }

    private fun setStatusBarColor() {
        requireActivity().window.statusBarColor =
            resources.getColor(R.color.blue_light_status, null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapterP = null
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        try {
            super.onViewStateRestored(savedInstanceState)
        } catch (e: Exception) {
            Log.e(TAG_ERROR, "onViewStateRestored: ${e.message}")
        }
    }

}