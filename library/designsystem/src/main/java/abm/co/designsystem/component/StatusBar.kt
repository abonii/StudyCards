package abm.co.designsystem.component

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.Window
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun SetStatusBarColor(
    color: Color = Color.Transparent,
    iconsColorsDark: Boolean = !isSystemInDarkTheme()
) {
    val context = LocalContext.current
    val systemUiController = rememberSystemUiController(context.findWindow())
    LaunchedEffect(systemUiController) {
        systemUiController.setStatusBarColor(
            color = color,
            darkIcons = iconsColorsDark
        )
    }
}

private tailrec fun Context.findWindow(): Window? =
    when (this) {
        is Activity -> window
        is ContextWrapper -> baseContext.findWindow()
        else -> null
    }
