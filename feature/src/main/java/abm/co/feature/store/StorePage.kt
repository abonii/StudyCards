package abm.co.feature.store

import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.component.modifier.clickableWithoutRipple
import abm.co.designsystem.component.systembar.SetStatusBarColor
import abm.co.designsystem.extensions.getActivity
import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.theme.StudyCardsTheme
import abm.co.feature.R
import abm.co.feature.utils.AnalyticsManager
import abm.co.feature.utils.StudyCardsConstants
import abm.co.feature.utils.StudyCardsConstants.APP_NAME
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.hilt.navigation.compose.hiltViewModel
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.SkuDetails

@Composable
fun StorePage(
    navigateBack: () -> Unit,
    showMessage: suspend (MessageContent) -> Unit,
    viewModel: StoreViewModel = hiltViewModel()
) {
    val activity = getActivity()
    LaunchedEffect(Unit) {
        AnalyticsManager.sendEvent(
            name = "store_page_viewed"
        )
    }
    val state by viewModel.skusStateFlow.collectAsState()

    SetStatusBarColor()
    StoreScreen(
        items = state,
        onClickBack = navigateBack,
        onClick = { product ->
            activity?.let {
                val flowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(product)
                    .build()
                when (
                    viewModel.getBillingClient().launchBillingFlow(activity, flowParams)
                        .responseCode
                ) {
                    BillingClient.BillingResponseCode.SERVICE_DISCONNECTED,
                    BillingClient.BillingResponseCode.SERVICE_TIMEOUT -> {
                        viewModel.retryConnection()
                    }
                }
            }
        }
    )
}

@Composable
private fun StoreScreen(
    items: List<SkuDetails>,
    onClick: (SkuDetails) -> Unit,
    onClickBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .background(StudyCardsTheme.colors.backgroundPrimary)
            .statusBarsPadding()
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Toolbar(onBack = onClickBack)
        Spacer(modifier = Modifier.height(10.dp))
        Image(
            modifier = Modifier.fillMaxWidth(0.8f),
            painter = painterResource(id = R.drawable.illustration_for_buy_premium),
            contentDescription = null,
            contentScale = ContentScale.FillWidth
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            modifier = Modifier,
            text = stringResource(id = R.string.Store_Info_title),
            style = StudyCardsTheme.typography.weight600Size14LineHeight18,
            color = StudyCardsTheme.colors.textPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            modifier = Modifier,
            text = stringResource(id = R.string.Store_Info_subtitle),
            style = StudyCardsTheme.typography.weight500Size14LineHeight20,
            color = StudyCardsTheme.colors.textSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(26.dp))
        Row(
            modifier = Modifier.padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items.forEachIndexed { index, skuDetails ->
                PurchaseItem(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onClick(skuDetails)
                    },
                    title = remember(skuDetails) {
                        skuDetails.title.replace(
                            oldValue = "($APP_NAME)",
                            newValue = ""
                        ).trim()
                    },
                    price = skuDetails.price,
                    backgroundColor = if (index == items.size / 2) {
                        StudyCardsTheme.colors.primary
                    } else {
                        StudyCardsTheme.colors.backgroundPrimary
                    },
                    contentColor = if (index == items.size / 2) {
                        Color.White
                    } else {
                        StudyCardsTheme.colors.textPrimary
                    }
                )
            }
        }
    }
}

@Composable
private fun PurchaseItem(
    title: String,
    price: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = StudyCardsTheme.colors.backgroundPrimary,
    contentColor: Color = StudyCardsTheme.colors.textPrimary
) {
    Column(
        modifier = modifier
            .border(
                width = 1.dp,
                color = StudyCardsTheme.colors.backgroundSecondary,
                shape = RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp))
            .background(color = backgroundColor)
            .clickable(onClick = onClick)
            .fillMaxWidth()
            .aspectRatio(1.05f)
            .padding(horizontal = 5.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = title,
            style = StudyCardsTheme.typography.weight500Size14LineHeight20,
            color = contentColor,
            textAlign = TextAlign.Center
        )
        Text(
            text = price,
            style = StudyCardsTheme.typography.weight600Size14LineHeight18,
            color = contentColor,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun Toolbar(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(StudyCardsConstants.TOOLBAR_HEIGHT)
            .fillMaxWidth()
            .padding(start = 6.dp, top = 6.dp, bottom = 6.dp, end = 16.dp)
    ) {
        Icon(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .clickableWithoutRipple(onBack)
                .padding(10.dp)
                .size(24.dp),
            painter = painterResource(id = R.drawable.ic_left),
            tint = StudyCardsTheme.colors.opposition,
            contentDescription = null
        )
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = stringResource(id = R.string.Store_Toolbar_title),
            style = StudyCardsTheme.typography.weight600Size14LineHeight18,
            color = StudyCardsTheme.colors.buttonPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}
