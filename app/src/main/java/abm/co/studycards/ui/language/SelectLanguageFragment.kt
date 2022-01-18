package abm.co.studycards.ui.language

import abm.co.studycards.MainActivity
import abm.co.studycards.R
import abm.co.studycards.data.model.AvailableLanguages
import abm.co.studycards.data.model.Language
import abm.co.studycards.data.pref.Prefs
import abm.co.studycards.databinding.FragmentSelectLanguageBinding
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SelectLanguageFragment : Fragment(R.layout.fragment_select_language),
    LanguageAdapter.OnClickWithPosition {

    @Inject
    lateinit var prefs: Prefs

    private lateinit var binding: FragmentSelectLanguageBinding
    private var nativeLanguagePosition = ""
    private var targetLanguagePosition = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSelectLanguageBinding.bind(view)
        setBindings()
    }

    private fun setBindings() {
        setToolbar()
        val nativeAdapter = LanguageAdapter(requireContext(), this, false)
        val targetAdapter = LanguageAdapter(requireContext(), this, true)
        nativeAdapter.addItems(AvailableLanguages.availableLanguages)
        targetAdapter.addItems(AvailableLanguages.availableLanguages)
        binding.apply {
            rvNativeLanguage.apply {
                adapter = nativeAdapter
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            }
            rvLearnLanguage.apply {
                adapter = targetAdapter
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            }
            readBtn.setOnClickListener {
                onReadBtnClicked()
            }
        }
    }

    private fun setToolbar() {
        (activity as MainActivity).setToolbar(binding.toolbar, findNavController())
    }

    private fun onReadBtnClicked() {
        if (binding.readBtn.alpha == 1f) {
            prefs.setSourceLanguage(nativeLanguagePosition)
            prefs.setTargetLanguage(targetLanguagePosition)
            if (parentFragmentManager.backStackEntryCount > 0) {
                findNavController().popBackStack()
            } else
                findNavController().navigate(R.id.navigation_home)
        }
    }


    override fun onClickWithPosition(
        lang: Language,
        isTargetLanguage: Boolean
    ) {
        if (isTargetLanguage) {
            targetLanguagePosition = lang.code
        } else {
            nativeLanguagePosition = lang.code
        }
        checkIfSelectedCorrectly()
    }

    private fun checkIfSelectedCorrectly() {
        if (nativeLanguagePosition.isNotBlank()
            && targetLanguagePosition.isNotBlank()
            && nativeLanguagePosition != targetLanguagePosition
        ) {
            binding.readBtn.alpha = 1f
        } else {
            binding.readBtn.alpha = 0.6f
        }
    }

}