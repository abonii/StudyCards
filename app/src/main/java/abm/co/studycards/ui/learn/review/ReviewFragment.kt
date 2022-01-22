package abm.co.studycards.ui.learn.review

import abm.co.studycards.MainActivity
import abm.co.studycards.R
import abm.co.studycards.databinding.FragmentReviewBinding
import abm.co.studycards.helpers.CenterZoomLayoutManager
import abm.co.studycards.helpers.SnapHelperOneByOne
import abm.co.studycards.ui.learn.confirmend.ConfirmText
import abm.co.studycards.util.base.BaseBindingFragment
import abm.co.studycards.util.navigate
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReviewFragment : BaseBindingFragment<FragmentReviewBinding>(R.layout.fragment_review) {

    private val viewModel: ReviewViewModel by viewModels()
    private lateinit var snapHelper: SnapHelper
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var reviewAdapter: ReviewAdapter

    override fun initViews(savedInstanceState: Bundle?) {
        (activity as MainActivity).setToolbar(binding.toolbar, findNavController())
        binding.toolbar.setNavigationIcon(R.drawable.ic_back)
        reviewAdapter = ReviewAdapter().apply {
            words = if (viewModel.isRepeat) viewModel.getFiveWords().toMutableList()
            else viewModel.words.toMutableList()
        }
        mLayoutManager = CenterZoomLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL, false
        )
        snapHelper = SnapHelperOneByOne().apply {
            attachToRecyclerView(binding.recyclerView)
        }
        binding.recyclerView.apply {
            layoutManager = mLayoutManager
            adapter = reviewAdapter
            setHasFixedSize(true)
            addOnScrollListener(onScroll())
        }
    }

    private fun onScroll(): RecyclerView.OnScrollListener =
        object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCREEN_STATE_OFF) {
                    if (mLayoutManager.itemCount - 1 == mLayoutManager.findLastCompletelyVisibleItemPosition()) {
                        onFinish()
                    }
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (mLayoutManager.itemCount - 2 == mLayoutManager.findFirstVisibleItemPosition()
                    && dx > 15
                )
                    onFinish()
            }
        }

    private fun onFinish() {
        val action = if (viewModel.isRepeat) {
            ReviewFragmentDirections
                .actionReviewFragmentToMatchingPairsFragment(
                    true,
                    viewModel.words
                )
        } else {
            ReviewFragmentDirections
                .actionGlobalConfirmEndFragment(ConfirmText.FINISH_REVIEW)
        }
        navigate(action)
    }
}
//const val TAG = "reviewFragment"

