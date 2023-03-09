package abm.co.domain.exception

import abm.co.domain.base.ExpectedMessage
import androidx.annotation.StringRes
import java.io.IOException

class FirebaseException(val expectedMessage: ExpectedMessage) : IOException()
