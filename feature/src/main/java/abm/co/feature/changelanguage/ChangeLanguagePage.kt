package abm.co.feature.changelanguage

import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.component.modifier.clickableWithoutRipple
import abm.co.designsystem.component.systembar.SetStatusBarColor
import abm.co.designsystem.component.widget.LoadingView
import abm.co.designsystem.extensions.collectInLaunchedEffect
import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.theme.StudyCardsTheme
import abm.co.feature.R
import abm.co.feature.userattributes.lanugage.LanguageItem
import abm.co.feature.userattributes.lanugage.LanguageUI
import abm.co.feature.utils.AnalyticsManager
import abm.co.feature.utils.StudyCardsConstants
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch

@Immutable
enum class HeaderKind {
    NATIVE_LANGUAGES,
    LEARNING_LANGUAGES;
}

@Composable
fun ChangeLanguagePage(
    navigateBack: () -> Unit,
    showMessage: suspend (MessageContent) -> Unit,
    viewModel: ChangeLanguageViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        AnalyticsManager.sendEvent(
            name = "change_language_page_viewed"
        )
    }

    viewModel.channel.collectInLaunchedEffect(
        function = { contract ->
            when (contract) {
                ChangeLanguageContractChannel.OnBack -> navigateBack()
                is ChangeLanguageContractChannel.ShowMessage -> showMessage(contract.messageContent)
            }
        }
    )

    val state by viewModel.state.collectAsState()

    SetStatusBarColor()
    StoreScreen(
        state = state,
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun StoreScreen(
    state: ChangeLanguageContractState,
    onEvent: (ChangeLanguageContractEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .background(StudyCardsTheme.colors.backgroundPrimary)
            .statusBarsPadding()
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Toolbar(
            onBack = {
                onEvent(ChangeLanguageContractEvent.OnBack)
            }
        )
        when (state) {
            ChangeLanguageContractState.Loading -> {
                LoadingScreen(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
            }

            is ChangeLanguageContractState.Success -> {
                SuccessContent(
                    learningLanguages = state.learningLanguages,
                    nativeLanguages = state.nativeLanguages,
                    selectedLearningLanguage = state.selectedLearningLanguage,
                    selectedNativeLanguage = state.selectedNativeLanguage,
                    onClickItem = { kind, language ->
                        when (kind) {
                            HeaderKind.NATIVE_LANGUAGES -> {
                                onEvent(ChangeLanguageContractEvent.OnClickNativeLanguage(language))
                            }

                            HeaderKind.LEARNING_LANGUAGES -> {
                                onEvent(ChangeLanguageContractEvent.OnClickLearningLanguage(language))
                            }
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SuccessContent(
    learningLanguages: ImmutableList<LanguageUI>,
    nativeLanguages: ImmutableList<LanguageUI>,
    selectedLearningLanguage: LanguageUI?,
    selectedNativeLanguage: LanguageUI?,
    onClickItem: (HeaderKind, LanguageUI) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        val pagerState = rememberPagerState()
        val coroutineScope = rememberCoroutineScope()
        Header(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(
                        page = it.ordinal
                    )
                }
            },
            selectedHeader = HeaderKind.values()[pagerState.currentPage]
        )
        HorizontalPager(
            state = pagerState,
            pageCount = HeaderKind.values().size,
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp
            ),
            pageSpacing = 32.dp,
            key = { it }
        ) { pageIndex ->
            val headerKind = HeaderKind.values()[pageIndex]
            LanguageListContent(
                items = when (headerKind) {
                    HeaderKind.NATIVE_LANGUAGES -> {
                        nativeLanguages
                    }

                    HeaderKind.LEARNING_LANGUAGES -> {
                        learningLanguages
                    }
                },
                selectedItem = when (headerKind) {
                    HeaderKind.NATIVE_LANGUAGES -> {
                        selectedNativeLanguage
                    }

                    HeaderKind.LEARNING_LANGUAGES -> {
                        selectedLearningLanguage
                    }
                },
                onClickItem = {
                    onClickItem(headerKind, it)
                },
                modifier = Modifier
                    .weight(1f)
            )
        }
    }
}

@Composable
private fun Header(
    selectedHeader: HeaderKind,
    onClick: (HeaderKind) -> Unit,
    modifier: Modifier = Modifier
) {
    @Composable
    fun Item(
        kind: HeaderKind,
        modifier: Modifier = Modifier
    ) {
        Box(
            modifier = modifier
                .border(
                    width = 0.5.dp,
                    color = StudyCardsTheme.colors.blueMiddle,
                    shape = CircleShape
                )
                .clip(CircleShape)
                .background(
                    if (selectedHeader == kind) {
                        StudyCardsTheme.colors.blueMiddle
                    } else {
                        StudyCardsTheme.colors.backgroundPrimary
                    }
                )
                .heightIn(min = 32.dp)
                .clickable(
                    onClick = {
                        onClick(kind)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(
                    id = when (kind) {
                        HeaderKind.NATIVE_LANGUAGES -> R.string.ChangeLanguage_NativeLanguage_title
                        HeaderKind.LEARNING_LANGUAGES -> R.string.ChangeLanguage_LearningLanguage_title
                    }
                ),
                style = StudyCardsTheme.typography.weight500Size14LineHeight20,
                color = StudyCardsTheme.colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
    Row(
        modifier = modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        HeaderKind.values().forEach { kind ->
            Item(
                modifier = Modifier.weight(1f),
                kind = kind
            )
        }
    }

}

@Composable
private fun LanguageListContent(
    items: ImmutableList<LanguageUI>,
    selectedItem: LanguageUI?,
    onClickItem: (LanguageUI) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items.forEach { item ->
            LanguageItem(
                language = item,
                onClick = {
                    onClickItem(item)
                },
                backgroundColor = if (selectedItem?.code == item.code) StudyCardsTheme.colors.blueMiddle
                else StudyCardsTheme.colors.backgroundSecondary,
                contentColor = StudyCardsTheme.colors.textPrimary
            )
        }
    }
}

@Composable
private fun LoadingScreen(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        LoadingView()
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
            text = stringResource(id = R.string.ChangeLanguage_Toolbar_title),
            style = StudyCardsTheme.typography.weight600Size14LineHeight18,
            color = StudyCardsTheme.colors.opposition,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}
