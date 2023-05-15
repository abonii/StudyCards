package abm.co.domain.repository

import abm.co.domain.base.Either
import abm.co.domain.base.Failure
import abm.co.domain.model.config.Config
import kotlinx.coroutines.flow.Flow

interface ConfigRepository {
    fun getConfig(): Flow<Either<Failure, Config>>
}
