package abm.co.permissions.extension

import abm.co.permissions.dialog.BaseAlertDialogFragment
import abm.co.permissions.model.PermissionResult
import android.Manifest
import android.app.Activity
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity

private const val PERMISSION_ACCESS_LOCATION = 10
const val BASE_ALERT_DIALOG_FRAGMENT_TAG = "BASE_ALERT_DIALOG_FRAGMENT_TAG"

fun Activity.requestPushNotificationsPermission(
    onActionGranted: (() -> Unit)? = null,
    onActionRejected: (() -> Unit)? = null,
    onActionDeniedPermanently: (() -> Unit)? = null
) {
    requestPushNotificationsPermissionImpl(
        onActionGranted = onActionGranted,
        onActionRejected = onActionRejected,
        onActionDeniedPermanently = onActionDeniedPermanently
    )
}

private fun Activity.requestPushNotificationsPermissionImpl(
    showRational: Boolean = false,
    rationalDialogTitle: String = "",
    rationalDialogDescription: String = "",
    onActionGranted: (() -> Unit)? = null,
    onActionRejected: (() -> Unit)? = null,
    onActionDeniedPermanently: (() -> Unit)? = null
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        (this as? AppCompatActivity)?.requestingPermissions(Manifest.permission.POST_NOTIFICATIONS) {
            requestCode = PERMISSION_ACCESS_LOCATION
            resultCallback = {
                when (this) {
                    is PermissionResult.PermissionGranted -> {
                        onActionGranted?.invoke()
                    }
                    is PermissionResult.PermissionDeniedPermanently -> {
                        onActionDeniedPermanently?.invoke()
                    }
                    is PermissionResult.PermissionDenied -> {
                        onActionRejected?.invoke()
                    }
                    is PermissionResult.ShowRational -> {
                        if (showRational) {
                            val baseAlertDialogFragment = BaseAlertDialogFragment.newInstance(
                                title = rationalDialogTitle,
                                description = rationalDialogDescription,
                                positiveButtonClickListener = {
                                    requestPushNotificationsPermission(
                                        onActionGranted = onActionGranted,
                                        onActionRejected = onActionRejected
                                    )
                                }
                            )
                            baseAlertDialogFragment.show(
                                supportFragmentManager,
                                BASE_ALERT_DIALOG_FRAGMENT_TAG
                            )
                        }
                    }
                }
            }
        }
    }
}