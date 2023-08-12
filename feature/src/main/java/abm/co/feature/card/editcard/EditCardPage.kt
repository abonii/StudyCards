package abm.co.feature.card.editcard

import abm.co.designsystem.component.button.IconButton
import abm.co.designsystem.component.button.PrimaryButton
import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.component.modifier.clickableWithoutRipple
import abm.co.designsystem.component.systembar.SetStatusBarColor
import abm.co.designsystem.component.textfield.ClearTextField
import abm.co.designsystem.extensions.collectInLaunchedEffect
import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.message.snackbar.MessageType
import abm.co.designsystem.theme.StudyCardsTheme
import abm.co.feature.R
import abm.co.feature.card.model.CardKindUI
import abm.co.feature.card.model.OxfordTranslationResponseUI
import abm.co.feature.utils.AnalyticsManager
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import abm.co.designsystem.R as dR

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EditCardPage(
    onBack: () -> Unit,
    openWordInfo: (
        fromNativeToLearning: Boolean,
        checkedOxfordItemsID: List<String>?,
        oxfordResponse: OxfordTranslationResponseUI
    ) -> Unit,
    onClickChangeCategory: (categoryID: String?) -> Unit,
    showMessage: suspend (MessageContent) -> Unit,
    viewModel: EditCardViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        AnalyticsManager.sendEvent(
            name = "edit_card_page_viewed"
        )
    }
    viewModel.channel.collectInLaunchedEffect {
        when (it) {
            is EditCardContractChannel.ChangeCategory -> {
                onClickChangeCategory(it.categoryId)
            }

            EditCardContractChannel.NavigateBack -> onBack()

            EditCardContractChannel.NavigateToSearchHistory -> {
                showMessage(
                    MessageContent.Snackbar.MessageContentRes(
                        titleRes = abm.co.designsystem.R.string.Messages_working,
                        subtitleRes = R.string.Message_inFuture,
                        type = MessageType.Info
                    )
                )  /*todo not released*/
            }

            is EditCardContractChannel.ShowMessage -> {
                showMessage(it.messageContent)
            }

            is EditCardContractChannel.NavigateToWordInfo -> {
                openWordInfo(
                    it.fromNativeToLearning,
                    it.checkedOxfordItemsID,
                    it.oxfordResponse
                )
            }
        }
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    DisposableEffect(Unit) {
        onDispose {
            keyboardController?.hide()
        }
    }
    val state = viewModel.uiState

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
            .background(StudyCardsTheme.colors.backgroundSecondary)
            .fillMaxSize()
    ) {
        Toolbar(
            modifier = Modifier
                .background(StudyCardsTheme.colors.backgroundPrimary)
                .statusBarsPadding()
                .padding(start = 6.dp, top = 10.dp, end = 16.dp),
            onBack = {
                onEvent(EditCardContractEvent.OnClickBack)
            }
        )
        ScrollableContent(
            modifier = Modifier.weight(1f),
            uiState = uiState,
            onEvent = onEvent
        )
        BottomButtons(
            modifier = Modifier
                .fillMaxWidth()
                .background(StudyCardsTheme.colors.backgroundPrimary),
            onClickPrimary = {
                onEvent(EditCardContractEvent.OnClickSaveCard)
            }
        )
    }
}

@Composable
private fun ScrollableContent(
    modifier: Modifier = Modifier,
    uiState: EditCardContractState,
    onEvent: (EditCardContractEvent) -> Unit
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        WordInfo(
            container = uiState.wordInfoContainer,
            onEnterNativeText = {
                onEvent(EditCardContractEvent.OnEnterNative(it))
            },
            onEnterLearningText = {
                onEvent(EditCardContractEvent.OnEnterLearning(it))
            },
            onClickTranslate = {
                onEvent(EditCardContractEvent.OnClickTranslate(it))
            }
        )
        if (uiState.translationVariantsContainer.isVisible) {
            TranslationVariantsContainer(
                container = uiState.translationVariantsContainer,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Category(
            container = uiState.categoryContainer,
            onClick = {
                onEvent(EditCardContractEvent.OnClickCategory)
            }
        )
        if (uiState.relatedWordsContainer.isVisible) {
            RelatedWordsContainer(
                container = uiState.relatedWordsContainer
            )
        }
        if (uiState.exampleContainer.isVisible) {
            ExamplesContainer(
                container = uiState.exampleContainer
            )
        }
        if (uiState.definitionContainer.isVisible) {
            DefinitionsContainer(
                container = uiState.definitionContainer
            )
        }
        ImageContainer(
            container = uiState.imageContainer
        )
    }
}

@Composable
private fun ExamplesContainer(
    container: EditCardContractState.ExampleContainer,
    modifier: Modifier = Modifier
) {
    ContainerHolder(
        title = stringResource(id = R.string.EditCard_Examples_title),
        modifier = modifier
    ) {
        val context = LocalContext.current
        Column(verticalArrangement = Arrangement.spacedBy(17.dp)) {
            container.examples.forEach { example ->
                ExamplesItem(
                    example = example,
                    onClick = {
                        example.speak(context)
                    }
                )
            }
        }
    }
}

@Composable
private fun ExamplesItem(
    example: EditCardContractState.ExampleContainer.ExampleUI,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = example.text,
                style = StudyCardsTheme.typography.weight500Size14LineHeight20,
                color = StudyCardsTheme.colors.textPrimary
            )
            Text(
                text = example.translation,
                style = StudyCardsTheme.typography.weight500Size14LineHeight20,
                color = StudyCardsTheme.colors.textSecondary
            )
        }
        IconButton(
            iconRes = R.drawable.ic_speak,
            onClick = onClick,
            contentColor = StudyCardsTheme.colors.buttonPrimary
        )
    }
}

@Composable
private fun WordInfo(
    container: EditCardContractState.WordInfoContainer,
    onEnterNativeText: (String) -> Unit,
    onEnterLearningText: (String) -> Unit,
    onClickTranslate: (fromNativeField: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(StudyCardsTheme.colors.backgroundPrimary)
            .padding(horizontal = 16.dp)
            .padding(bottom = 5.dp)
    ) {
        ClearTextField(
            hint = stringResource(id = R.string.EditCard_LearningLanuageField_hint),
            value = container.learningLanguageText,
            onValueChange = onEnterLearningText,
            endIconRes = R.drawable.ic_translate,
            singleLine = true,
            onClickEndIcon = { onClickTranslate(false) },
            modifier = Modifier.wrapContentWidth(),
            textStyle = StudyCardsTheme.typography.weight600Size32LineHeight24
                .copy(color = StudyCardsTheme.colors.buttonPrimary)
        )
        Divider(thickness = 1.dp, color = StudyCardsTheme.colors.stroke)
        ClearTextField(
            hint = stringResource(id = R.string.EditCard_NativeLanuageField_hint),
            value = container.nativeLanguageText,
            onValueChange = onEnterNativeText,
            endIconRes = R.drawable.ic_translate,
            singleLine = false,
            onClickEndIcon = { onClickTranslate(true) },
            textStyle = StudyCardsTheme.typography.weight400Size24LineHeight24
                .copy(color = StudyCardsTheme.colors.textSecondary),
            modifier = Modifier.wrapContentWidth()
        )
    }
}

@Composable
private fun Toolbar(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .clickableWithoutRipple(onBack)
                .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 10.dp)
                .size(24.dp),
            painter = painterResource(id = R.drawable.ic_left),
            contentDescription = null,
            tint = StudyCardsTheme.colors.opposition
        )
        Text(
            text = stringResource(id = R.string.EditCard_Toolbar_title),
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            style = StudyCardsTheme.typography.weight600Size23LineHeight24,
            color = StudyCardsTheme.colors.textPrimary
        )
    }
}

@Composable
private fun Category(
    container: EditCardContractState.CategoryContainer,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ContainerHolder(
        title = stringResource(id = R.string.EditCard_Category_title),
        modifier = modifier
    ) {
        Row(
            modifier = modifier.clickableWithoutRipple(onClick = onClick),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = stringResource(id = R.string.EditCard_Category_prefix) + " ${
                    container.category?.title ?: stringResource(
                        id = R.string.EditCard_Category_doesntExist
                    )
                }",
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 12.dp, end = 10.dp)
                    .weight(1f),
                style = StudyCardsTheme.typography.weight600Size14LineHeight18,
                color = StudyCardsTheme.colors.onyx,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Icon(
                modifier = Modifier.size(22.dp),
                painter = painterResource(id = R.drawable.ic_category),
                contentDescription = null,
                tint = StudyCardsTheme.colors.buttonPrimary
            )
        }
    }
}

@Composable
private fun TranslationVariantsContainer(
    container: EditCardContractState.TranslationVariantsContainer,
    modifier: Modifier = Modifier
) {
    ContainerHolder(
        title = stringResource(id = R.string.EditCard_TranslateVariants_title),
        modifier = modifier
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            container.translateVariants.forEach { translationVariant ->
                TranslationVariantsItem(
                    translationVariant = translationVariant
                )
            }
        }
    }
}

@Composable
private fun TranslationVariantsItem(
    translationVariant: EditCardContractState.TranslationVariantsContainer.TranslationVariantUI,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = when (translationVariant.kind) {
                        CardKindUI.UNDEFINED -> StudyCardsTheme.colors.blueMiddle
                        CardKindUI.UNKNOWN -> StudyCardsTheme.colors.unknown
                        CardKindUI.UNCERTAIN -> StudyCardsTheme.colors.uncertain
                        CardKindUI.KNOWN -> StudyCardsTheme.colors.known
                    },
                    shape = RoundedCornerShape(2.dp)
                )
                .width(4.dp)
                .height(24.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            modifier = Modifier.weight(1f),
            text = translationVariant.text,
            style = StudyCardsTheme.typography.weight400Size14LineHeight18,
            color = StudyCardsTheme.colors.textSecondary
        )
        Spacer(modifier = Modifier.width(30.dp))
        val selected by translationVariant.isSelected
        Box(
            modifier = Modifier.clickableWithoutRipple(
                onClick = {
                    translationVariant.setSelected(!selected)
                }
            )
        ) {
            if (selected) {
                androidx.compose.foundation.Image(
                    painter = painterResource(R.drawable.ic_selected),
                    colorFilter = ColorFilter.tint(
                        color = StudyCardsTheme.colors.success
                    ),
                    contentDescription = null
                )
            } else {
                androidx.compose.foundation.Image(
                    painter = painterResource(R.drawable.ic_select),
                    colorFilter = ColorFilter.tint(
                        color = StudyCardsTheme.colors.grayishBlue
                    ),
                    contentDescription = null
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RelatedWordsContainer(
    container: EditCardContractState.RelatedWordsContainer,
    modifier: Modifier = Modifier
) {
    ContainerHolder(
        title = stringResource(id = R.string.EditCard_RelatedWords_title),
        modifier = modifier
    ) {
        val groupedRelatedWords = remember(container.relatedWords) {
            container.relatedWords.groupBy { it.kind }
        }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            groupedRelatedWords.forEach { (kind, items) ->
                Text(
                    text = when (kind) {
                        EditCardContractState.RelatedWordsContainer.RelatedWordUI.KindUI.Synonym -> {
                            stringResource(id = R.string.EditCard_RelatedWords_synonyms)
                        }

                        EditCardContractState.RelatedWordsContainer.RelatedWordUI.KindUI.Antonym -> {
                            stringResource(id = R.string.EditCard_RelatedWords_antonyms)
                        }
                    },
                    style = StudyCardsTheme.typography.weight500Size14LineHeight20,
                    color = StudyCardsTheme.colors.textPrimary
                )
                FlowRow(
                    maxItemsInEachRow = 4,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    items.forEach { item ->
                        RelatedWordsTextItem(
                            modifier = Modifier.padding(bottom = 5.dp),
                            name = item.text
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RelatedWordsTextItem(
    name: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                color = StudyCardsTheme.colors.buttonPrimary.copy(alpha = .1f),
                shape = RoundedCornerShape(5.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = name,
            style = StudyCardsTheme.typography.weight400Size12LineHeight20,
            color = StudyCardsTheme.colors.buttonPrimary
        )
    }
}

@Composable
private fun DefinitionsContainer(
    container: EditCardContractState.DefinitionContainer,
    modifier: Modifier = Modifier
) {
    ContainerHolder(
        title = stringResource(id = R.string.EditCard_Definitions_title),
        modifier = modifier
    ) {
        container.definitions.forEachIndexed { index, definition ->
            DefinitionsItem(
                definition = "${index+1}. $definition"
            )
        }
    }
}

@Composable
private fun DefinitionsItem(
    definition: String,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier,
        text = definition,
        style = StudyCardsTheme.typography.weight500Size16LineHeight20,
        color = StudyCardsTheme.colors.textPrimary
    )
}

@Composable
private fun ImageContainer(
    container: EditCardContractState.ImageContainer,
    modifier: Modifier = Modifier
) {
    ContainerHolder(
        title = stringResource(id = R.string.EditCard_Image_title),
        modifier = modifier.animateContentSize()
    ) {
        val link = container.linkState
        ClearTextField(
            hint = stringResource(id = R.string.EditCard_Image_Filed_hint),
            value = container.linkState,
            onValueChange = container::setLink,
            textStyle = StudyCardsTheme.typography.weight600Size14LineHeight18,
        )
        if (link.value.isNotBlank()) {
            Spacer(modifier = Modifier.height(13.dp))
            Box(modifier = modifier) {
                AsyncImage(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .fillMaxWidth(),
                    model = link,
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    error = painterResource(id = R.drawable.ic_image)
                )
            }
        }
    }
}

@Composable
private fun BottomButtons(
    modifier: Modifier = Modifier,
    onClickPrimary: () -> Unit
) {
    Column(modifier = modifier) {
        Divider(thickness = 1.dp, color = StudyCardsTheme.colors.stroke)
        Spacer(modifier = Modifier.height(12.dp))
        PrimaryButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            title = stringResource(id = R.string.EditCard_Button_save),
            onClick = onClickPrimary
        )
        Spacer(modifier = Modifier.heightIn(24.dp))
    }
}

@Composable
private fun ContainerHolder(
    modifier: Modifier = Modifier,
    title: String,
    container: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = StudyCardsTheme.colors.backgroundPrimary,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = title,
            style = StudyCardsTheme.typography.weight400Size14LineHeight18,
            color = StudyCardsTheme.colors.textSecondary
        )
        Column {
            container()
        }
    }
}
