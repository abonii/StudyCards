package abm.co.domain.base

import abm.co.domain.base.Failure.FeatureFailure

/**
 * Base Class for handling errors/failures/exceptions.
 * Every feature specific failure should extend [FeatureFailure] class.
 */
sealed class Failure {
    object FailureNetwork : Failure()
    object FailureTimeout : Failure()
    data class FailureAlert(val expectedMessage: ExpectedMessage) : Failure()
    object Ignorable : Failure()
    data class FailureSnackbar(val expectedMessage: ExpectedMessage) : Failure()
    data class DefaultAlert(val expectedMessage: String?) : Failure()

    /** * Extend this class for feature specific failures.*/
    abstract class FeatureFailure : Failure()
}
