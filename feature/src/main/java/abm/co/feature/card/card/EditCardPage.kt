package abm.co.feature.card.card

import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.component.modifier.clickableWithoutRipple
import abm.co.designsystem.component.systembar.SetStatusBarColor
import abm.co.designsystem.component.textfield.TextFieldWithLabel
import abm.co.designsystem.component.widget.LinearProgress
import abm.co.designsystem.extensions.collectInLaunchedEffect
import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.preview.ThemePreviews
import abm.co.designsystem.theme.StudyCardsTheme
import abm.co.feature.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Translate
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EditCardPage(
    onBack: () -> Unit,
    showMessage: suspend (MessageContent) -> Unit,
    viewModel: EditCardViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        Firebase.analytics.logEvent(
            "edit_card_page_viewed", null
        )
    }
    viewModel.channel.collectInLaunchedEffect {
        when (it) {
            is EditCardContractChannel.ChangeCategory -> {
                // todo not done yet
            }

            EditCardContractChannel.NavigateBack -> onBack()

            EditCardContractChannel.NavigateToSearchHistory -> {/*todo not released*/
            }

            is EditCardContractChannel.ShowMessage -> {
                showMessage(it.messageContent)
            }
        }
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    DisposableEffect(Unit) {
        onDispose {
            keyboardController?.hide()
        }
    }
    val state by viewModel.state.collectAsState()

    SetStatusBarColor()
    EditCardScreen(
        uiState = state,
        onEvent = viewModel::onEvent
    )
}


@Composable
private fun EditCardScreen(
    uiState: EditCardContractState,
    onEvent: (EditCardContractEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .background(StudyCardsTheme.colors.backgroundPrimary)
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .systemBarsPadding()
    ) {
        Spacer(modifier = Modifier.height(29.dp))
        Toolbar(
            onClickSearch = {
                onEvent(EditCardContractEvent.OnClickCategory)
            }
        )
        Spacer(modifier = Modifier.height(24.dp))
        uiState.progress?.let { progress ->
            LinearProgress(
                progressFloat = progress,
                contentColor = StudyCardsTheme.colors.blueMiddle,
                backgroundColor = StudyCardsTheme.colors.silver.copy(alpha = 0.15f),
                modifier = Modifier
                    .clip(RoundedCornerShape(7.dp))
                    .height(6.dp)
                    .fillMaxWidth(),
                onReach100Percent = {
                    onEvent(EditCardContractEvent.OnFinishProgress)
                }
            )
            Spacer(modifier = Modifier.height(29.dp))
        }
        CategoryInfo(
            name = uiState.categoryName
                ?: stringResource(id = R.string.EditCard_Category_doesntExist),
            onClick = {
                onEvent(EditCardContractEvent.OnClickCategory)
            },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(24.dp))
        Fields(
            nativeLanguage = uiState.nativeLanguage?.languageNameResCode?.let {
                stringResource(id = it)
            } ?: "",
            learningLanguage = uiState.learningLanguage?.languageNameResCode?.let {
                stringResource(id = it)
            } ?: "",
            onEnterNativeText = {
                onEvent(EditCardContractEvent.OnEnterNative(it))
            },
            onEnterLearningText = {
                onEvent(EditCardContractEvent.OnEnterLearning(it))
            },
            imageURL = uiState.imageURL,
            nativeText = uiState.nativeText,
            learningText = uiState.learningText,
            onEnterImageURL = {
                onEvent(EditCardContractEvent.OnEnterImage(it))
            },
            onClickTranslate = {
                onEvent(EditCardContractEvent.OnClickTranslate(it))
            },
            exampleContent = {
                if (uiState.example != null) {
                    // todo not empty content
                } else {
                    Text(
                        modifier = Modifier.clickableWithoutRipple {
                            onEvent(EditCardContractEvent.OnClickEnterExample(""))
                        },
                        text = stringResource(id = R.string.EditCard_Example_add),
                        style = StudyCardsTheme.typography.weight500Size14LineHeight20,
                        color = StudyCardsTheme.colors.textPrimary
                    )
                }
            }
        )
    }
}

@OptIn(FlowPreview::class)
@Composable
private fun Fields(
    imageURL: String,
    nativeLanguage: String,
    learningLanguage: String,
    nativeText: String,
    learningText: String,
    onEnterImageURL: (String) -> Unit,
    onEnterNativeText: (String) -> Unit,
    onEnterLearningText: (String) -> Unit,
    onClickTranslate: (fromNative: Boolean) -> Unit,
    exampleContent: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
    ) {
        var showImage by remember {
            mutableStateOf(imageURL.isNotBlank())
        }
        LaunchedEffect(Unit) {
            snapshotFlow { imageURL }
                .debounce(500)
                .collectLatest {
                    showImage = imageURL.isNotBlank()
                }
        }
        if (imageURL.isNotBlank()) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .aspectRatio(0.65f),
                model = imageURL,
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        }
        TextFieldWithLabel(
            label = stringResource(
                id = R.string.EditCard_LanuageField_title,
                nativeLanguage,
                nativeLanguage
            ),
            hint = stringResource(id = R.string.EditCard_NativeLanuageField_hint),
            value = nativeText,
            onValueChange = onEnterNativeText,
            trailingIcon = {
                IconButton(onClick = { onClickTranslate(true) }) {
                    Icon(
                        imageVector = Icons.Filled.Translate,
                        tint = StudyCardsTheme.colors.primary,
                        contentDescription = null
                    )
                }
            },
            modifier = Modifier
                .height(62.dp)
                .wrapContentWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextFieldWithLabel(
            label = stringResource(
                id = R.string.EditCard_LanuageField_title,
                learningLanguage,
                learningLanguage
            ),
            hint = stringResource(id = R.string.EditCard_LearningLanuageField_hint),
            value = learningText,
            onValueChange = onEnterLearningText,
            trailingIcon = {
                IconButton(onClick = { onClickTranslate(false) }) {
                    Icon(
                        imageVector = Icons.Filled.Translate,
                        tint = StudyCardsTheme.colors.primary,
                        contentDescription = null
                    )
                }
            },
            modifier = Modifier
                .height(62.dp)
                .wrapContentWidth()
        )
        Spacer(modifier = Modifier.height(7.dp))
        exampleContent()
        Spacer(modifier = Modifier.height(24.dp))
        TextFieldWithLabel(
            label = stringResource(id = R.string.EditCard_ImageField_title),
            hint = stringResource(id = R.string.EditCard_ImageField_hint),
            value = imageURL,
            onValueChange = onEnterImageURL,
            modifier = Modifier
                .height(62.dp)
                .wrapContentWidth(),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_link),
                    tint = StudyCardsTheme.colors.buttonPrimary,
                    contentDescription = null
                )
            }
        )
    }
}

@Composable
private fun Toolbar(
    onClickSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Text(
            text = stringResource(id = R.string.EditCard_Toolbar_title),
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            style = StudyCardsTheme.typography.weight600Size23LineHeight24,
            color = StudyCardsTheme.colors.textPrimary
        )
        IconButton(
            modifier = Modifier
                .clip(CircleShape)
                .background(StudyCardsTheme.colors.buttonPrimary)
                .size(32.dp),
            onClick = { onClickSearch() }
        ) {
            Icon(
                modifier = Modifier,
                painter = painterResource(id = R.drawable.ic_search),
                tint = Color.White,
                contentDescription = null
            )
        }
    }
}

@Composable
private fun CategoryInfo(
    name: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(StudyCardsTheme.colors.middleGray)
            .clickableWithoutRipple { onClick() }
            .wrapContentWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            modifier = Modifier
                .padding(start = 13.dp, top = 12.dp, bottom = 12.dp)
                .size(20.dp),
            painter = painterResource(id = R.drawable.ic_page),
            tint = StudyCardsTheme.colors.onyx,
            contentDescription = null
        )
        Text(
            text = stringResource(id = R.string.EditCard_Category_prefix) + name,
            modifier = Modifier
                .padding(top = 12.dp, bottom = 12.dp, end = 10.dp),
            style = StudyCardsTheme.typography.weight400Size16LineHeight20,
            color = StudyCardsTheme.colors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@ThemePreviews
@Composable
fun MainScreenPreview() {
    val mutableState = MutableStateFlow(
        EditCardContractState(
            progress = 0.6f,
            categoryName = null
        )
    )
    EditCardScreen(
        uiState = mutableState.collectAsState().value,
        onEvent = { }
    )
}