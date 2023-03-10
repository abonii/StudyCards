package abm.co.feature.userattributes

import abm.co.designsystem.collectInLaunchedEffect
import abm.co.designsystem.component.SetStatusBarColor
import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.list.gridItemsIndexed
import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.theme.StudyCardsTheme
import abm.co.designsystem.widget.LinearProgress
import abm.co.feature.userattributes.lanugage.LanguageItem
import abm.co.feature.userattributes.lanugage.availableLanguages
import abm.co.feature.userattributes.usergoal.UserGoalItem
import abm.co.feature.userattributes.usergoal.userGoals
import abm.co.feature.userattributes.userinterest.UserInterestItem
import abm.co.feature.userattributes.userinterest.userInterests
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

enum class UserAttributesPage {
    NativeLanguage,
    LearningLanguage,
    UserGoal,
    UserInterests;
}

@Composable
fun ChooseUserAttributesPage(
    onNavigateHomePage: () -> Unit,
    showMessage: suspend (MessageContent) -> Unit,
    viewModel: ChooseUserAttributesViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    viewModel.channel.collectInLaunchedEffect {
        when (it) {
            ChooseUserAttributesContractChannel.NavigateToHomePage -> onNavigateHomePage()
            is ChooseUserAttributesContractChannel.ShowMessage -> showMessage(it.messageContent)
        }
    }
    SetStatusBarColor()
    ChooseUserAttributesScreen(
        state = state,
        event = viewModel::event
    )
}

@Composable
private fun ChooseUserAttributesScreen(
    state: ChooseUserAttributesContractState,
    event: (ChooseUserAttributesContractEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .background(StudyCardsTheme.colors.primary)
            .padding(top = 20.dp)
            .statusBarsPadding()
    ) {
        LinearProgress(
            progressFloat = state.progress,
            modifier = Modifier.fillMaxWidth(),
            onReach100Percent = {/*TODO remove or do*/ }
        )
        Spacer(modifier = Modifier.height(20.dp))
        LazyColumn(
            modifier = Modifier,
            contentPadding = PaddingValues(vertical = 20.dp, horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                AnimatedVisibility(
                    modifier = Modifier,
                    visible = true,
                    enter = fadeIn(animationSpec = tween(durationMillis = 500)),
                    exit = fadeOut(animationSpec = tween(durationMillis = 500))
                ) {
                    Column {
                        Text(
                            text = "Родной язык", // todo
                            style = StudyCardsTheme.typography.weight600Size23LineHeight24,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Выберите родной язык из списка", // todo
                            style = StudyCardsTheme.typography.weight400Size16LineHeight20,
                            color = Color.White
                        )
                    }
                }
            }

            when (state.currentPage) {
                UserAttributesPage.NativeLanguage -> {
                    items(availableLanguages) { language ->
                        LanguageItem(
                            language = language,
                            onClick = {
                                event(ChooseUserAttributesContractEvent.OnClickNativeLanguage(it))
                            }
                        )
                    }
                }
                UserAttributesPage.LearningLanguage -> {
                    items(availableLanguages) { language ->
                        LanguageItem(
                            language = language,
                            onClick = {
                                event(ChooseUserAttributesContractEvent.OnClickLearningLanguage(it))
                            }
                        )
                    }
                }
                UserAttributesPage.UserGoal -> {
                    items(userGoals) { userGoal ->
                        UserGoalItem(userGoal = userGoal, onClick = {
                            event(ChooseUserAttributesContractEvent.OnToggleUserInterests(it))
                        })
                    }
                }
                UserAttributesPage.UserInterests -> {
                    val gridBoxModifier = itemModifier@{ columnIndex: Int ->
                        var modifier: Modifier = androidx.compose.ui.Modifier
                        val maxColumnsIndex = (userInterests.count() - 1) / 3
                        if (columnIndex != maxColumnsIndex) {
//                            modifier = modifier.padding(bottom = 12.dp)
                        }
                        return@itemModifier modifier
                    }
                    gridItemsIndexed(
                        data = userInterests,
                        spanCount = 3,
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        itemModifier = gridBoxModifier,
                        key = { _, category ->
                            category.id
                        }
                    ) { index, item ->
                        UserInterestItem(
                            userInterest = item,
                            onClick = {/*TODO one click*/ },
                            modifier = Modifier
                                .fillMaxSize()
                                .aspectRatio(1f)
                        )
                    }
                }
            }
        }
    }
}
