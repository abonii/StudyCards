package abm.co.feature.book.library

import abm.co.designsystem.base.BaseFragment
import abm.co.designsystem.base.messageContent
import abm.co.designsystem.navigation.extension.navigateSafe
import android.view.View
import androidx.compose.runtime.Composable
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LibraryFragment : BaseFragment() {

    companion object {
        private val rootViewId = View.generateViewId()
    }

    override val rootViewId: Int get() = Companion.rootViewId

    @Composable
    override fun InitUI(messageContent: messageContent) {
        LibraryPage(
            showMessage = messageContent,
            navigateToBookDetailed = {
                findNavController().navigateSafe(
                    LibraryFragmentDirections.toBookDetailedDestination(book = it)
                )
            }
        )
    }
}
