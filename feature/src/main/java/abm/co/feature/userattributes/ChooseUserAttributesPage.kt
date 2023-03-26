package abm.co.feature.userattributes

import abm.co.designsystem.flow.collectInLaunchedEffect
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
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import kotlinx.collections.immutable.ImmutableList

@Immutable
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
    LaunchedEffect(Unit) {
        Firebase.analytics.logEvent(
            "user_attributes_page_viewed",
            null
        )
    }
    viewModel.channel.collectInLaunchedEffect {
        when (it) {
            ChooseUserAttributesContractChannel.NavigateToHomePage -> onNavigateHomePage()
            is ChooseUserAttributesContractChannel.ShowMessage -> showMessage(it.messageContent)
        }
    }
    SetStatusBarColor(iconsColorsDark = false)
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
    BackHandler(state.currentPage != UserAttributesPage.NativeLanguage) {
        when (state.currentPage) {
            UserAttributesPage.LearningLanguage -> {
                event(ChooseUserAttributesContractEvent.OnNavigateToNativeLanguage)
            }
            UserAttributesPage.UserGoal -> {
                event(
                    ChooseUserAttributesContractEvent.OnNavigateToLearningLanguage(
                        nativeLanguage = null,
                        isToRight = false
                    )
                )
            }
            UserAttributesPage.UserInterests -> {
                event(
                    ChooseUserAttributesContractEvent.OnNavigateToUserGoal(
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
    currentPage: UserAttributesPage,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = when (currentPage) {
                UserAttributesPage.NativeLanguage -> {
                    stringResource(id = R.string.ChooseUserAttributesPage_NativeLanguage_title)
                }
                UserAttributesPage.LearningLanguage -> {
                    stringResource(id = R.string.ChooseUserAttributesPage_LearningLanguage_title)
                }
                UserAttributesPage.UserGoal -> {
                    stringResource(id = R.string.ChooseUserAttributesPage_UserGoal_title)
                }
                UserAttributesPage.UserInterests -> {
                    stringResource(id = R.string.ChooseUserAttributesPage_UserInterests_title)
                }
            },
            style = StudyCardsTheme.typography.weight600Size23LineHeight24,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = when (currentPage) {
                UserAttributesPage.NativeLanguage -> {
                    stringResource(id = R.string.ChooseUserAttributesPage_NativeLanguage_subtitle)
                }
                UserAttributesPage.LearningLanguage -> {
                    stringResource(id = R.string.ChooseUserAttributesPage_LearningLanguage_subtitle)
                }
                UserAttributesPage.UserGoal -> {
                    stringResource(id = R.string.ChooseUserAttributesPage_UserGoal_subtitle)
                }
                UserAttributesPage.UserInterests -> {
                    stringResource(id = R.string.ChooseUserAttributesPage_UserInterests_subtitle)
                }
            },
            style = StudyCardsTheme.typography.weight400Size16LineHeight20,
            color = Color.White
        )
    }
}

@Composable
private fun ChangeableContent(
    currentPage: UserAttributesPage,
    userGoals: ImmutableList<UserGoalUI>,
    languages: ImmutableList<LanguageUI>,
    userInterests: ImmutableList<UserInterestUI>,
    event: (ChooseUserAttributesContractEvent) -> Unit,
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
            visible = currentPage == UserAttributesPage.NativeLanguage,
            languages = languages,
            isToRight = isToRight,
            onClickItem = {
                Firebase.analytics.logEvent(
                    "native_language_selected",
                    bundleOf("code" to it.code)
                )
                event(ChooseUserAttributesContractEvent.OnNavigateToLearningLanguage(it, true))
            }
        )
        LanguageItems(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(bottom = 20.dp),
            visible = currentPage == UserAttributesPage.LearningLanguage,
            languages = languages,
            isToRight = isToRight,
            onClickItem = {
                Firebase.analytics.logEvent(
                    "learning_language_selected",
                    bundleOf("code" to it.code)
                )
                if (showAdditionQuiz) {
                    event(ChooseUserAttributesContractEvent.OnNavigateToUserGoal(it, true))
                } else {
                    event(ChooseUserAttributesContractEvent.OnFinish)
                }
            }
        )
        if (showAdditionQuiz) {
            UserGoalItems(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 20.dp),
                visible = currentPage == UserAttributesPage.UserGoal,
                userGoals = userGoals,
                isToRight = isToRight,
                onClickItem = {
                    Firebase.analytics.logEvent(
                        "user_goal_selected",
                        bundleOf("id" to it.id)
                    )
                    event(ChooseUserAttributesContractEvent.OnNavigateToUserInterests(it))
                }
            )
            UserInterestItems(
                visible = currentPage == UserAttributesPage.UserInterests,
                userInterests = userInterests,
                isToRight = isToRight,
                onClickItem = {
                    Firebase.analytics.logEvent(
                        "user_interest_selected",
                        bundleOf("id" to it.id, "is_selected" to !it.isSelected)
                    )
                    event(ChooseUserAttributesContractEvent.OnSelectUserInterest(it))
                },
                onClickContinueButton = {
                    event(ChooseUserAttributesContractEvent.OnFinish)
                }
            )
        }
    }
}

