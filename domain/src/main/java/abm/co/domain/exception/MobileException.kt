package abm.co.domain.exception

import java.io.IOException

@JvmInline
value class ExceptionCode(val value: String)

class MobileException(val exceptionCode: ExceptionCode) : IOException()
