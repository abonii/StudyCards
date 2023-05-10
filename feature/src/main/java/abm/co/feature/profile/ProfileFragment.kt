package abm.co.feature.profile

import abm.co.designsystem.base.BaseFragment
import abm.co.designsystem.base.messageContent
import abm.co.designsystem.navigation.extension.navigateSafe
import android.view.View
import androidx.compose.runtime.Composable
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : BaseFragment() {

    companion object {
        private val rootViewId = View.generateViewId()
    }

    override val rootViewId: Int get() = ProfileFragment.rootViewId

    @Composable
    override fun InitUI(messageContent: messageContent) {
        ProfilePage(
            showMessage = messageContent,
            navigateToStorePage = {
                findNavController().navigateSafe(
                    ProfileFragmentDirections.toStoreNavGraph()
                )
            }
        )
    }
}