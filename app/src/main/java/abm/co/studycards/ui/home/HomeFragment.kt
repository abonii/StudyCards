package abm.co.studycards.ui.home

import abm.co.studycards.MainActivity
import abm.co.studycards.R
import abm.co.studycards.data.model.AvailableLanguages
import abm.co.studycards.data.model.vocabulary.Category
import abm.co.studycards.databinding.FragmentHomeBinding
import abm.co.studycards.util.base.BaseBindingFragment
import abm.co.studycards.util.getMyColor
import abm.co.studycards.util.navigate
import android.app.AlertDialog
import android.content.Intent
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : BaseBindingFragment<FragmentHomeBinding>(R.layout.fragment_home),
    CategoryAdapter.CategoryAdapterListener {

    private val viewModel: HomeViewModel by viewModels()
    private var categoryAdapter = CategoryAdapter(this)

    override fun initUI(savedInstanceState: Bundle?) {
        initUi()
        collectData()
        showLog("${viewModel.sourceLang} - ${viewModel.targetLang}")
    }

    private fun collectData() {
        viewModel.categoriesDbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = mutableListOf<Category>()
                snapshot.children.forEach {
//                    val a = it.toString()
                    it.getValue(Category::class.java)?.let { it1 -> items.add(it1) }
//                    Log.i("vocaa", a.toString())
                }
                categoryAdapter.submitList(items)
            }

            override fun onCancelled(error: DatabaseError) {
                showLog(error.message, "FromHome")
            }
        })
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
            changeTargetBtn.setOnClickListener { onChangeLanguageClicked() }
            flagImage.setOnClickListener { navigateToSelectLanguages() }
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
        val action =
            HomeFragmentDirections
                .actionHomeFragmentToAddEditWordFragment(
                    word = null, categoryName = null, categoryId = null
                )
        navigate(action)
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
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun initRecyclerView() {
        binding.recyclerView.apply {
            val divider = DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL,
            )
            setHasFixedSize(true)
            adapter = categoryAdapter
            addItemDecoration(divider)
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
        changePreferenceNativeWithTargetLanguages()
        binding.apply {
            targetLanguage.text =
                AvailableLanguages.getLanguageNameByCode(requireContext(), viewModel.targetLang)
            sourceLanguage.text =
                AvailableLanguages.getLanguageNameByCode(requireContext(), viewModel.sourceLang)
        }
    }

    private fun changePreferenceNativeWithTargetLanguages() {
        val target = viewModel.targetLang
        viewModel.targetLang = viewModel.sourceLang
        viewModel.sourceLang = target
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
        if (!isFabOpen()) return
        val action =
            HomeFragmentDirections.actionHomeFragmentToInCategoryFragment(category)
        navigate(action)
    }

    override fun onPlay(category: Category) {
        if (!isFabOpen()) return
        val action = HomeFragmentDirections.actionHomeFragmentToSelectLearnTypeDialogFragment(
            category
        )
        navigate(action)
    }

    override fun onLongClickCategory(category: Category) {
        showAlertToDeleteCategory(category)
    }

    private fun navigateToSelectLanguages() {
        val nav = HomeFragmentDirections.actionHomeFragmentToSelectLanguageFragment()
        navigate(nav)
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


    private fun isFabOpen(): Boolean {
        if (viewModel.fabMenuOpened) {
            closeFABMenu()
            return false
        }
        return true
    }

    private fun showAlertToDeleteCategory(category: Category) {
        AlertDialog.Builder(requireContext())
            .setMessage("Do you want to delete ${category.mainName}?")
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                viewModel.removeCategory(category)
            }
            .setNegativeButton(getString(R.string.cancel_upper)) { v, _ ->
                v.dismiss()
            }.create().show()
    }

    private fun setInvisibility() {
        binding.floatingActionButtonWord.visibility = View.GONE
        binding.floatingActionButtonCategory.visibility = View.GONE
    }

    override fun onDestroyView() {
        closeFABMenu()
        super.onDestroyView()
    }

}