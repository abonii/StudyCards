package abm.co.designsystem.base

import abm.co.designsystem.message.common.MessageAlertContent
import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.message.common.toMessageContent
import abm.co.designsystem.message.snackbar.MessageSnackbar
import abm.co.designsystem.message.snackbar.showSnackbarWithContent
import abm.co.designsystem.theme.StudyCardsTheme
import android.os.Build
import android.view.View
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import kotlinx.serialization.ExperimentalSerializationApi

typealias messageContent = suspend (messageContent: MessageContent) -> Unit

/**
 * Used to return [ComposeView] for overriding function **onCreateView()**
 */
@OptIn(ExperimentalSerializationApi::class)
fun Fragment.composableView(
    rootViewId: Int = View.generateViewId(),
    content: @Composable (messageContent) -> Unit,
): ComposeView = ComposeView(requireContext()).apply {
    id = rootViewId
    setViewCompositionStrategy(
        ViewCompositionStrategy.DisposeOnLifecycleDestroyed(viewLifecycleOwner)
    )
    setContent {
        val snackbarHostState = remember { SnackbarHostState() }
        var showAlertDialog by remember { mutableStateOf<MessageAlertContent?>(null) }

        ContentConfigurator(this@composableView) {
            StudyCardsTheme {
                val context = LocalContext.current
                content { messageContent ->
                    when (messageContent) {
                        is MessageContent.AlertDialog -> {
                            showAlertDialog = messageContent.toMessageContent(context)
                        }
                        is MessageContent.Snackbar -> {
                            snackbarHostState.showSnackbarWithContent(
                                messageContent.toMessageContent(context)
                            )
                        }
                    }
                }
                MessageSnackbar(snackbarHostState = snackbarHostState)
            }
        }
    }
}


@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S, lambda = 0)
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ContentConfigurator(
    fragment: Fragment?,
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        val backgroundColor = StudyCardsTheme.colors.backgroundPrimary.toArgb()
        SideEffect {
            val window = fragment?.activity?.window
            window?.let {
                window.navigationBarColor = backgroundColor
                WindowCompat.getInsetsController(window, view)
                    .isAppearanceLightNavigationBars = !isDarkTheme
            }
        }
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        content()
    } else {
        CompositionLocalProvider(LocalOverscrollConfiguration provides null) {
            content()
        }
    }
}