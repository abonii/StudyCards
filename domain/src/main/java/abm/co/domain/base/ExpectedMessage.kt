package abm.co.domain.base

import androidx.annotation.StringRes

sealed interface ExpectedMessage {
    data class Res(@StringRes val value: Int) : ExpectedMessage
    data class String(val value: kotlin.String) : ExpectedMessage
}
