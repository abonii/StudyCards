package abm.co.designsystem.navigation.extension

import androidx.navigation.NavBackStackEntry

fun <T> NavBackStackEntry.getParcelableData(key: String): T? {
    return arguments?.getParcelableData(key) as? T
}
