package abm.co.studycards.ui.home

import abm.co.studycards.MainActivity
import abm.co.studycards.R
import abm.co.studycards.data.model.AvailableLanguages
import abm.co.studycards.data.model.vocabulary.Category
import abm.co.studycards.databinding.FragmentHomeBinding
import abm.co.studycards.setDefaultStatusBar
import abm.co.studycards.util.base.BaseBindingFragment
import abm.co.studycards.util.fromHtml
import abm.co.studycards.util.launchAndRepeatWithViewLifecycle
import abm.co.studycards.util.leftAndRightDrawable
import abm.co.studycards.util.navigate
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : BaseBindingFragment<FragmentHomeBinding>(R.layout.fragment_home),
    CategoryAdapter.CategoryAdapterListener {

    private val viewModel: HomeViewModel by viewModels()
    private var categoryAdapter: CategoryAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        collectData()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun initUI(savedInstanceState: Bundle?) {
        binding.homefragment = this
        initFABMenu()
        initRecyclerView()
        changeStatusBar()
        setSourceAndTargetLanguages()
    }

    private fun collectData() {
        launchAndRepeatWithViewLifecycle(Lifecycle.State.STARTED) {
            viewModel.stateFlow.collect {
                when (it) {
                    is CategoryUiState.Error -> errorOccurred(it.msg)
                    CategoryUiState.Loading -> onLoading()
                    is CategoryUiState.Success -> onSuccess(it.value)
                }
            }
        }
    }

    private fun onSuccess(value: List<Category>) = binding.run {
        recyclerView.visibility = View.VISIBLE
        stopShimmer()
        error.visibility = View.GONE
        categoryAdapter?.submitList(value)
    }

    private fun stopShimmer() = binding.shimmerLayout.run {
        stopShimmer()
        hideShimmer()
        visibility = View.GONE
    }

    private fun startShimmer() = binding.shimmerLayout.run {
        startShimmer()
        visibility = View.VISIBLE
    }

    private fun onLoading() = binding.run {
        startShimmer()
        recyclerView.visibility = View.GONE
        error.visibility = View.GONE
    }

    private fun errorOccurred(@StringRes errorId: Int) = binding.run {
        error.visibility = View.VISIBLE
        error.text = getStr(errorId)
        recyclerView.visibility = View.GONE
        stopShimmer()
    }

    private fun changeStatusBar() {
        requireActivity().setDefaultStatusBar()
    }

    fun onFloatingActionClick() {
        if (!viewModel.fabMenuOpened) {
            showFABMenu()
        } else {
            closeFABMenu()
        }
    }

    private fun showFABMenu() = binding.run {
        viewModel.fabMenuOpened = true
        setVisibility(true)
        recyclerView.alpha = 0.6f
        toolbar.alpha = 0.6f
        floatingActionButton.animate().rotation(45F)
        floatingActionButtonWord.animate()
            .translationY(-floatingActionButton.height * 1.2f)
        floatingActionButtonCategory.animate()
            .translationY(
                -floatingActionButtonWord.height.toFloat()
                        - floatingActionButton.height * 1.5f
            )
    }

    private fun setVisibility(isVisible: Boolean) = binding.run {
        floatingActionButtonCategory.isVisible = isVisible
        floatingActionButtonWord.isVisible = isVisible
    }

    fun onFloatingActionWordClick() {
        val action =
            HomeFragmentDirections.actionHomeFragmentToAddEditWordFragment(
                word = null,
                categoryName = viewModel.defaultCategory?.mainName,
                categoryId = viewModel.defaultCategory?.id
            )
        navigate(action)
    }

    fun onFloatingActionCategoryClick() {
        val action = HomeFragmentDirections.actionHomeFragmentToAddEditCategoryFragment()
        navigate(action)
    }

    fun onChangeLanguageClicked() {
        if (isFabOpen()) return
        viewModel.changePreferenceNativeWithTargetLanguages()
        reCreateItself()
    }

    private fun reCreateItself() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun initRecyclerView() {
        categoryAdapter = CategoryAdapter(this)
        binding.recyclerView.run {
            setOnTouchListener(RVClickHandler(this))
            adapter = categoryAdapter
            addItemDecoration(getItemDecoration())
            setOnClickListener { isFabOpen() }
        }
    }

    private fun getItemDecoration() = DividerItemDecoration(
        requireContext(),
        DividerItemDecoration.VERTICAL,
    )

    private fun setSourceAndTargetLanguages() {
        val nativeLangDrawable = AvailableLanguages.getLanguageDrawableByCode(viewModel.sourceLang)
        val targetLangDrawable = AvailableLanguages.getLanguageDrawableByCode(viewModel.targetLang)
        binding.sourceLanguage.leftAndRightDrawable(
            left = nativeLangDrawable,
            sizeRes = R.dimen.home_page_language_image_size
        )
        binding.targetLanguage.leftAndRightDrawable(
            left = targetLangDrawable,
            sizeRes = R.dimen.home_page_language_image_size
        )
        binding.targetLanguage.text =
            AvailableLanguages.getLanguageShortNameByCode(requireContext(), viewModel.targetLang)
        binding.sourceLanguage.text =
            AvailableLanguages.getLanguageShortNameByCode(requireContext(), viewModel.sourceLang)
    }

    private fun initFABMenu() {
        binding.floatingActionButton.animate().rotation(-180F).duration = 500
    }

    private fun closeFABMenu() = binding.run {
        viewModel.fabMenuOpened = false
        recyclerView.alpha = 1f
        toolbar.alpha = 1f
        floatingActionButton.animate().rotation(-180F).withEndAction {
            setVisibility(false)
        }.duration = 500
        floatingActionButtonWord.animate().translationY(0F).duration = 500
        floatingActionButtonCategory.animate().translationY(0F).duration = 500
    }

    override fun onCategoryClicked(category: Category) {
        if (isFabOpen()) return
        val action =
            HomeFragmentDirections.actionHomeFragmentToInCategoryFragment(category)
        navigate(action)
    }

    override fun onPlay(category: Category) {
        if (isFabOpen()) return
        val action = HomeFragmentDirections.actionHomeFragmentToSelectGamesTypeDialogFragment(
            category
        )
        navigate(action)
    }

    override fun onLongClickCategory(category: Category) {
        if (isFabOpen()) return
        showAlertToDeleteCategory(category)
    }

    fun navigateToSelectLanguages(isTarget: Boolean) {
        if (isFabOpen()) return
        val nav = HomeFragmentDirections
            .actionHomeFragmentToSelectLanguageAnyWhereFragment(isTarget)
        navigate(nav)
    }

    fun isFabOpen(): Boolean {
        if (viewModel.fabMenuOpened) {
            closeFABMenu()
            return true
        }
        return false
    }

    private fun showAlertToDeleteCategory(category: Category) {
        AlertDialog.Builder(requireContext())
            .setMessage(getString(R.string.do_u_want_to_delete_this, category.mainName).fromHtml())
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                viewModel.removeCategory(category)
            }
            .setNegativeButton(getString(R.string.cancel)) { v, _ ->
                v.dismiss()
            }.create().show()
    }

    override fun onDestroyView() {
        viewModel.fabMenuOpened = false
        binding.floatingActionButton.animate().cancel()
        binding.floatingActionButtonWord.animate().cancel()
        binding.floatingActionButtonCategory.animate().cancel()
        super.onDestroyView()
    }
}