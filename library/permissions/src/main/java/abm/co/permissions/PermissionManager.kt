/*
 * Copyright 2019 Sagar Viradiya
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package abm.co.permissions

import abm.co.permissions.model.PermissionRequest
import abm.co.permissions.model.PermissionResult
import android.content.Context
import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

/**
 * A headless fragment which wraps the boilerplate code for checking and requesting permission.
 * A simple [Fragment] subclass.
 */

class PermissionManager : BasePermissionManager() {

    private var callbackMap = mutableMapOf<Int, PermissionResult.() -> Unit>()

    override fun onPermissionResult(permissionResult: PermissionResult) {
        callbackMap[permissionResult.requestCode]?.let {
            permissionResult.it()
        }
        callbackMap.remove(permissionResult.requestCode)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbackMap.clear()
    }

    companion object {

        private const val TAG = "PermissionManager"

        @JvmStatic
        @MainThread
        inline fun requestingPermissions(
            activity: AppCompatActivity,
            vararg permissions: String,
            requestBlock: PermissionRequest.() -> Unit
        ) {
            val permissionRequest = PermissionRequest().apply(requestBlock)
            requireNotNull(permissionRequest.requestCode) {
                "No request code specified."
            }
            requireNotNull(permissionRequest.resultCallback) {
                "No result callback found."
            }
            _requestPermissions(
                activity,
                permissionRequest.requestCode!!,
                permissionRequest.resultCallback!!,
                *permissions
            )
        }

        @JvmStatic
        @MainThread
        inline fun requestingPermissions(
            fragment: Fragment,
            vararg permissions: String,
            requestBlock: PermissionRequest.() -> Unit
        ) {
            val permissionRequest = PermissionRequest().apply(requestBlock)
            requireNotNull(permissionRequest.requestCode) {
                "No request code specified."
            }
            requireNotNull(permissionRequest.resultCallback) {
                "No result callback found."
            }
            _requestPermissions(
                fragment,
                permissionRequest.requestCode!!,
                permissionRequest.resultCallback!!,
                *permissions
            )
        }

        fun _requestPermissions(
            activityOrFragment: Any,
            requestId: Int,
            callback: PermissionResult.() -> Unit,
            vararg permissions: String
        ) {
            val fragmentManager = if (activityOrFragment is AppCompatActivity) {
                activityOrFragment.supportFragmentManager
            } else {
                (activityOrFragment as Fragment).childFragmentManager
            }
            if (fragmentManager.findFragmentByTag(TAG) != null) {
                (fragmentManager.findFragmentByTag(TAG) as PermissionManager)
                    .also { it.callbackMap[requestId] = callback }
                    .requestingPermissions(requestId, *permissions)
            } else {
                val permissionManager = PermissionManager()
                fragmentManager.beginTransaction().add(permissionManager, TAG).commitNow()
                permissionManager.callbackMap[requestId] = callback
                permissionManager.requestingPermissions(requestId, *permissions)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        callbackMap.clear()
    }
}
