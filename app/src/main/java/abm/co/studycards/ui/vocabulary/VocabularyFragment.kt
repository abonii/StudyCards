package abm.co.studycards.ui.vocabulary

import abm.co.studycards.R
import abm.co.studycards.databinding.FragmentVocabularyBinding
import abm.co.studycards.util.base.BaseBindingFragment
import android.os.Bundle
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VocabularyFragment :
    BaseBindingFragment<FragmentVocabularyBinding>(R.layout.fragment_vocabulary) {

    private val viewModel: VocabularyViewModel by viewModels()

    override fun initViews(savedInstanceState: Bundle?) {

    }
}