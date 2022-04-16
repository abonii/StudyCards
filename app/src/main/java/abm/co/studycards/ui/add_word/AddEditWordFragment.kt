package abm.co.studycards.ui.add_word

import abm.co.studycards.MainActivity
import abm.co.studycards.R
import abm.co.studycards.data.model.oxford.ResultsEntry
import abm.co.studycards.databinding.FragmentAddEditWordBinding
import abm.co.studycards.setDefaultStatusBar
import abm.co.studycards.util.Constants.REQUEST_CATEGORY_KEY
import abm.co.studycards.util.Constants.REQUEST_DICTIONARY_KEY
import abm.co.studycards.util.Constants.REQUEST_IMAGE_KEY
import abm.co.studycards.util.base.BaseBindingFragment
import abm.co.studycards.util.launchAndRepeatWithViewLifecycle
import abm.co.studycards.util.navigate
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class AddEditWordFragment :
    BaseBindingFragment<FragmentAddEditWordBinding>(R.layout.fragment_add_edit_word) {

    private val viewModel: AddEditWordViewModel by viewModels()

    override fun initUI(savedInstanceState: Bundle?) {
        setToolbar()
        initBindings()
        collectData()
        setResultListener()
    }

    private fun setToolbar() {
        requireActivity().setDefaultStatusBar()
        (activity as MainActivity).setToolbar(binding.toolbar, findNavController())
    }

    private fun setResultListener() {
        setFragmentResultListener(REQUEST_CATEGORY_KEY) { _, bundle ->
            viewModel.changeCategory(bundle.getParcelable("category"))
        }
        setFragmentResultListener(REQUEST_IMAGE_KEY) { _, bundle ->
            viewModel.setImage(bundle.getString("image_url"))
        }
        setFragmentResultListener(REQUEST_DICTIONARY_KEY) { _, bundle ->
            viewModel.onDictionaryReceived(
                translations = bundle.getString("selected_translations"),
                examples = bundle.getString("selected_examples"),
                fromTarget = bundle.getBoolean("from_target")
            )
        }
    }

    private fun collectData() {
        launchAndRepeatWithViewLifecycle(Lifecycle.State.STARTED) {
            viewModel.eventChannel.collectLatest {
                when (it) {
                    is AddEditWordEventChannel.NavigateToDictionary -> {
                        directToOxfordWordsDialog(it.resultsEntry!!, it.fromSource)
                    }
                    AddEditWordEventChannel.PopBackStack -> findNavController().popBackStack()
                    AddEditWordEventChannel.ShakeCategory -> {
                        val shake: Animation =
                            AnimationUtils.loadAnimation(requireContext(), R.anim.shake)
                        binding.category.startAnimation(shake)
                    }
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.toast.collectLatest {
                toast(it)
            }
        }
    }

    private fun initBindings() = binding.run {
        viewmodel = viewModel
        wordFragment = this@AddEditWordFragment
        wordLayout.setEndIconOnClickListener {
            viewModel.fetchWord(true)
        }
        translatedLayout.setEndIconOnClickListener {
            viewModel.fetchWord(false)
        }
    }

    fun directToChooseImage() {
        val action = AddEditWordFragmentDirections
            .actionAddEditWordFragmentToImageDialogFragment(viewModel.word?.name)
        navigate(action)
    }

    fun directToCategoryDialogFragment() {
        val action =
            AddEditWordFragmentDirections
                .actionAddEditWordFragmentToSelectCategoryDialogFragment(viewModel.currentCategoryId)
        navigate(action)
    }


    private fun directToOxfordWordsDialog(entry: ResultsEntry, fromSource: Boolean) {
        val action =
            AddEditWordFragmentDirections.actionAddEditWordFragmentToDictionaryDialogFragment(
                entry, fromSource
            )
        navigate(action)
    }


}
