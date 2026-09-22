package com.groupeight.safezonesa.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.groupeight.safezonesa.data.PrefKeys
import com.groupeight.safezonesa.data.SessionManager
import com.groupeight.safezonesa.service.ShakeDetectionService
import com.groupeight.safezonesa.ui.components.SafeZoneTopBar
import com.groupeight.safezonesa.ui.theme.AlertRed
import com.groupeight.safezonesa.ui.theme.DeepBlue
import com.groupeight.safezonesa.ui.theme.PageBackground
import com.groupeight.safezonesa.ui.theme.TextSecondary

/**
 * Settings menu. Every switch has a real effect:
 *  - Shake-to-SOS starts/stops ShakeDetectionService.
 *  - Volume-button SOS is checked in MainActivity.dispatchKeyEvent.
 *  - Alert notifications are checked in SafeZoneMessagingService.
 *  - "Post anonymously by default" pre-ticks the box in the New Post dialog.
 * Values are stored on the device through SessionManager (SharedPreferences).
 */
@Composable
fun SettingsScreen(onBack: () -> Unit, onLoggedOut: () -> Unit) {
    val context = LocalContext.current

    var shakeSos by remember { mutableStateOf(SessionManager.getPref(context, PrefKeys.SHAKE_SOS, true)) }
    var volumeSos by remember { mutableStateOf(SessionManager.getPref(context, PrefKeys.VOLUME_SOS, true)) }
    var alerts by remember { mutableStateOf(SessionManager.getPref(context, PrefKeys.ALERT_NOTIFICATIONS, true)) }
    var anonymous by remember { mutableStateOf(SessionManager.getPref(context, PrefKeys.ANONYMOUS_DEFAULT, false)) }
    var confirmLogout by remember { mutableStateOf(false) }

    val name = SessionManager.getDisplayName(context)?.takeIf { it.isNotBlank() } ?: "SafeZone member"
    val email = SessionManager.getEmail(context)?.takeIf { it.isNotBlank() } ?: ""

    Column(Modifier.fillMaxSize().background(PageBackground)) {
        SafeZoneTopBar("Settings", onBack)

        Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            SectionHeader("Account")
            Column(Modifier.fillMaxWidth().background(Color.White).padding(16.dp)) {
                Text(name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                if (email.isNotEmpty()) Text(email, fontSize = 12.sp, color = TextSecondary)
            }

            SectionHeader("Emergency triggers")
            SettingSwitchRow(
                title = "Shake-to-SOS",
                subtitle = "Shake the phone three times to send a silent SOS",
                checked = shakeSos
            ) { on ->
                shakeSos = on
                SessionManager.setPref(context, PrefKeys.SHAKE_SOS, on)
                val intent = Intent(context, ShakeDetectionService::class.java)
                val hasLocation = ContextCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
                if (on && hasLocation) ContextCompat.startForegroundService(context, intent)
                else if (!on) context.stopService(intent)
            }
            SettingSwitchRow(
                title = "Volume-button SOS",
                subtitle = "Press volume-down five times while the app is open",
                checked = volumeSos
            ) { on ->
                volumeSos = on
                SessionManager.setPref(context, PrefKeys.VOLUME_SOS, on)
            }

            SectionHeader("Notifications and privacy")
            SettingSwitchRow(
                title = "Nearby alert notifications",
                subtitle = "Show push alerts about incidents near you",
                checked = alerts
            ) { on ->
                alerts = on
                SessionManager.setPref(context, PrefKeys.ALERT_NOTIFICATIONS, on)
            }
            SettingSwitchRow(
                title = "Post anonymously by default",
                subtitle = "Hide your name on new community reports",
                checked = anonymous
            ) { on ->
                anonymous = on
                SessionManager.setPref(context, PrefKeys.ANONYMOUS_DEFAULT, on)
            }

            Spacer(Modifier.height(24.dp))
            OutlinedButton(
                onClick = { confirmLogout = true },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AlertRed)
            ) { Text("Log out") }
            Spacer(Modifier.height(24.dp))
        }
    }

    if (confirmLogout) {
        AlertDialog(
            onDismissRequest = { confirmLogout = false },
            title = { Text("Log out?") },
            text = { Text("You will need to log in again to send reports or use SOS.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmLogout = false
                    SessionManager.logout(context)
                    onLoggedOut()
                }) { Text("Log out", color = AlertRed) }
            },
            dismissButton = { TextButton(onClick = { confirmLogout = false }) { Text("Cancel") } }
        )
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text,
        modifier = Modifier.padding(start = 16.dp, top = 20.dp, bottom = 8.dp),
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = DeepBlue
    )
}

@Composable
private fun SettingSwitchRow(title: String, subtitle: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Text(subtitle, fontSize = 12.sp, color = TextSecondary)
        }
        Switch(checked = checked, onCheckedChange = onChange)
    }
    HorizontalDivider(color = Color(0xFFEDEFF3))
}