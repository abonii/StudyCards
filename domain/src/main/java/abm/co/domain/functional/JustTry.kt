package abm.co.domain.functional

inline fun justTry(block: () -> Unit) {
    try {
        block()
    } catch (e: Exception) {
        // catch any exception but ignore it
    }
}
