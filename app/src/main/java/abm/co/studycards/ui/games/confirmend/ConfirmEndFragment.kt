package abm.co.studycards.ui.games.confirmend

import abm.co.studycards.R
import abm.co.studycards.data.model.ConfirmText
import abm.co.studycards.databinding.FragmentConfirmEndBinding
import abm.co.studycards.util.base.BaseDialogFragment
import abm.co.studycards.util.navigate
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ConfirmEndFragment :
    BaseDialogFragment<FragmentConfirmEndBinding>(R.layout.fragment_confirm_end) {

    private val viewModel: ConfirmEndViewModel by viewModels()

    override fun initUI(savedInstanceState: Bundle?) {
        cancelableEnabled()
        binding.run {
            ok.setOnClickListener {
                onOkClicked()
            }
            okBtn.setOnClickListener {
                onOkClicked()
            }
            no.setOnClickListener {
                dismiss()
            }
            image.isVisible = viewModel.confirmType != ConfirmText.ON_EXIT
            okBtn.isVisible = viewModel.confirmType != ConfirmText.ON_EXIT
            ok.isVisible = viewModel.confirmType == ConfirmText.ON_EXIT
            no.isVisible = viewModel.confirmType == ConfirmText.ON_EXIT
            when (viewModel.confirmType) {
                ConfirmText.FINISH_REPEAT -> {
//                secondText.text = getString(R.string.finished_repeating)
                }
                ConfirmText.FINISH_LEARN -> {
//                secondText.text = getString(R.string.finished_learning)
                }
                ConfirmText.FINISH_REVIEW -> {
//                secondText.text = getString(R.string.finished_review)
                }
                ConfirmText.FINISH_GUESS -> {
//                secondText.text = getString(R.string.finished_guessing)
                }
                ConfirmText.FINISH_PAIR -> {
//                secondText.text = getString(R.string.finished_pairing)
                }
                else -> {
                    text.text = getString(R.string.do_u_want_to_exit)
                    no.isVisible = true
                }
            }
        }
    }

    private fun cancelableEnabled() {
        val isExitMode = viewModel.confirmType == ConfirmText.ON_EXIT
        dialog?.setCancelable(isExitMode)
        dialog?.setCanceledOnTouchOutside(isExitMode)
    }

    private fun onOkClicked() {
        val action = ConfirmEndFragmentDirections.actionConfirmEndFragmentToHomeFragment()
        navigate(action)
    }

}