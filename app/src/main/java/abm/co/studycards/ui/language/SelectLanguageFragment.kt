package abm.co.studycards.ui.language

import abm.co.studycards.MainActivity
import abm.co.studycards.R
import abm.co.studycards.data.model.Language
import abm.co.studycards.databinding.FragmentSelectLanguageBinding
import abm.co.studycards.util.base.BaseBindingFragment
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectLanguageFragment :
    BaseBindingFragment<FragmentSelectLanguageBinding>(R.layout.fragment_select_language) {

    private val viewModel: SelectLanguageViewModel by viewModels()

    override fun initUI(savedInstanceState: Bundle?) {
        setBindings()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun setBindings() {
        setToolbar()
        val nativeAdapter = LanguageAdapter(::onClickWithCode, false)
        val targetAdapter = LanguageAdapter(::onClickWithCode, true)
        nativeAdapter.submitList(viewModel.getAvailableLanguages())
        targetAdapter.submitList(viewModel.getAvailableLanguages())
        binding.run {
            rvNativeLanguage.adapter = nativeAdapter
            rvLearnLanguage.adapter = targetAdapter
            readBtn.setOnClickListener { onReadBtnClicked() }
        }
    }

    private fun setToolbar() {
        if (viewModel.prefs.getSourceLanguage().isNotEmpty() && viewModel.prefs.getTargetLanguage()
                .isNotEmpty()
        ) {
            binding.toolbar.setNavigationIcon(R.drawable.ic_clear)
        }
    }

    private fun onReadBtnClicked() {
        if (binding.readBtn.alpha == 1f) {
            viewModel.onSaveLanguages()
            reCreateItself()
        }
    }

    private fun reCreateItself() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }


    private fun onClickWithCode(
        lang: Language,
        isTargetLanguage: Boolean,
    ) {
        viewModel.onClickWithCode(lang, isTargetLanguage)
        checkIfSelectedCorrectly()
    }

    private fun checkIfSelectedCorrectly() {
        if (viewModel.isSelectedCorrectly()) {
            binding.readBtn.alpha = 1f
        } else {
            binding.readBtn.alpha = 0.6f
        }
    }

    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (viewModel.prefs.getSourceLanguage()
                    .isNotEmpty() && viewModel.prefs.getTargetLanguage().isNotEmpty()
            ) {
                findNavController().popBackStack()
                isEnabled = false
            } else {
                requireActivity().finishAffinity()
            }
        }
    }
}