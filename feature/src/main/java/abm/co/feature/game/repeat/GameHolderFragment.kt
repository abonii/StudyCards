package abm.co.feature.game.repeat

import abm.co.designsystem.component.dialog.ShowDialogOnBackPressed
import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.component.toolbar.Toolbar
import abm.co.designsystem.component.widget.LinearProgress
import abm.co.designsystem.extensions.launchLifecycleScope
import abm.co.designsystem.navigation.extension.navigateSafe
import abm.co.designsystem.theme.StudyCardsTheme
import abm.co.feature.R
import abm.co.feature.card.model.CardUI
import abm.co.feature.game.common.ImageAlertDialog
import abm.co.feature.game.guess.GuessFragment
import abm.co.feature.game.model.GameKindUI
import abm.co.feature.game.pairit.PairItFragment
import abm.co.feature.game.review.ReviewFragment
import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class GameHolderFragment : Fragment(R.layout.layout_game_holder) {

    companion object {
        const val BACK_PRESSED_KEY = "BACK_PRESSED_KEY"
        const val PROGRESS_KEY = "PROGRESS_KEY"
    }

    private val viewModel by viewModels<GameHolderViewModel>()

    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initNavigation()
        setFragmentResultListeners()
        initOtherScreen(view)
        collectViewModel()
    }

    private fun initNavigation() {
        val navHostFragment = childFragmentManager
            .findFragmentById(R.id.game_holder_host_fragment) as NavHostFragment
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.repeat_nav_graph)
        navController = navHostFragment.navController
        when (viewModel.gameKind) {
            GameKindUI.Review -> {
                graph.setStartDestination(R.id.review_nav_graph)
            }

            GameKindUI.Guess -> {
                graph.setStartDestination(R.id.guess_nav_graph)
            }

            GameKindUI.Pair -> {
                graph.setStartDestination(R.id.pair_it_nav_graph)
            }

            null -> {
                graph.setStartDestination(R.id.review_nav_graph)
            }
        }
        navController.setGraph(
            graph = graph,
            startDestinationArgs = bundleOf(
                "cards" to viewModel.takeCards(),
                "is_repeat" to true
            )
        )
    }

    private fun collectViewModel() {
        launchLifecycleScope {
            viewModel.channel.collectLatest {
                when (it) {
                    is RepeatContract.Channel.StartRepeat -> {
                        navigate(
                            destination = R.id.review_nav_graph,
                            cards = viewModel.takeCards(),
                            popUpDestination = R.id.guess_nav_graph
                        )
                        viewModel.updateCurrentGame(GameKindUI.Review)
                    }
                }
            }
        }
    }

    private fun setFragmentResultListeners() {
        with(childFragmentManager) {
            setFragmentResultListener(
                BACK_PRESSED_KEY,
                viewLifecycleOwner
            ) { _, _ -> findNavController().navigateUp() }
            setFragmentResultListener(
                PROGRESS_KEY,
                viewLifecycleOwner
            ) { _, bundle ->
                val progress = bundle.getFloat("progress")
                if (viewModel.gameKind != null) {
                    viewModel.updateProgress(newValue = progress, currentGame = null)
                } else {
                    viewModel.updateProgress(progress)
                }
            }
            setFragmentResultListener(
                ReviewFragment.REVIEW_FINISHED_KEY,
                viewLifecycleOwner
            ) { _, _ -> navigateToNextPage(currentGame = GameKindUI.Review) }
            setFragmentResultListener(
                PairItFragment.PAIR_IT_FINISHED_KEY,
                viewLifecycleOwner
            ) { _, _ -> navigateToNextPage(currentGame = GameKindUI.Pair) }
            setFragmentResultListener(
                GuessFragment.GUESS_FINISHED_KEY,
                viewLifecycleOwner
            ) { _, _ -> navigateToNextPage(currentGame = GameKindUI.Guess) }
        }
    }

    private fun navigateToNextPage(currentGame: GameKindUI) {
        when (currentGame) {
            GameKindUI.Guess -> {
                if (viewModel.gameKind != null) {
                    val canPlay = viewModel.removePlayedCards()
                    if (canPlay) {
                        navigate(
                            destination = R.id.guess_nav_graph,
                            cards = viewModel.takeCards(),
                            popUpDestination = R.id.guess_nav_graph
                        )
                    } else {
                        viewModel.showGameFinished()
                    }
                } else {
                    viewModel.updateProgress(newValue = 1f)
                    viewModel.updateLearnedCards()
                }
            }

            GameKindUI.Pair -> {
                if (viewModel.gameKind != null) {
                    val canPlay = viewModel.removePlayedCards()
                    if (canPlay) {
                        navigate(
                            destination = R.id.pair_it_nav_graph,
                            cards = viewModel.takeCards(),
                            popUpDestination = R.id.pair_it_nav_graph
                        )
                    } else {
                        viewModel.showGameFinished()
                    }
                } else {
                    navigate(
                        destination = R.id.guess_nav_graph,
                        cards = viewModel.takeCards(),
                        popUpDestination = R.id.pair_it_nav_graph
                    )
                    viewModel.updateCurrentGame(GameKindUI.Guess)
                    viewModel.updateProgress(newValue = 0f)
                }
            }

            GameKindUI.Review -> {
                if (viewModel.gameKind != null) {
                    val canPlay = viewModel.removePlayedCards()
                    if (canPlay) {
                        navigate(
                            destination = R.id.review_nav_graph,
                            cards = viewModel.takeCards(),
                            popUpDestination = R.id.review_nav_graph
                        )
                    } else {
                        viewModel.showGameFinished()
                    }
                } else {
                    navigate(
                        destination = R.id.pair_it_nav_graph,
                        cards = viewModel.takeCards(),
                        popUpDestination = R.id.review_nav_graph
                    )
                    viewModel.updateCurrentGame(GameKindUI.Pair)
                    viewModel.updateProgress(newValue = 0f)
                }
            }
        }
    }

    private fun navigate(
        @IdRes destination: Int,
        @IdRes popUpDestination: Int,
        cards: Array<CardUI>
    ) {
        navController.navigateSafe(
            destinationRes = destination,
            args = bundleOf(
                "cards" to cards,
                "is_repeat" to true
            ),
            navOptions = NavOptions.Builder()
                .setPopUpTo(popUpDestination, true)
                .setEnterAnim(abm.co.designsystem.R.anim.enter_from_right)
                .setExitAnim(abm.co.designsystem.R.anim.exit_to_left)
                .setPopEnterAnim(abm.co.designsystem.R.anim.pop_enter_from_left)
                .setPopExitAnim(abm.co.designsystem.R.anim.pop_exit_to_right)
                .build()
        )
    }

    private fun initOtherScreen(view: View) {
        val topHolder = view.findViewById<ComposeView>(R.id.top_holder)
        topHolder.setContent {
            StudyCardsTheme {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                TopHolder(
                    uiState = uiState,
                    onFinish = viewModel::showContinueToRepeatOrFinishRepeating,
                    onBack = viewModel::showConfirmDialogToNavigateBack,
                    navigateBack = {
                        findNavController().navigateUp()
                    }
                )
                val dialogState by viewModel.dialogState.collectAsStateWithLifecycle()
                Dialog(
                    dialogState = dialogState,
                    onBack = {
                        findNavController().navigateUp()
                    },
                    onDismiss = viewModel::dismissDialog,
                    onConfirmContinue = viewModel::onConfirmContinue,
                    onConfirmFinish = {
                        findNavController().navigateUp()
                    }
                )
            }
        }
    }

    @Composable
    private fun TopHolder(
        uiState: RepeatContract.ScreenState,
        onBack: () -> Unit,
        navigateBack: () -> Unit,
        onFinish: () -> Unit
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
        ) {
            Toolbar(
                title = when (uiState.currentGame) {
                    GameKindUI.Review -> stringResource(id = R.string.Review_Toolbar_title)
                    GameKindUI.Guess -> stringResource(id = R.string.Guess_Toolbar_title)
                    GameKindUI.Pair -> stringResource(id = R.string.PairIt_Toolbar_title)
                },
                onBack = onBack
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgress(
                progressFloat = uiState.progress,
                contentColor = StudyCardsTheme.colors.blueMiddle,
                backgroundColor = StudyCardsTheme.colors.silver.copy(alpha = 0.15f),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .height(6.dp)
                    .fillMaxWidth(),
                onReach100Percent = {
                    if (viewModel.gameKind != null) {
                        navigateBack()
                    } else {
                        onFinish()
                    }
                }
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
    }

    @Composable
    private fun Dialog(
        dialogState: RepeatContract.DialogState,
        onDismiss: () -> Unit,
        onConfirmContinue: () -> Unit,
        onConfirmFinish: () -> Unit,
        onBack: () -> Unit
    ) {
        ShowDialogOnBackPressed(
            show = dialogState.backPressConfirm,
            onConfirm = onBack,
            onDismiss = onDismiss
        )
        if (dialogState.continueRepeatOrFinish) {
            ImageAlertDialog(
                title = "Do you want to repeat the other cards or is it enough?", // todo
                confirm = "Continue",
                dismiss = "Finish",
                onConfirm = onConfirmContinue,
                onDismiss = onConfirmFinish,
                imageRes = R.drawable.illustration_game_repeat_continue,
                properties = DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false
                )
            )
        }
        if (dialogState.finishedRepeat) {
            ImageAlertDialog(
                title = "Your speed puts the Flash to shame", // todo
                confirm = "Continue",
                onConfirm = onConfirmFinish,
                onDismiss = onConfirmFinish,
                imageRes = R.drawable.illustration_game_finished
            )
        }
        if (dialogState.finishedGame != null) {
            ImageAlertDialog(
                title = "Your speed puts the Flash to shame", // todo
                confirm = "Continue",
                onConfirm = onConfirmFinish,
                onDismiss = onConfirmFinish,
                imageRes = R.drawable.illustration_game_finished
            )
        }
    }
}
