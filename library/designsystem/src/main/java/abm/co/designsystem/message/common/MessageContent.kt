package abm.co.designsystem.message.common

import abm.co.designsystem.message.snackbar.MessageType
import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import kotlinx.serialization.Serializable

@Serializable
class MessageSnackbarContent(
    val title: String,
    val subtitle: String,
    val type: MessageType
)

class MessageAlertContent(
    val title: String,
    val subtitle: String
)
@Immutable
sealed interface MessageContent {
    sealed interface Snackbar: MessageContent {
        data class MessageContentRes(
            @StringRes val titleRes: Int,
            @StringRes val subtitleRes: Int,
            val type: MessageType
        ) : Snackbar

        data class MessageContentTitleRes(
            @StringRes val titleRes: Int,
            val subtitle: String,
            val type: MessageType
        ) : Snackbar
    }

    sealed interface AlertDialog: MessageContent {
        data class MessageContentRes(
            @StringRes val titleRes: Int,
            @StringRes val subtitleRes: Int,
        ) : AlertDialog

        data class MessageContentTitleRes(
            @StringRes val titleRes: Int,
            val subtitle: String,
        ) : AlertDialog
    }
}

fun MessageContent.Snackbar.toMessageContent(context: Context): MessageSnackbarContent = when (this) {
    is MessageContent.Snackbar.MessageContentRes -> {
        MessageSnackbarContent(
            title = context.getString(titleRes),
            subtitle = context.getString(subtitleRes),
            type = type
        )
    }
    is MessageContent.Snackbar.MessageContentTitleRes -> {
        MessageSnackbarContent(
            title = context.getString(titleRes),
            subtitle = subtitle,
            type = type
        )
    }
}

fun MessageContent.AlertDialog.toMessageContent(context: Context): MessageAlertContent = when (this) {
    is MessageContent.AlertDialog.MessageContentRes -> {
        MessageAlertContent(
            title = context.getString(titleRes),
            subtitle = context.getString(subtitleRes)
        )
    }
    is MessageContent.AlertDialog.MessageContentTitleRes -> {
        MessageAlertContent(
            title = context.getString(titleRes),
            subtitle = subtitle
        )
    }
}
