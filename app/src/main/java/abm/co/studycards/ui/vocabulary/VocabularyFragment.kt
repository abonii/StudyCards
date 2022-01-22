package abm.co.studycards.ui.vocabulary

import abm.co.studycards.R
import abm.co.studycards.databinding.FragmentVocabularyBinding
import abm.co.studycards.util.base.BaseBindingFragment
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VocabularyFragment :
    BaseBindingFragment<FragmentVocabularyBinding>(R.layout.fragment_vocabulary) {


    private val viewModel: VocabularyViewModel by viewModels()
    private lateinit var fragmentsArray: Array<String>

    override fun initViews(savedInstanceState: Bundle?) {
        setStatusBarColor()
        val adapterP = PagerAdapter(requireActivity())
        fragmentsArray = resources.getStringArray(R.array.vocabulary_tabs)
        binding.pager.apply {
            adapter = adapterP
            isUserInputEnabled = false
            offscreenPageLimit = 2
        }
        TabLayoutMediator(binding.tabLayout, binding.pager, true, false) { tab, position ->
            tab.text = fragmentsArray[position]
            tab.position.let { binding.pager.setCurrentItem(it, false) }
        }.attach()
    }
    private fun setStatusBarColor(){
        requireActivity().window.statusBarColor = resources.getColor(R.color.blue_light_status,null)
    }
}