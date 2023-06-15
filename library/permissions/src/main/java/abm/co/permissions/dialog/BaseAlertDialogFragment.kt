package abm.co.permissions.dialog

import abm.co.designsystem.base.composableView
import abm.co.domain.functional.safeLet
import abm.co.permissions.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.MaterialTheme
import androidx.fragment.app.DialogFragment

class BaseAlertDialogFragment : DialogFragment() {

    companion object {
        private val rootViewId = View.generateViewId()

        fun newInstance(
            title: String,
            description: String,
            positiveButtonClickListener: () -> Unit
        ): BaseAlertDialogFragment {
            val fragment = BaseAlertDialogFragment()
            fragment.title = title
            fragment.description = description
            fragment.positiveButtonClickListener = positiveButtonClickListener
            return fragment
        }
    }

    private var title: String? = null
    private var description: String? = null
    private var positiveButtonClickListener: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = composableView(rootViewId = rootViewId) {
        safeLet(title, description) { title, description ->
            MaterialTheme {
                BaseAlert(
                    title = title,
                    subtitle = description,
                    negativeButtonText = getString(R.string.Alert_cancel),
                    positiveButtonText = getString(R.string.Alert_allow),
                    onNegativeClick = { dismiss() },
                    onPositiveClick = {
                        positiveButtonClickListener?.invoke()
                        dismiss()
                    },
                    onDismiss = { dismiss() }
                )
            }
        }
    }
}
