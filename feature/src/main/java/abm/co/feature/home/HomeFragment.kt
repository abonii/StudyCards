package abm.co.feature.home

import abm.co.designsystem.base.BaseFragment
import abm.co.designsystem.base.messageContent
import abm.co.designsystem.navigation.extension.navigateSafe
import android.view.View
import androidx.compose.runtime.Composable
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment() {

    companion object {
        val rootViewId = View.generateViewId()
    }

    override val rootViewId: Int get() = Companion.rootViewId

    @Composable
    override fun InitUI(messageContent: messageContent) {
        HomePage(
            showMessage = {
                messageContent(it)
            },
            onNavigateToLanguageSelectPage = {
                // TODO navigation
            },
            openDrawer = {
                // TODO open drawer
            },
            navigateToAllCategory = {},
            navigateToCategory = { category ->
                findNavController().navigateSafe(
                    HomeFragmentDirections.toCategoryDestination(category)
                )
            },
            navigateToGameKinds = {
//                findNavController().navigate(GameDestinations.SwipeGame.route) TODO navigation
            }
        )
    }
}
