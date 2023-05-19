package abm.co.feature.game.repeat

import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.component.toolbar.Toolbar
import abm.co.designsystem.component.widget.LinearProgress
import abm.co.designsystem.navigation.extension.navigateSafe
import abm.co.designsystem.theme.StudyCardsTheme
import abm.co.feature.R
import abm.co.feature.card.model.CardUI
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
import androidx.compose.runtime.State
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RepeatFragment : Fragment(R.layout.layout_repeat) {

    companion object {
        const val BACK_PRESSED_KEY = "back_pressed_key"
    }

    private val viewModel by viewModels<RepeatViewModel>()

    lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initNavigation()
        setFragmentResultListeners()
        initOtherScreen(view)
    }

    private fun initNavigation() {
        val navHostFragment = childFragmentManager
            .findFragmentById(R.id.repeat_host_fragment) as NavHostFragment
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.repeat_nav_graph)
        navController = navHostFragment.navController
        navController.setGraph(
            graph = graph,
            startDestinationArgs = bundleOf(
                "cards" to viewModel.takeCards(),
                "is_repeat" to true
            )
        )
        viewModel.currentGame.value = GameKindUI.Review
    }

    private fun setFragmentResultListeners() {
        with(childFragmentManager) {
            setFragmentResultListener(
                BACK_PRESSED_KEY,
                viewLifecycleOwner
            ) { _, _ -> findNavController().navigateUp() }
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
                viewModel.updateLearnedCards()
            }

            GameKindUI.Pair -> {
                navigate(
                    destination = R.id.guess_nav_graph,
                    cards = viewModel.takeCards(),
                    popUpDestination = R.id.to_pair_it_nav_graph
                )
                viewModel.currentGame.value = GameKindUI.Guess
            }

            GameKindUI.Review -> {
                navigate(
                    destination = R.id.pair_it_nav_graph,
                    cards = viewModel.takeCards(),
                    popUpDestination = R.id.review_nav_graph
                )
                viewModel.currentGame.value = GameKindUI.Pair
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
                val currentGameState = viewModel.currentGame.collectAsStateWithLifecycle()
                TopHolder(
                    currentGame = currentGameState,
                    onFinish = {
                        findNavController().navigateUp()
                    }
                )
            }
        }
    }

    @Composable
    private fun TopHolder(
        currentGame: State<GameKindUI?>,
        onFinish: () -> Unit
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
        ) {
            Toolbar(title = "I don't know, thinking...")
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgress(
                progressFloat = when (currentGame.value) {
                    GameKindUI.Review -> 0.25f
                    GameKindUI.Guess -> 0.75f
                    GameKindUI.Pair -> 0.5f
                    null -> 1f
                },
                contentColor = StudyCardsTheme.colors.blueMiddle,
                backgroundColor = StudyCardsTheme.colors.silver.copy(alpha = 0.15f),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .height(6.dp)
                    .fillMaxWidth(),
                onReach100Percent = onFinish
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}
