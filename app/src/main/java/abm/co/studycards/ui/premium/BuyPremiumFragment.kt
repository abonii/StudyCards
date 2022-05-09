package abm.co.studycards.ui.premium

import abm.co.studycards.MainActivity
import abm.co.studycards.R
import abm.co.studycards.databinding.FragmentBuyPremiumBinding
import abm.co.studycards.util.base.BaseBindingFragment
import abm.co.studycards.util.launchAndRepeatWithViewLifecycle
import android.content.res.Resources
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.SkuDetails
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class BuyPremiumFragment :
    BaseBindingFragment<FragmentBuyPremiumBinding>(R.layout.fragment_buy_premium) {

    private val viewModel: BuyPremiumViewModel by viewModels()
    private var productsAdapter: ProductAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launchWhenStarted {
            viewModel.toast.collectLatest {
                toast(it)
            }
        }
    }

    override fun initUI(savedInstanceState: Bundle?) {
        (activity as MainActivity).setToolbar(binding.toolbar)
        collectData()
        initRV()
    }

    private fun collectData() {
        launchAndRepeatWithViewLifecycle(Lifecycle.State.STARTED) {
            viewModel.skusStateFlow.collectLatest { subscriptions ->
                if (subscriptions.isNotEmpty()) {
                    setProductsList(subscriptions)
                }
            }
        }
    }

    private fun getNewWidthOfItem(products: Int): Int {
        return ((Resources.getSystem().displayMetrics.widthPixels
                - (products + 2) * resources
            .getDimension(R.dimen.activity_horizontal_margin)) / if (products == 1) 2 else products).toInt()
    }

    private fun initRV() {
        productsAdapter = ProductAdapter { skuDetails ->
            when {
                viewModel.isUserNotVerified() -> {
                    toast(getString(R.string.verify_account))
                }
                viewModel.isUserAnonymous() -> {
                    toast(getString(R.string.link_to_account))
                }
                else -> {
                    launchPurchaseFlow(skuDetails)
                }
            }
        }
        binding.recyclerViewProducts.adapter = productsAdapter
    }

    private fun setProductsList(products: List<SkuDetails>) {
        productsAdapter?.newWidth = getNewWidthOfItem(products.size)
        productsAdapter?.products = products
    }

    private fun launchPurchaseFlow(product: SkuDetails) {
        val flowParams = BillingFlowParams.newBuilder()
            .setSkuDetails(product)
            .build()
        val billingClient = viewModel.getBillingClient()
        when (billingClient.launchBillingFlow(requireActivity(), flowParams).responseCode) {
            BillingClient.BillingResponseCode.SERVICE_DISCONNECTED,
            BillingClient.BillingResponseCode.SERVICE_TIMEOUT -> {
                viewModel.retryConnection()
            }
        }
    }
}