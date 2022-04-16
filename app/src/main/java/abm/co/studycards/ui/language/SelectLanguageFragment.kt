package abm.co.studycards.ui.language

import abm.co.studycards.MainActivity
import abm.co.studycards.R
import abm.co.studycards.data.model.AvailableLanguages
import abm.co.studycards.data.model.Language
import abm.co.studycards.data.pref.Prefs
import abm.co.studycards.databinding.FragmentSelectLanguageBinding
import abm.co.studycards.util.base.BaseBindingFragment
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SelectLanguageFragment :
    BaseBindingFragment<FragmentSelectLanguageBinding>(R.layout.fragment_select_language),
    LanguageAdapter.OnClickWithPosition {

    @Inject
    lateinit var prefs: Prefs
    private var nativeLanguagePosition = ""
    private var targetLanguagePosition = ""

    override fun initUI(savedInstanceState: Bundle?) {
        setBindings()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun setBindings() {
        setToolbar()
        if (prefs.getSourceLanguage().isEmpty() || prefs.getTargetLanguage().isEmpty()) {
            (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        }
        val nativeAdapter = LanguageAdapter(requireContext(), this, false)
        val targetAdapter = LanguageAdapter(requireContext(), this, true)
        nativeAdapter.addItems(AvailableLanguages.availableLanguages)
        targetAdapter.addItems(AvailableLanguages.availableLanguages)
        binding.run {
            rvNativeLanguage.adapter = nativeAdapter
            rvLearnLanguage.adapter = targetAdapter
            readBtn.setOnClickListener {
                onReadBtnClicked()
            }
        }
    }

    private fun setToolbar() {
        (activity as MainActivity).setToolbar(binding.toolbar, findNavController())
        binding.toolbar.setNavigationIcon(R.drawable.ic_clear)
    }

    private fun onReadBtnClicked() {
        if (binding.readBtn.alpha == 1f) {
            prefs.setSourceLanguage(nativeLanguagePosition)
            prefs.setTargetLanguage(targetLanguagePosition)
//            findNavController().navigate(SelectLanguageFragmentDirections.actionSelectLanguageFragmentToHomeFragment())
            reCreateItself()
        }
        }

    private fun reCreateItself() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }


    override fun onClickWithPosition(
        lang: Language,
        isTargetLanguage: Boolean,
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

    private val callback = object : OnBackPressedCallback(
        true
        /** true means that the callback is enabled */
    ) {
        override fun handleOnBackPressed() {
            if (prefs.getSourceLanguage().isNotEmpty() && prefs.getTargetLanguage().isNotEmpty()) {
                findNavController().popBackStack()
                isEnabled = false
            } else {
                requireActivity().finishAffinity()
            }
        }
    }

}