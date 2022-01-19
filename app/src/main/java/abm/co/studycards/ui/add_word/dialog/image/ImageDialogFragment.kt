package abm.co.studycards.ui.add_word.dialog.image

import abm.co.studycards.R
import abm.co.studycards.databinding.FragmentImageDialogBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ImageDialogFragment : DialogFragment() {

    private val viewModel: ImageDialogViewModel by viewModels()
    private lateinit var binding: FragmentImageDialogBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog!!.window?.setBackgroundDrawableResource(R.drawable.round_corner)

        binding = FragmentImageDialogBinding.inflate(inflater, container, false)

        binding.apply {
            url.addTextChangedListener {
                viewModel.urlText = it.toString()
            }
            url.setText(viewModel.urlText)
            urlLayout.setEndIconOnClickListener {
                it.isEnabled = false
                setImage()
                it.isEnabled = true
            }
            save.setOnClickListener {
                onDone()
            }
            delete.setOnClickListener {
                binding.url.text = null
                setImage()
            }

        }
        return binding.root
    }

    private fun onDone() {
        val result = viewModel.urlText
        setFragmentResult("requestImage", bundleOf("imageUrl" to result))
        dismiss()
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.7).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun setImage() {
        Glide
            .with(requireActivity())
            .load(viewModel.urlText)
            .centerCrop()
            .into(binding.imageView)

    }

}