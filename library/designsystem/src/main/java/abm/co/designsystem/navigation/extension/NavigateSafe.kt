package abm.co.designsystem.navigation.extension

import abm.co.designsystem.functional.justTry
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions

fun NavController.navigateSafe(directions: NavDirections) {
    justTry {
        currentDestination?.getAction(directions.actionId)?.run {
            navigate(directions)
        }
    }
}

fun NavController.navigateSafe(
    @IdRes destinationRes: Int,
    args: Bundle? = null,
    navOptions: NavOptions? = null
) {
    justTry { navigate(destinationRes, args, navOptions) }
}
