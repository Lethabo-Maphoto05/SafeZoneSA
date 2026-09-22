package com.groupeight.safezonesa.ui.components

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat

/**
 * Requests ACCESS_FINE_LOCATION, needed by the Crime Map (FR7), Walk With Me (FR4),
 * Safe Zones (FR6), and Safe Route (FR13) screens. Returns whether it's currently
 * granted plus a function to trigger the system permission dialog.
 */
@Composable
fun rememberLocationPermissionState(): Pair<Boolean, () -> Unit> {
    val context = androidx.compose.ui.platform.LocalContext.current
    var granted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        granted = isGranted
    }
    val request: () -> Unit = { launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION) }
    return granted to request
}
