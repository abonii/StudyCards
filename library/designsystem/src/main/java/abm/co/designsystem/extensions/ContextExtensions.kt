package abm.co.designsystem.extensions

import android.content.Context
import android.content.Intent

fun Context.shareText(textToShare: String, title: String? = null) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, textToShare)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, title)
    startActivity(shareIntent)
}