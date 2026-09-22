package com.groupeight.safezonesa.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.groupeight.safezonesa.MainActivity
import com.groupeight.safezonesa.R
import com.groupeight.safezonesa.util.SilentSosTrigger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.math.sqrt

/**
 * Runs as a foreground service (not tied to any Activity) so shake detection keeps working
 * with the screen off or the app in the background — a normal Activity-scoped SensorEventListener
 * would stop the moment onPause() fires. Detects 3 distinct shakes within a rolling 4-second
 * window and calls [SilentSosTrigger], which vibrates and posts the alert with no visible UI.
 *
 * This only needs the accelerometer, which — unlike the volume-button path in MainActivity —
 * has no dependency on the screen being on or the app having input focus.
 */
class ShakeDetectionService : Service(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private var lastShakeTimestamps = mutableListOf<Long>()
    private var lastTriggerTime = 0L

    companion object {
        private const val CHANNEL_ID = "silent_sos_channel"
        private const val NOTIFICATION_ID = 4201
        private const val SHAKE_THRESHOLD_MS2 = 14.0 // deviation from gravity that counts as "a shake"
        private const val SHAKE_WINDOW_MS = 4_000L
        private const val COOLDOWN_MS = 15_000L // don't fire again immediately after a trigger
    }

    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        startForeground(NOTIFICATION_ID, buildNotification())
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = START_STICKY

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onSensorChanged(event: SensorEvent) {
        val (x, y, z) = event.values
        val magnitude = sqrt((x * x + y * y + z * z).toDouble())
        val deviationFromGravity = kotlin.math.abs(magnitude - SensorManager.GRAVITY_EARTH)

        if (deviationFromGravity <= SHAKE_THRESHOLD_MS2) return

        val now = System.currentTimeMillis()
        lastShakeTimestamps.add(now)
        lastShakeTimestamps.removeAll { now - it > SHAKE_WINDOW_MS }

        if (lastShakeTimestamps.size >= 3 && now - lastTriggerTime > COOLDOWN_MS) {
            lastTriggerTime = now
            lastShakeTimestamps.clear()
            scope.launch { SilentSosTrigger.trigger(applicationContext) }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onDestroy() {
        sensorManager.unregisterListener(this)
        super.onDestroy()
    }

    private fun buildNotification(): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "Silent SOS protection", NotificationManager.IMPORTANCE_MIN
            ).apply {
                description = "Keeps shake-to-SOS active in the background."
                setShowBadge(false)
            }
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }

        val openAppIntent = PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        // Android requires a foreground service to show *some* persistent notification — this
        // is the minimum visible footprint that satisfies that, deliberately low-priority
        // (IMPORTANCE_MIN) and worded generically rather than "SOS armed", since a visible
        // "your phone can call for help" banner is itself a safety/privacy signal you may not
        // want broadcast on a shared or borrowed device's notification shade.
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("SafeZone SA")
            .setContentText("Running in the background")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setOngoing(true)
            .setContentIntent(openAppIntent)
            .build()
    }
}
