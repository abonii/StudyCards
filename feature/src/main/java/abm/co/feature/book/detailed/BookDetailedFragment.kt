package abm.co.feature.book.detailed

import abm.co.designsystem.base.BaseFragment
import abm.co.designsystem.base.messageContent
import android.view.View
import androidx.compose.runtime.Composable
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookDetailedFragment : BaseFragment() {

    companion object {
        private val rootViewId = View.generateViewId()
    }

    override val rootViewId: Int get() = Companion.rootViewId

    @Composable
    override fun InitUI(messageContent: messageContent) {
        BookDetailedPage(
            showMessage = messageContent,
            navigateBack = {
                findNavController().navigateUp()
            },
            navigateToBookReader = {

            }
        )
    }
}
