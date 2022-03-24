package abm.co.studycards.ui.games.confirmend

import abm.co.studycards.R
import abm.co.studycards.databinding.FragmentConfirmEndBinding
import abm.co.studycards.util.base.BaseDialogFragment
import abm.co.studycards.util.navigate
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize

@AndroidEntryPoint
class ConfirmEndFragment : BaseDialogFragment<FragmentConfirmEndBinding>(R.layout.fragment_confirm_end) {

    private val viewModel: ConfirmEndViewModel by viewModels()

    override fun initUI(savedInstanceState: Bundle?) {
        dialog?.setCancelable(false)
        dialog?.setCanceledOnTouchOutside(false)
        binding.ok.setOnClickListener {
            val action = ConfirmEndFragmentDirections.actionConfirmEndFragmentToHomeFragment()
            navigate(action)
        }
        binding.no.setOnClickListener {
            dismiss()
        }
        when(viewModel.confirmType){
            ConfirmText.FINISH_REPEAT -> {
                binding.secondText.text = getString(R.string.finished_repeating)
            }
            ConfirmText.FINISH_LEARN -> {
                binding.secondText.text = getString(R.string.finished_learning)
            }
            ConfirmText.FINISH_REVIEW -> {
                binding.secondText.text = getString(R.string.finished_review)
            }
            ConfirmText.FINISH_GUESS -> {
                binding.secondText.text = getString(R.string.finished_guessing)
            }
            ConfirmText.FINISH_PAIR -> {
                binding.secondText.text = getString(R.string.finished_pairing)
            }
            else -> {
                binding.text.text = getString(R.string.do_u_want_to_delete)
                binding.no.isVisible = true
                dialog?.setCancelable(true)
                dialog?.setCanceledOnTouchOutside(true)
            }
        }
    }

}
@Parcelize
enum class ConfirmText: Parcelable {
    FINISH_REPEAT,
    FINISH_LEARN,
    FINISH_REVIEW,
    FINISH_GUESS,
    FINISH_PAIR,
    ON_EXIT
}