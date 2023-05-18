package abm.co.feature.game.picker

import abm.co.designsystem.base.BaseDialogFragment
import abm.co.designsystem.base.messageContent
import abm.co.designsystem.navigation.extension.navigateSafe
import abm.co.feature.game.model.GameKindUI
import android.view.View
import androidx.compose.runtime.Composable
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GamePickerDialogFragment : BaseDialogFragment() {

    companion object {
        private val rootViewId = View.generateViewId()
    }

    override val rootViewId: Int get() = GamePickerDialogFragment.rootViewId

    @Composable
    override fun InitUI(messageContent: messageContent) {
        GamePickerPage(
            navigateToGame = { gameKind, cards ->
                when(gameKind){
                    GameKindUI.Review -> {
                        findNavController().navigateSafe(
                            GamePickerDialogFragmentDirections.toReviewNavGraph(
                                cards = cards.toTypedArray()
                            )
                        )
                    }
                    GameKindUI.Guess -> {
                        findNavController().navigateSafe(
                            GamePickerDialogFragmentDirections.toGuessNavGraph(
                                cards = cards.toTypedArray()
                            )
                        )
                    }
                    GameKindUI.Pair -> {
                        findNavController().navigateSafe(
                            GamePickerDialogFragmentDirections.toPairItNavGraph(
                                cards = cards.toTypedArray()
                            )
                        )
                    }
                }
            },
            navigateToLearn = { category ->
                findNavController().navigateSafe(
                    GamePickerDialogFragmentDirections.toLearnNavGraph(
                        category = category
                    )
                )
            },
            navigateToRepeat = {

            },
            showMessage = messageContent
        )
    }
}
