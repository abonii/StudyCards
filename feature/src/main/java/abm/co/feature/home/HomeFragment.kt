package abm.co.feature.home

import abm.co.designsystem.base.BaseFragment
import abm.co.designsystem.base.messageContent
import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.message.snackbar.MessageType
import abm.co.designsystem.navigation.extension.navigateSafe
import abm.co.feature.R
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
                messageContent(
                    MessageContent.Snackbar.MessageContentRes(
                        titleRes = abm.co.designsystem.R.string.Messages_working,
                        subtitleRes = R.string.Message_inFuture,
                        type = MessageType.Info
                    )
                )
                // TODO open drawer
            },
            navigateToAllCategory = {/*TODO implement to all categories*/ },
            navigateToCategory = { category ->
                findNavController().navigateSafe(
                    HomeFragmentDirections.toCategoryDestination(category)
                )
            },
            navigateToGameKinds = { category ->
                findNavController().navigateSafe(
                    HomeFragmentDirections.toGamePickerNavGraph(
                        category = category
                    )
                )
            }
        )
    }
}
