package abm.co.feature.book.detailed

import abm.co.designsystem.base.BaseFragment
import abm.co.designsystem.base.messageContent
import abm.co.designsystem.navigation.extension.navigateSafe
import abm.co.feature.book.utils.DownloadedFileReceivedInterface
import abm.co.feature.book.utils.FileReceiverBroadcastReceiver
import android.app.DownloadManager
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.compose.runtime.Composable
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class BookDetailedFragment : BaseFragment() {

    companion object {
        private val rootViewId = View.generateViewId()
    }

    override val rootViewId: Int get() = Companion.rootViewId

    private val viewModel by viewModels<BookDetailedViewModel>()

    private var fileBroadcastReceiver: FileReceiverBroadcastReceiver? = null

    @Composable
    override fun InitUI(messageContent: messageContent) {
        BookDetailedPage(
            showMessage = messageContent,
            navigateBack = {
                findNavController().navigateUp()
            },
            navigateToBookReader = { book, url ->
                findNavController().navigateSafe(
                    BookDetailedFragmentDirections.toBookReaderDestination(
                        book = book,
                        bookUrl = url
                    )
                )
            },
            viewModel = viewModel
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fileBroadcastReceiver = FileReceiverBroadcastReceiver()
        fileBroadcastReceiver?.setListener(object : DownloadedFileReceivedInterface {
            override fun onFileReceived(file: File?) {
                viewModel.onFileReceived(file)
            }

            override fun onError() {
                viewModel.errorWhileGettingFile()
            }
        })
        activity?.applicationContext?.registerReceiver(
            fileBroadcastReceiver,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )
    }
}
