package abm.co.studycards.data.model

import abm.co.studycards.R
import android.content.Context
import androidx.core.content.ContextCompat

enum class LearnOrKnown {
    UNDEFINED,
    UNKNOWN,
    UNCERTAIN,
    KNOWN;

    fun getType(): String {
        return when (this) {
            UNKNOWN -> {
                "unknown"
            }
            KNOWN -> {
                "known"
            }
            UNCERTAIN -> {
                "uncertain"
            }
            UNDEFINED -> {
                "undefined"
            }
        }
    }

    fun getName(context: Context): String {
        return when (this) {
            KNOWN -> context.getString(R.string.upper_know)
            UNCERTAIN -> context.getString(R.string.uncertain)
            else -> context.getString(R.string.unknown)
        }
    }

    fun getColor(context: Context): Int {
        return when (this) {
            KNOWN -> ContextCompat.getColor(context, R.color.swiping_know)
            UNCERTAIN -> ContextCompat.getColor(context, R.color.swiping_doubted)
            else -> ContextCompat.getColor(context, R.color.swiping_n_know)
        }
    }

    companion object {
        fun getType(type: String): LearnOrKnown {
            return when (type) {
                "unknown" -> {
                    UNKNOWN
                }
                "known" -> {
                    KNOWN
                }
                "undefined" -> {
                    UNDEFINED
                }
                else -> {
                    UNCERTAIN
                }
            }
        }

        fun getType(num: Int): LearnOrKnown {
            return when (num) {
                0 -> UNKNOWN
                1 -> UNCERTAIN
                else -> KNOWN
            }
        }
    }
}