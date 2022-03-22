package abm.co.studycards.ui.add_word.dialog.image

import abm.co.studycards.R
import abm.co.studycards.databinding.FragmentImageDialogBinding
import abm.co.studycards.util.Constants
import abm.co.studycards.util.Constants.REQUEST_IMAGE_KEY
import abm.co.studycards.util.base.BaseDialogFragment
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
class ImageDialogFragment : BaseDialogFragment<FragmentImageDialogBinding>(R.layout.fragment_image_dialog) {

    private val viewModel: ImageDialogViewModel by viewModels()

    override fun initUI(savedInstanceState: Bundle?) {
        binding.run {
            url.addTextChangedListener {
                viewModel.urlText = it.toString()
            }
            url.setText(viewModel.urlText)
            urlLayout.setEndIconOnClickListener {
                setImage()
            }
            save.setOnClickListener {
                onDone()
            }
            delete.setOnClickListener {
                binding.url.text = null
                setImage()
            }

        }
    }

    private fun onDone() {
        setFragmentResult(REQUEST_IMAGE_KEY, bundleOf("image_url" to viewModel.urlText))
        dismiss()
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.7).toInt()
        dialog!!.window?.run {
            setBackgroundDrawableResource(R.drawable.round_corner)
            setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    private fun setImage() {
        Glide.with(binding.root)
            .load(viewModel.urlText)
            .centerCrop()
            .into(binding.imageView)
    }

}