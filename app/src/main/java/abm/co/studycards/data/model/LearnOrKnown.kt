package abm.co.studycards.data.model

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
    }
}