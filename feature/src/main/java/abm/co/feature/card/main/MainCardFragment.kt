package abm.co.feature.card.main

import abm.co.designsystem.base.BaseFragment
import abm.co.designsystem.base.messageContent
import abm.co.designsystem.navigation.extension.navigateSafe
import android.view.View
import androidx.compose.runtime.Composable
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainCardFragment : BaseFragment() {

    companion object {
        private val rootViewId = View.generateViewId()
    }

    override val rootViewId: Int get() = MainCardFragment.rootViewId

    @Composable
    override fun InitUI(messageContent: messageContent) {
        MainCardPage(
            navigateToLearnGame = {
                findNavController().navigateSafe(
                    MainCardFragmentDirections.toLearnNavGraph(it)
                )
            },
            showMessage = messageContent
        )
    }
}