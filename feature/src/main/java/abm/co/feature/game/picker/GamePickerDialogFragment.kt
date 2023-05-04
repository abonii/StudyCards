package abm.co.feature.game.picker

import abm.co.designsystem.base.BaseDialogFragment
import abm.co.designsystem.base.messageContent
import abm.co.designsystem.navigation.extension.navigateSafe
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
