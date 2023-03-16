package abm.co.studycards.ui

import abm.co.designsystem.message.alert.MessageAlertDialog
import abm.co.designsystem.message.common.MessageAlertContent
import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.message.common.toMessageContent
import abm.co.designsystem.message.snackbar.MessageSnackbar
import abm.co.designsystem.message.snackbar.showSnackbarWithContent
import abm.co.designsystem.theme.StudyCardsTheme
import abm.co.navigation.graph.root.RootNavHost
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.ExperimentalSerializationApi


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalAnimationApi::class, ExperimentalSerializationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                viewModel.state.value.isSplashScreenVisible
            }
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
        setContent {
            StudyCardsTheme {
                val snackbarHostState = remember { SnackbarHostState() }
                var showAlertDialog by remember { mutableStateOf<MessageAlertContent?>(null) }
                val state by viewModel.state.collectAsState()
                state.startDestination?.let {
                    RootNavHost(
                        navController = rememberAnimatedNavController(),
                        startDestination = it,
                        showMessage = { messageContent ->
                            when (messageContent) {
                                is MessageContent.AlertDialog -> {
                                    showAlertDialog = messageContent.toMessageContent(this)
                                }
                                is MessageContent.Snackbar -> {
                                    snackbarHostState.showSnackbarWithContent(
                                        messageContent.toMessageContent(this)
                                    )
                                }
                            }
                        }
                    )
                }
                MessageAlertDialog(
                    showAlertDialog = showAlertDialog,
                    onDismiss = { showAlertDialog = null },
                )
                MessageSnackbar(snackbarHostState = snackbarHostState)
            }

        }
    }
}
