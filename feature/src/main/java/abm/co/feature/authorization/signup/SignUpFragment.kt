package abm.co.feature.authorization.signup

import abm.co.designsystem.base.BaseFragment
import abm.co.designsystem.base.messageContent
import android.view.View
import androidx.compose.runtime.Composable
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : BaseFragment() {

    companion object {
        private val rootViewId = View.generateViewId()
    }

    override val rootViewId: Int get() = SignUpFragment.rootViewId

    @Composable
    override fun InitUI(messageContent: messageContent) {
        SignUpPage(
            showMessage = {
                messageContent(it)
            },
            onNavigateChooseUserAttributes = {
//                navController.navigate(
//                    route = Graph.USER_ATTRIBUTES,
//                    args = bundleOf(
//                        ChooseUserAttributesDestination().showAdditionQuiz to true
//                    ),
//                    navOptions = NavOptions.Builder().apply {
//                        setPopUpTo(Graph.AUTH, inclusive = true)
//                    }.build() todo navigation
//                )
            },
            onNavigateLoginPage = {
//                navController.navigate(AuthDestinations.Login.route) {
//                    popUpTo(AuthDestinations.WelcomeLogin.route) todo navigation
//                }
            }
        )
    }
}