package abm.co.studycards.ui.settings

import abm.co.studycards.data.model.AvailableLanguages
import abm.co.studycards.data.model.Language
import abm.co.studycards.databinding.LanguageOfTheAppDialogBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LanguageOfTheAppDialogFragment(private val listener: LanguageOfTheAppAdapter.OnClick) :
    DialogFragment(), LanguageOfTheAppAdapter.OnClick {

    private var _binding: LanguageOfTheAppDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LanguageOfTheAppDialogBinding.inflate(inflater, container, false)
        binding.listView.adapter = LanguageOfTheAppAdapter(
            requireContext(),
            AvailableLanguages.systemLanguages,
            this
        )
        return binding.root
    }

    override fun onClick(language: Language) {
        listener.onClick(language)
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}