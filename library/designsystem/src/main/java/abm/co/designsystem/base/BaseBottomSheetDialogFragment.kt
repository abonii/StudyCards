package abm.co.designsystem.base

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BaseBottomSheetDialogFragment : BottomSheetDialogFragment() {

    abstract val rootViewId: Int

    @Composable
    abstract fun InitUI(
        messageContent: messageContent
    )

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        bottomSheetDialog.setOnShowListener {
            bottomSheetDialog
                .findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                ?.let { it1 ->
                    BottomSheetBehavior.from(it1).apply {
                        skipCollapsed = true
                        state = BottomSheetBehavior.STATE_EXPANDED
                    }
                }
        }
        return bottomSheetDialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = composableView(
        rootViewId = rootViewId,
        content = { composableInFragment ->
            InitUI(messageContent = composableInFragment)
        }
    )
}
