package abm.co.studycards.ui.home

import abm.co.studycards.MainActivity
import abm.co.studycards.R
import abm.co.studycards.data.model.AvailableLanguages
import abm.co.studycards.data.model.vocabulary.Category
import abm.co.studycards.databinding.FragmentHomeBinding
import abm.co.studycards.util.base.BaseBindingFragment
import abm.co.studycards.util.fromHtml
import abm.co.studycards.util.getMyColor
import abm.co.studycards.util.launchAndRepeatWithViewLifecycle
import abm.co.studycards.util.navigate
import android.app.AlertDialog
import android.content.Intent
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class HomeFragment : BaseBindingFragment<FragmentHomeBinding>(R.layout.fragment_home),
    CategoryAdapter.CategoryAdapterListener {

    private val viewModel: HomeViewModel by viewModels()
    private var categoryAdapter: CategoryAdapter? = null

    override fun initUI(savedInstanceState: Bundle?) {
        binding.homefragment = this
        initFABMenu()
        initRecyclerView()
        changeStatusBar()
        setSourceAndTargetLanguages()
        collectData()
    }

    private fun collectData() {
        launchAndRepeatWithViewLifecycle(Lifecycle.State.STARTED) {
            viewModel.stateFlow.collect {
                when (it) {
                    is CategoryUiState.Error -> errorOccurred()
                    CategoryUiState.Loading -> onLoading()
                    is CategoryUiState.Success -> onSuccess(it.value)
                }
            }
        }
    }

    private fun onSuccess(value: List<Category>) = binding.run {
        recyclerView.visibility = View.VISIBLE
        stopShimmer()
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
    }

    private fun errorOccurred() = binding.run {
        binding.error.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        stopShimmer()
    }

    private fun changeStatusBar() {
        requireActivity().window.statusBarColor = getMyColor(R.color.background)
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
        floatingActionButton.animate().rotation(45F)
        floatingActionButtonWord.animate()
            .translationY(-resources.getDimension(R.dimen.standard_80))
        floatingActionButtonCategory.animate()
            .translationY(-resources.getDimension(R.dimen.standard_160))
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
        changeTargetWithSource()
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
            adapter = categoryAdapter
            addItemDecoration(getItemDecoration())
        }
    }

    private fun getItemDecoration() = DividerItemDecoration(
        requireContext(),
        DividerItemDecoration.VERTICAL,
    )

    private fun setSourceAndTargetLanguages() {
        val nativeLangDrawable = AvailableLanguages.getLanguageDrawableByCode(viewModel.sourceLang)
        val targetLangDrawable = AvailableLanguages.getLanguageDrawableByCode(viewModel.targetLang)
        val overlayImage =
            ContextCompat.getDrawable(requireContext(), R.drawable.overlayed_flags) as LayerDrawable
        val replaceNativeDrawable =
            ContextCompat.getDrawable(requireContext(), nativeLangDrawable) as Drawable
        val replaceTargetDrawable =
            ContextCompat.getDrawable(requireContext(), targetLangDrawable) as Drawable
        val testFactor1 = overlayImage.setDrawableByLayerId(R.id.first_flag, replaceNativeDrawable)
        val testFactor2 = overlayImage.setDrawableByLayerId(R.id.second_flag, replaceTargetDrawable)
        if (testFactor1 && testFactor2)
            binding.flagImage.setImageDrawable(overlayImage)
        binding.targetLanguage.text =
            AvailableLanguages.getLanguageNameByCode(requireContext(), viewModel.targetLang)
        binding.sourceLanguage.text =
            AvailableLanguages.getLanguageNameByCode(requireContext(), viewModel.sourceLang)

    }

    private fun changeTargetWithSource() {
        viewModel.changePreferenceNativeWithTargetLanguages()
        binding.apply {
            targetLanguage.text =
                AvailableLanguages.getLanguageNameByCode(requireContext(), viewModel.targetLang)
            sourceLanguage.text =
                AvailableLanguages.getLanguageNameByCode(requireContext(), viewModel.sourceLang)
        }
    }


    private fun initFABMenu() {
        binding.floatingActionButton.animate().rotation(-180F).duration = 500
    }

    private fun closeFABMenu() = binding.run {
        viewModel.fabMenuOpened = false
        recyclerView.alpha = 1f
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
        val action = HomeFragmentDirections.actionHomeFragmentToSelectLearnTypeDialogFragment(
            category
        )
        navigate(action)
    }

    override fun onLongClickCategory(category: Category) {
        showAlertToDeleteCategory(category)
    }

    fun navigateToSelectLanguages() {
        val nav = HomeFragmentDirections.actionHomeFragmentToSelectLanguageFragment()
        navigate(nav)
    }

    private fun isFabOpen(): Boolean {
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
            .setNegativeButton(getString(R.string.cancel_upper)) { v, _ ->
                v.dismiss()
            }.create().show()
    }

    override fun onDestroyView() {
        viewModel.fabMenuOpened = false
        super.onDestroyView()
    }
}