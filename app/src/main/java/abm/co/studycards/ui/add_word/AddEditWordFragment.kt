package abm.co.studycards.ui.add_word

import abm.co.studycards.MainActivity
import abm.co.studycards.R
import abm.co.studycards.data.model.oxford.ResultsEntry
import abm.co.studycards.databinding.FragmentAddEditWordBinding
import abm.co.studycards.setDefaultStatusBar
import abm.co.studycards.util.Constants.REQUEST_CATEGORY_KEY
import abm.co.studycards.util.Constants.REQUEST_DICTIONARY_KEY
import abm.co.studycards.util.base.BaseBindingFragment
import abm.co.studycards.util.launchAndRepeatWithViewLifecycle
import abm.co.studycards.util.navigate
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class AddEditWordFragment :
    BaseBindingFragment<FragmentAddEditWordBinding>(R.layout.fragment_add_edit_word),
    OnFocusChangeListener {

    private val viewModel: AddEditWordViewModel by viewModels()

    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (viewModel.backPressedTime + 2000 > System.currentTimeMillis()) {
                findNavController().popBackStack()
                isEnabled = false
            } else {
                toast(getString(R.string.back_click_again_to_exit))
            }
            viewModel.backPressedTime = System.currentTimeMillis()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        collectData()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun initUI(savedInstanceState: Bundle?) {
        setToolbar()
        initBindings()
        setResultListener()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun setToolbar() {
        requireActivity().setDefaultStatusBar()
        (activity as MainActivity).setToolbar(binding.toolbar, findNavController())
    }

    private fun setResultListener() {
        setFragmentResultListener(REQUEST_CATEGORY_KEY) { _, bundle ->
            viewModel.changeCategory(bundle.getParcelable("category"))
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
                        shakeView(binding.category)
                    }

                    is AddEditWordEventChannel.ChangeImageVisibility -> {
                        if (it.enabled && !binding.wordImageContainer.isVisible) {
                            binding.containerLayout.animate().translationY(
                                resources.getDimension(R.dimen.square_word_image_height)
                            ).withStartAction {
                                binding.wordImageContainer.animate().alpha(1f).duration = 0
                            }.withEndAction {
                                binding.containerLayout.animate().translationY(0f).duration = 0
                                viewModel.imageVisibleStateFlow.value = true
                            }.duration = 500
                        } else if (!it.enabled && binding.wordImageContainer.isVisible) {
                            binding.containerLayout.animate().translationY(
                                -resources.getDimension(R.dimen.square_word_image_height)
                            ).withStartAction {
                                binding.wordImageContainer.animate().alpha(0f).duration = 0
                            }.withEndAction {
                                binding.containerLayout.animate().translationY(0f).duration = 0
                                viewModel.imageVisibleStateFlow.value = false
                            }.duration = 500
                        }

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
        imageLayout.setEndIconOnClickListener {
            binding.image.setText("")
            viewModel.changeEditableImageUrl(false)
        }
        image.setOnFocusChangeListener { _, hasFocus ->
            viewModel.changeEditableImageUrl(hasFocus)
        }
        image.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                image.clearFocus()
            }
            false
        }
    }

    fun shakeImageContainer() {
        shakeView(binding.imageLayout)
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

    private fun shakeView(view: View) {
        val shake: Animation =
            AnimationUtils.loadAnimation(requireContext(), R.anim.shake)
        view.startAnimation(shake)
    }

    override fun onDestroyView() {
        binding.wordImageContainer.animate().cancel()
        binding.containerLayout.animate().cancel()
        super.onDestroyView()
    }

    override fun onFocusChange(p0: View?, hasFocus: Boolean) {

    }
}
