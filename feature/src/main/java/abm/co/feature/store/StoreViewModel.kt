package abm.co.feature.store

import abm.co.domain.repository.AuthorizationRepository
import abm.co.domain.repository.ConfigRepository
import abm.co.domain.repository.ServerRepository
import abm.co.domain.repository.StoreRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoreViewModel @Inject constructor(
    private val storeRepository: StoreRepository,
    private val authorizationRepository: AuthorizationRepository,
    private val configRepository: ConfigRepository,
    serverRepository: ServerRepository
) : ViewModel() {

    private val availableTranslateCountFlow =
        serverRepository.getUser.map { it.asRight?.b?.translateCounts ?: 0L }

    val skusStateFlow = storeRepository.skusStateFlow
    fun getBillingClient() = storeRepository.billingClient
    fun retryConnection() = viewModelScope.launch {
        storeRepository.startConnection()
    }

    suspend fun canAddTranslateCount(): Boolean {
        val canAdd =
            (availableTranslateCountFlow.firstOrNull() ?: 0L) < (configRepository.getConfig()
                .firstOrNull()?.asRight?.b?.translateCount ?: 0)
        if (canAdd) {
            authorizationRepository.addUserTranslationCount()
        }
        return canAdd
    }
}
