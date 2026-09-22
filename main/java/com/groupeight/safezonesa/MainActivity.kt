package com.groupeight.safezonesa

import com.groupeight.safezonesa.data.PrefKeys
import com.groupeight.safezonesa.data.SessionManager
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.groupeight.safezonesa.navigation.SafeZoneNavGraph
import com.groupeight.safezonesa.service.ShakeDetectionService
import com.groupeight.safezonesa.ui.theme.SafeZoneSATheme
import com.groupeight.safezonesa.util.SilentSosTrigger
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    // Volume-down-x5 silent SOS. Important limitation, stated plainly rather than glossed
    // over: this only works while this Activity has input focus — i.e. the screen is on and
    // SafeZone SA is the foreground app. A normal app cannot intercept hardware key events
    // system-wide while the phone is locked; that requires an Accessibility Service or Device
    // Admin privileges, which is a much larger and more invasive permission ask than this
    // feature warrants. The shake-based trigger below (ShakeDetectionService) is the one that
    // genuinely works with the screen off, since it only needs the accelerometer.
    private val volumePressTimestamps = mutableListOf<Long>()

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { startShakeServiceIfReady() }

    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { startShakeServiceIfReady() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestNotificationPermissionIfNeeded()
        requestLocationPermissionIfNeeded()
        startShakeServiceIfReady()
        setContent {
            SafeZoneSATheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SafeZoneNavGraph()
                }
            }
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        // dispatchKeyEvent
        if (event.keyCode == KeyEvent.KEYCODE_VOLUME_DOWN && event.action == KeyEvent.ACTION_DOWN &&
            SessionManager.getPref(this, PrefKeys.VOLUME_SOS)) {
            val now = System.currentTimeMillis()
            volumePressTimestamps.add(now)
            volumePressTimestamps.removeAll { now - it > 3_000L }
            if (volumePressTimestamps.size >= 5) {
                volumePressTimestamps.clear()
                lifecycleScope.launch { SilentSosTrigger.trigger(applicationContext) }
            }
        }
        return super.dispatchKeyEvent(event)
    }

    /** Android 13+ requires runtime consent for push notifications (FR12) and, separately,
     *  to show the foreground-service notification the shake detector needs to run at all. */
    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun requestLocationPermissionIfNeeded() {
        val granted = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (!granted) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    /** Only starts once both permissions the service needs (notifications + location) are
     *  actually granted, so it doesn't get killed immediately or silently do nothing. */
    private fun startShakeServiceIfReady() {
        val hasLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasNotifications = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

        // startShakeServiceIfReady
        if (hasLocation && hasNotifications && SessionManager.getPref(this, PrefKeys.SHAKE_SOS))  {
            val intent = Intent(this, ShakeDetectionService::class.java)
            ContextCompat.startForegroundService(this, intent)
        }
    }
}
