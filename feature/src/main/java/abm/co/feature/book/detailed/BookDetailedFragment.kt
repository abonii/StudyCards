package abm.co.feature.book.detailed

import abm.co.designsystem.base.BaseFragment
import abm.co.designsystem.base.messageContent
import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.message.snackbar.MessageType
import abm.co.designsystem.navigation.extension.navigateSafe
import abm.co.feature.book.utils.DownloadedFileReceivedInterface
import abm.co.feature.book.utils.FileReceiverBroadcastReceiver
import android.Manifest
import android.app.DownloadManager
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat
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

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                viewModel.downloadOrOpenBook()
            } else {
                viewModel.enableStoragePermissionInApplicationSettings()
            }
        }

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
            openBookIfHasPermission = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    viewModel.downloadOrOpenBook()
                } else {
                    when {
                        ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED -> {
                            viewModel.downloadOrOpenBook()
                        }

                        shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) -> {
                            requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            messageContent(
                                MessageContent.Snackbar.MessageContentTitleRes(
                                    titleRes = abm.co.designsystem.R.string.Messages_error,
                                    subtitle = "Permission is necessary to save books",
                                    type = MessageType.Info
                                )
                            )
                        }

                        else -> {
                            requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        }
                    }
                }
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
