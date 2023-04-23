package abm.co.feature.home

import abm.co.designsystem.base.BaseFragment
import abm.co.designsystem.base.messageContent
import android.os.Bundle
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment() {

    companion object {
        val rootViewId = View.generateViewId()
    }

    override val rootViewId: Int get() = Companion.rootViewId

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("init home fragment")
    }

    @Composable
    override fun InitUI(messageContent: messageContent) {
        LaunchedEffect(Unit){
            println("init home screen")
        }
        HomePage(
            showMessage = {
                messageContent(it)
            },
            onNavigateToLanguageSelectPage = {
//                findNavController().navigate(Graph.PROFILE) TODO navigation
            },
            openDrawer = {
                // TODO open drawer
            },
            navigateToAllCategory = {},
            navigateToCategory = { category ->
//                findNavController().navigate(
//                    route = CardDestinations.Category().route,
//                    args = bundleOf(
//                        CardDestinations.Category().category to category
//                    ),
//                    navOptions = NavOptions.Builder().apply {
//                        this.setEnterAnim(R.anim.slide_in_left)
//                    }.build()
//                ) TODO navigation
            },
            navigateToCategoryGame = {
//                findNavController().navigate(GameDestinations.SwipeGame.route) TODO navigation
            }
        )
    }
}
