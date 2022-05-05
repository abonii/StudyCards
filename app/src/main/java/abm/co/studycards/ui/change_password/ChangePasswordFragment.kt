package abm.co.studycards.ui.change_password

import abm.co.studycards.MainActivity
import abm.co.studycards.R
import abm.co.studycards.databinding.FragmentChangePasswordBinding
import abm.co.studycards.util.base.BaseBindingFragment
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangePasswordFragment :
    BaseBindingFragment<FragmentChangePasswordBinding>(R.layout.fragment_change_password) {

    private val viewModel: ChangePasswordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launchWhenStarted {
            viewModel.toast.collect {
                toast(it)
            }
        }
    }

    override fun initUI(savedInstanceState: Bundle?) {
        binding.run {
            viewmodel = viewModel
            (activity as MainActivity).setToolbar(toolbar, findNavController())
        }
    }

}