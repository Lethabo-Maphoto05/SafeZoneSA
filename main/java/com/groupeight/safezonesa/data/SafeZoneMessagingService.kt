package com.groupeight.safezonesa.data

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.groupeight.safezonesa.MainActivity

/**
 * FR12 — Location Based (Geo Targeted) Notifications.
 *
 * The ASP.NET Core API (Section 5) computes which registered users fall inside the
 * notification radius of a new verified incident and dispatches the push via Firebase
 * Cloud Messaging (Section 5.4 / 6). This service receives that push on the device and
 * is responsible only for local display + routing into the app (Section 4.5's Push
 * Notifications screen) — all targeting logic stays server-side per NFR8.
 *
 * Expected data payload keys (set server-side): "category" (alert | message | system),
 * "title", "body" — matching the three tabs on the Notifications screen.
 */
class SafeZoneMessagingService : FirebaseMessagingService() {

    companion object {
        private const val CHANNEL_ALERTS = "safezone_alerts"
        private const val CHANNEL_GENERAL = "safezone_general"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // TEMPORARY for Firebase console testing — copy this token into the "Send test
        // message" dialog. Remove this line (or gate it behind BuildConfig.DEBUG) before
        // Part 2 submission; tokens shouldn't sit in release logs.
        android.util.Log.d("SafeZoneFCM", "FCM token: $token")

        // TODO: POST the new FCM token to a /api/users/{id}/device-token endpoint so the
        // backend's Community Alert & Notification Service (Section 6) can keep sending
        // to this device. Requires SessionManager.getUserId(this) to already be set,
        // i.e. this fires meaningfully only after the user has logged in at least once.
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        if (!SessionManager.getPref(this, PrefKeys.ALERT_NOTIFICATIONS)) return

        val category = message.data["category"] ?: "alert"
        val title = message.notification?.title ?: message.data["title"] ?: "SafeZone SA"
        val body = message.notification?.body ?: message.data["body"] ?: ""

        ensureChannels()

        val channelId = if (category == "alert") CHANNEL_ALERTS else CHANNEL_GENERAL

        val contentIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_alert) // TODO: swap for a proper shield icon asset
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(if (category == "alert") NotificationCompat.PRIORITY_HIGH else NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun ensureChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        manager.createNotificationChannel(
            NotificationChannel(CHANNEL_ALERTS, "Safety Alerts", NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Nearby verified incidents, SOS escalations, missing-person notices (FR3, FR11, FR12)"
            }
        )
        manager.createNotificationChannel(
            NotificationChannel(CHANNEL_GENERAL, "Messages & System", NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "Community chat messages and app/system updates"
            }
        )
    }
}
