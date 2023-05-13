package abm.co.designsystem.message.common

import abm.co.designsystem.R
import abm.co.designsystem.message.snackbar.MessageType
import abm.co.domain.base.ExpectedMessage
import abm.co.domain.base.Failure

fun Failure.toMessageContent(): MessageContent? =
    when (this) {
        is Failure.DefaultAlert -> {
            expectedMessage?.let {
                MessageContent.AlertDialog.MessageContentTitleRes(
                    titleRes = R.string.InternalErrorAlert_title,
                    subtitle = it
                )
            }
        }
        is Failure.FailureAlert -> {
            val messageContent = when (val expectedMessage = expectedMessage) {
                is ExpectedMessage.Res -> {
                    MessageContent.AlertDialog.MessageContentRes(
                        titleRes = R.string.Messages_error,
                        subtitleRes = expectedMessage.value
                    )
                }
                is ExpectedMessage.String -> {
                    MessageContent.AlertDialog.MessageContentTitleRes(
                        titleRes = R.string.Messages_error,
                        subtitle = expectedMessage.value
                    )
                }
            }
            messageContent
        }
        Failure.FailureNetwork -> {
            MessageContent.AlertDialog.MessageContentRes(
                titleRes = R.string.Error_InternetConnection_title,
                subtitleRes = R.string.Error_InternetConnection_description
            )
        }
        is Failure.FailureSnackbar -> {
            val messageContent = when (val expectedMessage = expectedMessage) {
                is ExpectedMessage.Res -> {
                    MessageContent.Snackbar.MessageContentRes(
                        titleRes = R.string.Messages_error,
                        subtitleRes = expectedMessage.value,
                        type = MessageType.Error
                    )
                }
                is ExpectedMessage.String -> {
                    MessageContent.Snackbar.MessageContentTitleRes(
                        titleRes = R.string.Messages_error,
                        subtitle = expectedMessage.value,
                        type = MessageType.Error
                    )
                }
            }
            messageContent
        }
        Failure.FailureTimeout -> {
            MessageContent.AlertDialog.MessageContentRes(
                titleRes = R.string.Error_timeOut,
                subtitleRes = R.string.Messages_error
            )
        }
        else -> null
    }