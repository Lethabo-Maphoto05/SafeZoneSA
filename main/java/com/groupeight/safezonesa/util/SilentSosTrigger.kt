package com.groupeight.safezonesa.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import com.groupeight.safezonesa.network.SosRequestDto
import android.os.VibratorManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.groupeight.safezonesa.data.SessionManager
import com.groupeight.safezonesa.network.ApiClient
import kotlinx.coroutines.tasks.await

/**
 * The silent / discreet SOS path: shake-3-times or volume-down-5x. Deliberately bypasses
 * SosScreen/SosViewModel's UI entirely and posts straight to POST /api/sos (Section 5.3),
 * so it works with the screen off or the app backgrounded — the whole point of a silent
 * trigger is that it produces no visible UI, only a short confirmatory vibration.
 *
 * Both callers (ShakeDetectionService for the accelerometer path, and MainActivity for the
 * volume-button path) call [trigger] so the actual "send it" logic exists in one place.
 */
object SilentSosTrigger {

    @SuppressLint("MissingPermission")
    suspend fun trigger(context: Context) {
        vibrate(context)

        val hasLocationPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val (lat, lng) = if (hasLocationPermission) {
            runCatching {
                val location = LocationServices.getFusedLocationProviderClient(context).lastLocation.await()
                (location?.latitude ?: 0.0) to (location?.longitude ?: 0.0)
            }.getOrDefault(0.0 to 0.0)
        } else {
            0.0 to 0.0
        }

        val userId = SessionManager.getUserId(context) ?: return // not logged in — nothing to attach the alert to

        runCatching {
            ApiClient.getService(context).raiseSos(SosRequestDto(userId, "Silent", lat, lng))
        }

        // Deliberately swallow the result either way: showing a success/failure dialog here
        // would defeat the entire point of a *silent* trigger. If it fails, the vibration
        // still happened, which is the only feedback the design calls for.
    }

    private fun vibrate(context: Context) {
        val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            manager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(400, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(400)
        }
    }
}
