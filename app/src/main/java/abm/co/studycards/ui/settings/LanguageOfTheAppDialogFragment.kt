package abm.co.studycards.ui.settings

import abm.co.studycards.R
import abm.co.studycards.data.model.AvailableLanguages
import abm.co.studycards.data.model.Language
import abm.co.studycards.databinding.LanguageOfTheAppDialogBinding
import abm.co.studycards.util.Constants
import abm.co.studycards.util.Constants.REQUEST_SYSTEM_LANGUAGE_KEY
import abm.co.studycards.util.base.BaseDialogFragment
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.DividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LanguageOfTheAppDialogFragment :
    BaseDialogFragment<LanguageOfTheAppDialogBinding>(R.layout.language_of_the_app_dialog) {

    override fun initUI(savedInstanceState: Bundle?) {
        binding.listView.adapter = LanguageOfTheAppAdapter(
            AvailableLanguages.systemLanguages,
            ::onSelectSystemLanguage
        )
        binding.listView.addItemDecoration(getItemDecoration())
    }

    private fun onSelectSystemLanguage(language: Language) {
        setFragmentResult(REQUEST_SYSTEM_LANGUAGE_KEY, bundleOf("language" to language))
        dismiss()
    }

    private fun getItemDecoration() = DividerItemDecoration(
        requireContext(),
        DividerItemDecoration.VERTICAL,
    )

}