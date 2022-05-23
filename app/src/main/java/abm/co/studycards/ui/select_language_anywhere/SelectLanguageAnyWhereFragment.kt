package abm.co.studycards.ui.select_language_anywhere

import abm.co.studycards.MainActivity
import abm.co.studycards.R
import abm.co.studycards.domain.model.Language
import abm.co.studycards.databinding.FragmentSelectLanguageAnyWhereBinding
import abm.co.studycards.setDefaultStatusBar
import abm.co.studycards.util.base.BaseBindingFragment
import abm.co.studycards.util.launchAndRepeatWithViewLifecycle
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class SelectLanguageAnyWhereFragment :
    BaseBindingFragment<FragmentSelectLanguageAnyWhereBinding>(R.layout.fragment_select_language_any_where) {

    private val viewModel: SelectLanguageAnyWhereViewModel by viewModels()
    private lateinit var parentLanguageAdapter: LanguageAnyWhereParentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launchWhenStarted {
            viewModel.toast.collectLatest {
                toast(it)
            }
        }
    }

    override fun initUI(savedInstanceState: Bundle?) {
        setBindings()
        launchAndRepeatWithViewLifecycle(Lifecycle.State.STARTED) {
            viewModel.parentLanguageStateFlow.collectLatest {
                parentLanguageAdapter.submitList(it)
            }
        }
    }

    private fun setBindings() = binding.run {
        requireActivity().setDefaultStatusBar()
        (activity as MainActivity).setToolbar(toolbar)
        toolbar.navigationIcon = getImg(R.drawable.ic_clear)
        parentLanguageAdapter = LanguageAnyWhereParentAdapter().apply {
            selectedItemPos = viewModel.selectedItemPos
            lastItemSelectedPos = viewModel.lastItemSelectedPos
        }
        save.setOnClickListener {
            if (parentLanguageAdapter.selectedItemPos != -1)
                viewModel.save(
                    (parentLanguageAdapter
                        .currentList[parentLanguageAdapter.selectedItemPos]
                            as LanguageVHUI.Language).value
                ) {
                    reCreateItself()
                }
            else toast(R.string.u_don_t_selected_word)

        }
        nestedLanguagesRv.adapter = parentLanguageAdapter
    }

    private fun reCreateItself() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.selectedItemPos = parentLanguageAdapter.selectedItemPos
        viewModel.lastItemSelectedPos = parentLanguageAdapter.lastItemSelectedPos
    }
}

data class LanguageSelectable(val language: Language, var isSelected: Boolean)