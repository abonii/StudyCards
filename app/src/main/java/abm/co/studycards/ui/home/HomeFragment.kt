package abm.co.studycards.ui.home

import abm.co.studycards.R
import abm.co.studycards.data.model.AvailableLanguages
import abm.co.studycards.data.model.vocabulary.Category
import abm.co.studycards.databinding.FragmentHomeBinding
import abm.co.studycards.util.base.BaseBindingFragment
import abm.co.studycards.util.getMyColor
import abm.co.studycards.util.navigate
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class HomeFragment : BaseBindingFragment<FragmentHomeBinding>(R.layout.fragment_home),
    CategoryAdapter.CategoryAdapterListener {

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var options: FirebaseRecyclerOptions<Category>

    override fun initViews(savedInstanceState: Bundle?) {
        viewModel.fetchCategories()
        initUi()
        collectData()
    }

    private fun collectData() {
        lifecycleScope.launchWhenResumed {
            viewModel.stateFlow.collectLatest {
                when (it) {
                    is CategoryUi.Error -> {
                        showLog(it.error.toString())
                    }
                    CategoryUi.Loading -> {

                    }
                    is CategoryUi.Success -> {
                        showLog(it.value.toString())
                    }
                }
            }
        }
    }

    private fun initUi() {
        initFABMenu()
        initRecyclerView()
        changeStatusBar()
        setSourceAndTargetLanguages()
        setClickListeners()
    }

    private fun changeStatusBar() {
        requireActivity().window.statusBarColor = getMyColor(R.color.background)
    }

    private fun setClickListeners() {
        setOnBackPressed()
        binding.apply {
            root.setOnClickListener { isFabOpen() }
            floatingActionButton.setOnClickListener { onFloatingActionClick() }
            floatingActionButtonWord.setOnClickListener { onFloatingActionWordClick() }
            floatingActionButtonCategory.setOnClickListener { onFloatingActionCategoryClick() }
            deleteCategory.setOnClickListener { onDeleteClicked() }
            cancel.setOnClickListener { onCancelClicked() }
            changeLayout.setOnClickListener { onChangeLanguageClicked() }
        }
    }


    private fun onFloatingActionClick() {
        if (!viewModel.fabMenuOpened) {
            showFABMenu()
        } else {
            closeFABMenu()
        }
    }

    private fun showFABMenu() {
        viewModel.fabMenuOpened = true
        setVisibility()
        binding.recyclerView.alpha = 0.6f
        binding.floatingActionButton.animate().rotation(45F)
        binding.floatingActionButtonWord.animate()
            .translationY(-resources.getDimension(R.dimen.standard_80))
        binding.floatingActionButtonCategory.animate()
            .translationY(-resources.getDimension(R.dimen.standard_160))
    }

    private fun setVisibility() {
        binding.floatingActionButtonCategory.visibility = View.VISIBLE
        binding.floatingActionButtonWord.visibility = View.VISIBLE
    }

    private fun onFloatingActionWordClick() {
//        val action =
//            HomeFragmentDirections
//                .actionNavigationHomeToAddEditFragment(category = viewModel.category)
//        navigate(action)
    }

    private fun onFloatingActionCategoryClick() {
        val action = HomeFragmentDirections
            .actionHomeFragmentToAddEditCategoryFragment()
        navigate(action)
    }

    private fun onChangeLanguageClicked() {
        changeTargetWithSource()
        reCreateItself()
    }

    private fun reCreateItself() {
        findNavController().navigate(
            R.id.navigation_home,
            arguments,
            NavOptions.Builder()
                .setPopUpTo(R.id.navigation_home, true)
                .build()
        )
    }

    override fun onStart() {
        super.onStart()
        categoryAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        categoryAdapter.stopListening()
    }

    private fun initRecyclerView() {
        options = FirebaseRecyclerOptions.Builder<Category>()
            .setQuery(viewModel.categoriesDbRef, Category::class.java)
            .build()
        categoryAdapter = CategoryAdapter(this, options)

        binding.recyclerView.apply {
            itemAnimator = null
            setHasFixedSize(true)
            adapter = categoryAdapter
            addItemDecoration(
                DividerItemDecoration(
                    context,
                    DividerItemDecoration.VERTICAL,
                )
            )
        }
    }

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
        binding.apply {
            val target = targetLanguage.text.toString()
            targetLanguage.text = sourceLanguage.text
            sourceLanguage.text = target
        }
        changePreferenceNativeWithTargetLanguages()
    }

    private fun changePreferenceNativeWithTargetLanguages() {
        viewModel.sourceLang = viewModel.targetLang
        viewModel.targetLang = viewModel.sourceLang
    }


    private fun initFABMenu() {
        binding.floatingActionButton.animate().rotation(-180F).duration = 500
    }

    private fun closeFABMenu() {
        viewModel.fabMenuOpened = false
        binding.recyclerView.alpha = 1f
        binding.floatingActionButton.animate().rotation(-180F).withEndAction {
            setInvisibility()
        }.duration = 500
        binding.floatingActionButtonWord.animate().translationY(0F).duration = 500
        binding.floatingActionButtonCategory.animate().translationY(0F).duration = 500

    }

    override fun onCategoryClicked(category: Category) {
        if (isFabOpen()) return
        val action =
            HomeFragmentDirections.actionHomeFragmentToAddEditCategoryFragment(category)
        navigate(action)
    }

    override fun onPlay(category: Category) {
        if (isFabOpen()) return
//        val action = HomeFragmentDirections.actionNavigationHomeToSelectLearnTypeDialogFragment(
//            category
//        )
//        navigate(action)
    }

    override fun onSelectItem(isShortClickActivated: Boolean, selectedItemsCount: Int) {
        isFabOpen()
        binding.apply {
            binding.floatingActionButton.isVisible = !isShortClickActivated
            cancel.isVisible = isShortClickActivated
            deleteCategory.isVisible = isShortClickActivated
        }
    }

    private fun setOnBackPressed() {
//        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
//            object : OnBackPressedCallback(true) {
//                override fun handleOnBackPressed() {
//                    if (!categoryAdapter.isShortClickActivated && !viewModel.fabMenuOpened) {
//                        if (viewModel.pressedTime + 2000 > System.currentTimeMillis()) {
//                            isEnabled = false
//                            requireActivity().finishAffinity()
//                        } else {
//                            toast(binding.root, "Press back again to exit")
//                        }
//                        viewModel.pressedTime = System.currentTimeMillis()
//                    } else {
//                        closeFABMenu()
////                        categoryAdapter.disableSelectableItems()
//                        onSelectItem(false)
//                    }
//                }
//            })
    }

    private fun onCancelClicked() {
//        categoryAdapter.disableSelectableItems()
        onSelectItem(false)
    }

    private fun isFabOpen(): Boolean {
        if (viewModel.fabMenuOpened) {
            closeFABMenu()
            return false
        }
        return true
    }

    private fun onDeleteClicked() {
//        val items = categoryAdapter.selectedItems()
//        AlertDialog.Builder(requireContext())
//            .setMessage("Do you want to delete ${items.size} categor${if (items.size > 1) "ies" else "y"}?")
//            .setPositiveButton(getString(R.string.ok)) { _, _ ->
////                viewModel.deleteCategories(items)
//                categoryAdapter.isShortClickActivated = false
//                onSelectItem(false)
//            }
//            .setNegativeButton(getString(R.string.cancel)) { v, _ ->
//                v.dismiss()
//            }.create().show()
    }

    private fun setInvisibility() {
        binding.floatingActionButtonWord.visibility = View.GONE
        binding.floatingActionButtonCategory.visibility = View.GONE
    }


}