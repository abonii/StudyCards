package abm.co.designsystem.base

import androidx.compose.runtime.Immutable

@JvmInline
@Immutable
value class WrapperList<T>(private val data: List<T>) : List<T> by data

fun <T> List<T>.toWrapperList() = WrapperList(this)
