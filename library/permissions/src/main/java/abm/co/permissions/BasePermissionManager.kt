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

import abm.co.permissions.model.PermissionResult
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

/**
 * A simple abstract [Fragment] subclass.
 */
abstract class BasePermissionManager : Fragment() {

    private lateinit var permissionViewModel: PermissionViewModel
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        handlePermissionsResult(permissionViewModel.currentRequestCode, permissions)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionViewModel = ViewModelProvider(this)[PermissionViewModel::class.java]
    }

    protected fun requestingPermissions(requestId: Int, vararg permissions: String) {
        permissionViewModel.currentRequestCode = requestId
        permissionViewModel.getRationalRequest(requestId)?.let {
            requestPermissionLauncher.launch(permissions.toList().toTypedArray())
            permissionViewModel.removeRationalRequest(requestId)
            return
        }

        val notGranted = permissions.filter {
            ContextCompat.checkSelfPermission(
                requireActivity(),
                it
            ) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        when {
            notGranted.isEmpty() ->
                onPermissionResult(PermissionResult.PermissionGranted(requestId))

            notGranted.any { shouldShowRequestPermissionRationale(it) } -> {
                permissionViewModel.updateRationalRequest(requestId, true)
                onPermissionResult(PermissionResult.ShowRational(requestId))
            }

            else -> {
                requestPermissionLauncher.launch(notGranted)
            }
        }
    }

    private fun handlePermissionsResult(requestCode: Int, permissions: Map<String, Boolean>) {
        if (permissions.isNotEmpty() && permissions.all { it.value }) {
            onPermissionResult(PermissionResult.PermissionGranted(requestCode))
        } else if (permissions.any { shouldShowRequestPermissionRationale(it.key) }) {
            onPermissionResult(
                PermissionResult.PermissionDenied(
                    requestCode,
                    permissions.filterNot { it.value }.keys.toList()
                )
            )
        } else {
            onPermissionResult(
                PermissionResult.PermissionDeniedPermanently(
                    requestCode,
                    permissions.filterNot { it.value }.keys.toList()
                )
            )
        }
    }

    protected abstract fun onPermissionResult(permissionResult: PermissionResult)
}
