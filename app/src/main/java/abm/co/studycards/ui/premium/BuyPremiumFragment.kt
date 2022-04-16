package abm.co.studycards.ui.premium

import abm.co.studycards.R
import abm.co.studycards.databinding.FragmentBuyPremiumBinding
import abm.co.studycards.util.base.BaseBindingFragment
import android.content.res.Resources
import android.os.Bundle
import androidx.fragment.app.viewModels
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.SkuDetails
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BuyPremiumFragment :
    BaseBindingFragment<FragmentBuyPremiumBinding>(R.layout.fragment_buy_premium) {

    private val viewModel: BuyPremiumViewModel by viewModels()
    private var productsAdapter: ProductAdapter? = null

    override fun initUI(savedInstanceState: Bundle?) {
        initRV()
        collectData()
        viewModel.getSubscriptions()
    }

    private fun collectData() {
        viewModel.subscriptionsLiveData.observe(viewLifecycleOwner) { subscriptions ->
            if (subscriptions != null) {
                setProductsList(subscriptions)
            } else {
                toast(getString(R.string.skus_not_found))
            }
        }

    }

    private fun getNewWidthOfItem(products: Int): Int {
        return ((Resources.getSystem().displayMetrics.widthPixels - (products + 2) * resources
            .getDimension(R.dimen.activity_horizontal_margin)) / if (products == 1) 2 else products).toInt()
    }

    private fun initRV() {
        productsAdapter = ProductAdapter { skuDetails ->
            launchPurchaseFlow(skuDetails)
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
        billingClient.launchBillingFlow(requireActivity(), flowParams)
    }

    companion object {
        const val TAG = "BILLING_ABO"
    }
}