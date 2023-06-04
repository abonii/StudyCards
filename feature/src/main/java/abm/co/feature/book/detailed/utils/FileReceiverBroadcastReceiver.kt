package abm.co.feature.book.detailed.utils

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import java.io.File

interface DownloadedFileReceivedInterface {
    fun onFileReceived(file: File?)
    fun onError()
}

class FileReceiverBroadcastReceiver : BroadcastReceiver() {

    private var fileReceiver: DownloadedFileReceivedInterface? = null

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action

        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == action) {
            val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val downloadManager =
                context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

            // Query the download manager for the downloaded file
            val query = DownloadManager.Query().setFilterById(downloadId)
            val cursor = downloadManager.query(query)

            if (cursor.moveToFirst()) {
                val statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                val fileIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
                if (statusIndex < 0 || fileIndex < 0) return
                val status = cursor.getInt(statusIndex)
                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    val fileUri = cursor.getString(fileIndex)
                    val downloadedFile = Uri.parse(fileUri).path?.let { File(it) }
                    fileReceiver?.onFileReceived(downloadedFile)
                } else {
                    fileReceiver?.onError()
                }
            }

            cursor.close()
        }
    }

    fun setListener(receiver: DownloadedFileReceivedInterface?) {
        this.fileReceiver = receiver
    }
}