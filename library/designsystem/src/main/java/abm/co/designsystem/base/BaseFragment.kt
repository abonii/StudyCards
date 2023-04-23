package abm.co.designsystem.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.fragment.app.Fragment

abstract class BaseFragment: Fragment() {

    abstract val rootViewId: Int

    @Composable
    abstract fun InitUI(
        messageContent: messageContent
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return composableView(rootViewId = rootViewId) { composableInFragment ->
            InitUI(messageContent = composableInFragment)
        }
    }
}
