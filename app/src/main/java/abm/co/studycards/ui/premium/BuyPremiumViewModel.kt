package abm.co.studycards.ui.premium

import abm.co.studycards.domain.Prefs
import abm.co.studycards.domain.usecases.DoStartPurchaseConnectionUseCase
import abm.co.studycards.domain.usecases.GetBillingClientUseCase
import abm.co.studycards.domain.usecases.GetPurchaseProductsUseCase
import abm.co.studycards.util.base.BaseViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BuyPremiumViewModel @Inject constructor(
    val prefs: Prefs,
    productsUseCase: GetPurchaseProductsUseCase,
    private val billingClientUseCase: GetBillingClientUseCase,
    private val startPurchaseConnectionUseCase: DoStartPurchaseConnectionUseCase
) : BaseViewModel() {
    val skusStateFlow = productsUseCase()
    fun getBillingClient() = billingClientUseCase()
    fun retryConnection() = viewModelScope.launch {
        startPurchaseConnectionUseCase()
    }

//    fun isUserAnonymous() = firebaseAuth.currentUser?.isAnonymous ?: true
//    fun isUserNotVerified() = !(firebaseAuth.currentUser?.isEmailVerified ?: false)
}