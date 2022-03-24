package abm.co.studycards.ui.games.confirmend

import abm.co.studycards.databinding.FragmentConfirmEndBinding
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
class ConfirmEndFragment : DialogFragment() {

    private lateinit var binding: FragmentConfirmEndBinding
    private val viewModel: ConfirmEndViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =  FragmentConfirmEndBinding.inflate(inflater, container, false)
        dialog?.setCancelable(false)
        dialog?.setCanceledOnTouchOutside(false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ok.setOnClickListener {
            val action = ConfirmEndFragmentDirections.actionConfirmEndFragmentToHomeFragment()
            navigate(action)
        }
        binding.no.setOnClickListener {
            dismiss()
        }
        when(viewModel.confirmType){
            ConfirmText.FINISH_REPEAT -> {
                binding.secondText.text = "You finished repeating"
            }
            ConfirmText.FINISH_LEARN -> {
                binding.secondText.text = "You finished learning"
            }
            ConfirmText.FINISH_REVIEW -> {
                binding.secondText.text = "You finished reviewing"
            }
            ConfirmText.ON_EXIT -> {
                binding.text.text = "Do you want to exit?"
                binding.no.isVisible = true
                dialog?.setCancelable(true)
                dialog?.setCanceledOnTouchOutside(true)
            }
            ConfirmText.FINISH_GUESS -> {
                binding.secondText.text = "You finished guessing"
            }
            ConfirmText.FINISH_PAIR -> {
                binding.secondText.text = "You finished pairing"
            }
            ConfirmText.DELETE_CATEGORY -> {
                binding.image.isVisible = false
                binding.no.isVisible = true
                binding.secondText.isVisible = false
                dialog?.setCancelable(true)
                dialog?.setCanceledOnTouchOutside(true)
                binding.text.text = "Do you want to delete the category?"
            }
            null -> TODO()
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
    ON_EXIT,
    DELETE_CATEGORY
}