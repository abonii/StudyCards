package abm.co.navigation.bottomnavigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable

@Immutable
data class BottomNavigationItem(
    @StringRes val nameRes: Int?,
    val route: String,
    @DrawableRes val iconRes: Int,
    val badgeCount: Int = 0,
)
