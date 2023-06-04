package abm.co.feature.book.detailed.utils

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment

fun downloadEpubFile(
    bookTitle: String,
    context: Context,
    url: String
) {
    val request = DownloadManager.Request(Uri.parse(url))
        .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
        .setAllowedOverRoaming(false)
        .setTitle("Downloading")
        .setDescription("Downloading $bookTitle file")
        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "$bookTitle.epub")

    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    downloadManager.enqueue(request)
}
