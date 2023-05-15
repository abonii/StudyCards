package abm.co.feature.card.wordinfo

import abm.co.designsystem.base.BaseBottomSheetDialogFragment
import abm.co.designsystem.base.messageContent
import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class WordInfoFragment : BaseBottomSheetDialogFragment() {

    companion object {
        private val rootViewId = View.generateViewId()
        const val WORD_INFO_CLOSED = "WORD_INFO_CLOSED"
    }

    override val rootViewId: Int get() = WordInfoFragment.rootViewId

    private val viewModel by viewModels<WordInfoViewModel>()

    var behavior: BottomSheetBehavior<*>? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        bottomSheetDialog.setOnShowListener {
            bottomSheetDialog
                .findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                ?.let { it1 ->
                    behavior = BottomSheetBehavior.from(it1).apply {
                        skipCollapsed = true
                        state = BottomSheetBehavior.STATE_EXPANDED
                    }
                }
        }
        return bottomSheetDialog
    }

    @Composable
    override fun InitUI(messageContent: messageContent) {
        val scrollState = rememberScrollState()
        LaunchedEffect(Unit) {
            snapshotFlow { scrollState.canScrollBackward }
                .collectLatest {
                    behavior?.isDraggable = !it
                }
        }
        WordInfoPage(
            navigateBack = {
                findNavController().navigateUp()
            },
            showMessage = messageContent,
            scrollState = scrollState,
            viewModel = viewModel
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        setFragmentResult(
            requestKey = WORD_INFO_CLOSED,
            result = bundleOf(
                "oxford_checked_items_id" to viewModel.getCheckedItemsID(),
                "oxford_response" to viewModel.getOxfordResponse()
            )
        )
    }
}