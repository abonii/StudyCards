package abm.co.domain.exception

import abm.co.domain.base.ExpectedMessage
import java.io.IOException

class ClientSideException(val expectedMessage: ExpectedMessage) : IOException()
