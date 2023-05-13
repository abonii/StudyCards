package abm.co.feature.profile.main

import abm.co.core.navigation.NavigationBetweenModules
import abm.co.designsystem.base.BaseFragment
import abm.co.designsystem.base.messageContent
import abm.co.designsystem.extensions.addPaddingOnShownKeyboard
import abm.co.designsystem.navigation.extension.navigateSafe
import android.os.Bundle
import android.view.View
import androidx.compose.runtime.Composable
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : BaseFragment() {

    companion object {
        private val rootViewId = View.generateViewId()
    }

    override val rootViewId: Int get() = Companion.rootViewId

    @Inject
    lateinit var navigationBetweenModules: NavigationBetweenModules

    @Composable
    override fun InitUI(messageContent: messageContent) {
        ProfilePage(
            showMessage = messageContent,
            navigateToStorePage = {
                findNavController().navigateSafe(
                    ProfileFragmentDirections.toStoreNavGraph()
                )
            },
            navigateToChangePasswordPage = {
                findNavController().navigateSafe(
                    ProfileFragmentDirections.toChangePasswordDestination()
                )
            },
            navigateToAuthorization = {
                navigationBetweenModules.navigateFromMainToAuthorization(requireActivity())
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.addPaddingOnShownKeyboard(view)
    }
}