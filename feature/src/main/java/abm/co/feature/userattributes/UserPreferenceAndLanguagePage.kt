package abm.co.feature.userattributes

import abm.co.designsystem.extensions.collectInLaunchedEffect
import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.component.systembar.SetStatusBarColor
import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.theme.StudyCardsTheme
import abm.co.designsystem.component.widget.LinearProgress
import abm.co.feature.R
import abm.co.feature.userattributes.lanugage.LanguageItems
import abm.co.feature.userattributes.lanugage.LanguageUI
import abm.co.feature.userattributes.usergoal.UserGoalItems
import abm.co.feature.userattributes.usergoal.UserGoalUI
import abm.co.feature.userattributes.userinterest.UserInterestItems
import abm.co.feature.userattributes.userinterest.UserInterestUI
import abm.co.feature.utils.AnalyticsManager
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.collections.immutable.ImmutableList

@Immutable
enum class UserPreferenceAndLanguagePage {
    NativeLanguage,
    LearningLanguage,
    UserGoal,
    UserInterests;
}

@Composable
fun UserPreferenceAndLanguage(
    onNavigateHomePage: () -> Unit,
    showMessage: suspend (MessageContent) -> Unit,
    viewModel: UserPreferenceAndLanguageViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(Unit) {
        AnalyticsManager.sendEvent(
            name = "user_preference_and_language_page_viewed"
        )
    }
    viewModel.channel.collectInLaunchedEffect {
        when (it) {
            UserPreferenceAndLanguageContractChannel.NavigateToHomePage -> onNavigateHomePage()
            is UserPreferenceAndLanguageContractChannel.ShowMessage -> showMessage(it.messageContent)
        }
    }
    SetStatusBarColor(iconsColorsDark = false)
    UserPreferenceAndLanguageScreen(
        state = state,
        event = viewModel::event
    )
}

@Composable
private fun UserPreferenceAndLanguageScreen(
    state: ChooseUserPreferenceAndLanguageContractState,
    event: (ChooseUserPreferenceAndLanguageContractEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(Color(0xFF_2970E5))
            .padding(top = 20.dp)
            .systemBarsPadding()
            .fillMaxSize()
            .animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        LinearProgress(
            progressFloat = state.progress,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(9.dp))
                .height(6.dp)
                .fillMaxWidth(),
            contentColor = Color.White,
            backgroundColor = Color(0x5E_CADAE7),
            onReach100Percent = { /*Just ignore*/ }
        )
        Column(
            modifier = Modifier.padding(top = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            TopTitle(
                modifier = Modifier.padding(horizontal = 16.dp),
                currentPage = state.currentPage
            )
            ChangeableContent(
                modifier = Modifier,
                currentPage = state.currentPage,
                userGoals = state.userGoals,
                languages = state.languages,
                userInterests = state.userInterests,
                isToRight = state.isToRight,
                showAdditionQuiz = state.showAdditionQuiz,
                event = event
            )
        }
    }
    BackHandler(state.currentPage != UserPreferenceAndLanguagePage.NativeLanguage) {
        when (state.currentPage) {
            UserPreferenceAndLanguagePage.LearningLanguage -> {
                event(ChooseUserPreferenceAndLanguageContractEvent.OnNavigateToNativeLanguage)
            }
            UserPreferenceAndLanguagePage.UserGoal -> {
                event(
                    ChooseUserPreferenceAndLanguageContractEvent.OnNavigateToLearningLanguage(
                        nativeLanguage = null,
                        isToRight = false
                    )
                )
            }
            UserPreferenceAndLanguagePage.UserInterests -> {
                event(
                    ChooseUserPreferenceAndLanguageContractEvent.OnNavigateToUserGoal(
                        learningLanguage = null,
                        isToRight = false
                    )
                )
            }
            else -> Unit
        }
    }
}

@Composable
private fun TopTitle(
    currentPage: UserPreferenceAndLanguagePage,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = when (currentPage) {
                UserPreferenceAndLanguagePage.NativeLanguage -> {
                    stringResource(id = R.string.UserPreferenceAndLanguagePage_NativeLanguage_title)
                }
                UserPreferenceAndLanguagePage.LearningLanguage -> {
                    stringResource(id = R.string.UserPreferenceAndLanguagePage_LearningLanguage_title)
                }
                UserPreferenceAndLanguagePage.UserGoal -> {
                    stringResource(id = R.string.UserPreferenceAndLanguagePage_UserGoal_title)
                }
                UserPreferenceAndLanguagePage.UserInterests -> {
                    stringResource(id = R.string.UserPreferenceAndLanguagePage_UserInterests_title)
                }
            },
            style = StudyCardsTheme.typography.weight600Size23LineHeight24,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = when (currentPage) {
                UserPreferenceAndLanguagePage.NativeLanguage -> {
                    stringResource(id = R.string.UserPreferenceAndLanguagePage_NativeLanguage_subtitle)
                }
                UserPreferenceAndLanguagePage.LearningLanguage -> {
                    stringResource(id = R.string.UserPreferenceAndLanguagePage_LearningLanguage_subtitle)
                }
                UserPreferenceAndLanguagePage.UserGoal -> {
                    stringResource(id = R.string.UserPreferenceAndLanguagePage_UserGoal_subtitle)
                }
                UserPreferenceAndLanguagePage.UserInterests -> {
                    stringResource(id = R.string.UserPreferenceAndLanguagePage_UserInterests_subtitle)
                }
            },
            style = StudyCardsTheme.typography.weight400Size16LineHeight20,
            color = Color.White
        )
    }
}

@Composable
private fun ChangeableContent(
    currentPage: UserPreferenceAndLanguagePage,
    userGoals: ImmutableList<UserGoalUI>,
    languages: ImmutableList<LanguageUI>,
    userInterests: ImmutableList<UserInterestUI>,
    event: (ChooseUserPreferenceAndLanguageContractEvent) -> Unit,
    isToRight: Boolean,
    showAdditionQuiz: Boolean,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        LanguageItems(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(bottom = 20.dp),
            visible = currentPage == UserPreferenceAndLanguagePage.NativeLanguage,
            languages = languages,
            isToRight = isToRight,
            onClickItem = {
                AnalyticsManager.sendEvent(
                    name = "native_language_selected",
                    params = bundleOf("code" to it.code)
                )
                event(ChooseUserPreferenceAndLanguageContractEvent.OnNavigateToLearningLanguage(it, true))
            }
        )
        LanguageItems(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(bottom = 20.dp),
            visible = currentPage == UserPreferenceAndLanguagePage.LearningLanguage,
            languages = languages,
            isToRight = isToRight,
            onClickItem = {
                AnalyticsManager.sendEvent(
                    name = "learning_language_selected",
                    params = bundleOf("code" to it.code)
                )
                if (showAdditionQuiz) {
                    event(ChooseUserPreferenceAndLanguageContractEvent.OnNavigateToUserGoal(it, true))
                } else {
                    event(ChooseUserPreferenceAndLanguageContractEvent.OnFinish(it))
                }
            }
        )
        if (showAdditionQuiz) {
            UserGoalItems(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 20.dp),
                visible = currentPage == UserPreferenceAndLanguagePage.UserGoal,
                userGoals = userGoals,
                isToRight = isToRight,
                onClickItem = {
                    AnalyticsManager.sendEvent(
                        name = "user_goal_selected",
                        params = bundleOf("id" to it.id)
                    )
                    event(ChooseUserPreferenceAndLanguageContractEvent.OnNavigateToUserInterests(it))
                }
            )
            UserInterestItems(
                visible = currentPage == UserPreferenceAndLanguagePage.UserInterests,
                userInterests = userInterests,
                isToRight = isToRight,
                onClickItem = {
                    AnalyticsManager.sendEvent(
                        name = "user_interest_selected",
                        params = bundleOf("id" to it.id, "is_selected" to !it.isSelected)
                    )
                    event(ChooseUserPreferenceAndLanguageContractEvent.OnSelectUserInterest(it))
                },
                onClickContinueButton = {
                    event(ChooseUserPreferenceAndLanguageContractEvent.OnFinish(null))
                }
            )
        }
    }
}

