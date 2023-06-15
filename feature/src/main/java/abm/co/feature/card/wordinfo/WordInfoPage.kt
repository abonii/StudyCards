package abm.co.feature.card.wordinfo

import abm.co.designsystem.component.button.IconButton
import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.component.modifier.clickableWithoutRipple
import abm.co.designsystem.component.systembar.SetStatusBarColor
import abm.co.designsystem.extensions.collectInLaunchedEffect
import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.theme.StudyCardsTheme
import abm.co.feature.R
import abm.co.feature.card.model.OxfordEntryUI
import abm.co.feature.card.model.OxfordTranslationResponseUI
import abm.co.feature.utils.AnalyticsManager
import abm.co.feature.utils.StudyCardsConstants
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun WordInfoPage(
    scrollState: ScrollState,
    navigateBack: () -> Unit,
    showMessage: suspend (MessageContent) -> Unit,
    viewModel: WordInfoViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        AnalyticsManager.sendEvent(
            name = "word_info_page_viewed"
        )
    }

    viewModel.channel.collectInLaunchedEffect(
        function = { contract ->
            when (contract) {
                is WordInfoContractChannel.ShowMessage -> {
                    showMessage(contract.messageContent)
                }

                WordInfoContractChannel.NavigateBack -> {
                    navigateBack()
                }
            }
        }
    )

    val state by viewModel.state.collectAsState()

    SetStatusBarColor()
    WordInfoScreen(
        uiState = state,
        onEvent = viewModel::onEvent,
        scrollState = scrollState,
        checkedItemsID = viewModel.checkedItemsID
    )
}

@Composable
fun WordInfoScreen(
    uiState: WordInfoContractState,
    checkedItemsID: SnapshotStateList<String>,
    onEvent: (WordInfoContractEvent) -> Unit,
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState()
) {
    Column(
        modifier = modifier
            .background(
                color = StudyCardsTheme.colors.backgroundPrimary,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            )
            .statusBarsPadding()
            .wrapContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Toolbar(
            onBack = {
                onEvent(WordInfoContractEvent.OnBack)
            }
        )
        Success(
            oxfordResponse = uiState.oxfordResponse,
            checkedItemsID = checkedItemsID,
            modifier = Modifier.verticalScroll(scrollState),
            onClickEntry = {
                onEvent(WordInfoContractEvent.OnClickEntry(it))
            },
            onClickPlayText = {
                onEvent(WordInfoContractEvent.OnClickPlayText(it))
            }
        )
    }
}

@Composable
private fun Success(
    oxfordResponse: OxfordTranslationResponseUI,
    checkedItemsID: SnapshotStateList<String>,
    onClickEntry: (OxfordEntryUI) -> Unit,
    onClickPlayText: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp)
    ) {
        oxfordResponse.word?.let {
            Name(
                text = it,
                onClickPlayText = {
                    onClickPlayText(it)
                }
            )
        }
        oxfordResponse.lexicalEntry?.forEach { lexicalEntry ->
            lexicalEntry.lexicalKind?.let {
                Text(
                    text = it,
                    style = StudyCardsTheme.typography.weight400Size16LineHeight20,
                    color = StudyCardsTheme.colors.textSecondary
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            lexicalEntry.entries?.forEach { entry ->
                key(entry.id) {
                    EntryContent(
                        item = entry,
                        onClick = {
                            onClickEntry(entry)
                        },
                        checked = remember(entry.id) {
                            derivedStateOf {
                                checkedItemsID.contains(entry.id)
                            }
                        }.value,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun Name(
    text: String,
    onClickPlayText: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.offset(y = (-1).dp),
            text = text,
            style = StudyCardsTheme.typography.weight600Size23LineHeight24,
            color = StudyCardsTheme.colors.grayishBlue,
        )
        IconButton(
            onClick = onClickPlayText,
            iconRes = R.drawable.ic_speak,
            contentColor = StudyCardsTheme.colors.grayishBlue
        )
    }
}

@Composable
private fun EntryContent(
    item: OxfordEntryUI,
    checked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(StudyCardsTheme.colors.blueMiddle)
            .clickable(onClick = onClick)
            .padding(vertical = 15.dp, horizontal = 10.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Checkbox(
                modifier = Modifier.size(20.dp),
                checked = checked,
                colors = CheckboxDefaults.colors(
                    checkedColor = StudyCardsTheme.colors.blueMiddle,
                    checkmarkColor = Color.White
                ),
                onCheckedChange = null
            )
            Text(
                text = item.translation,
                style = StudyCardsTheme.typography.weight500Size14LineHeight20,
                color = StudyCardsTheme.colors.textPrimary
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        item.examples?.forEachIndexed { index, example ->
            if (example.text != null || example.translation != null) {
                ExampleItem(
                    text = example.text,
                    translation = example.translation,
                    position = index + 1
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ExampleItem(
    position: Int,
    text: String?,
    translation: String?,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier,
        text = buildAnnotatedString {
            withStyle(
                style = StudyCardsTheme.typography.weight500Size16LineHeight20.toSpanStyle()
            ) {
                append("$position. ")
            }
            text?.let {
                append(text)
            }
            translation?.let {
                withStyle(
                    style = StudyCardsTheme.typography.weight600Size16LineHeight18.toSpanStyle()
                ) {
                    append(" - ")
                }
                append(translation)
            }
        },
        style = StudyCardsTheme.typography.weight400Size14LineHeight18,
        color = StudyCardsTheme.colors.textPrimary
    )
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
            .padding(6.dp)
    ) {
        Icon(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .clickableWithoutRipple(onBack)
                .padding(10.dp)
                .size(24.dp),
            painter = painterResource(id = R.drawable.ic_close),
            tint = StudyCardsTheme.colors.opposition,
            contentDescription = null
        )
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = stringResource(id = R.string.WordInfo_Toolbar_title),
            style = StudyCardsTheme.typography.weight600Size16LineHeight18,
            color = StudyCardsTheme.colors.opposition,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}
