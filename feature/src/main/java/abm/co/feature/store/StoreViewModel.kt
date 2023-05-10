package abm.co.feature.store

import abm.co.domain.repository.StoreRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoreViewModel @Inject constructor(
    private val storeRepository: StoreRepository
) : ViewModel() {

    val skusStateFlow = storeRepository.skusStateFlow
    fun getBillingClient() = storeRepository.billingClient
    fun retryConnection() = viewModelScope.launch {
        storeRepository.startConnection()
    }
}
