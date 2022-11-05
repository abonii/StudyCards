package abm.co.studycards.domain.usecases

import abm.co.studycards.domain.repository.PricingRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class GetBillingClientUseCase @Inject constructor(
    private val pricingRepository: PricingRepository
) {
    operator fun invoke() = pricingRepository.billingClient
}