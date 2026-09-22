package com.groupeight.safezonesa.ui.screens

import com.groupeight.safezonesa.data.SessionManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.groupeight.safezonesa.data.MockRepository
import com.groupeight.safezonesa.model.Incident
import com.groupeight.safezonesa.ui.theme.*
import androidx.compose.ui.platform.LocalContext


/**
 * Section 4.3: default post-login screen. Greets the user, shows local weather, a one-tap
 * SOS shortcut, a quick-access grid, Nearby Alerts, and recent community posts
 * (combines FR3, FR7, FR12, FR18 into one overview).
 */
@Composable
fun HomeDashboardScreen(
    onSos: () -> Unit,
    onWalkWithMe: () -> Unit,
    onCrimeMap: () -> Unit,
    onDirectory: () -> Unit,
    onChat: () -> Unit,
    onNotifications: () -> Unit
) {
    val user = MockRepository.currentUser

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBackground)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val name = SessionManager.getDisplayName(LocalContext.current)?.takeIf { it.isNotBlank() } ?: user.displayName
            Text("Good Evening, ${name.substringBefore(' ')}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            IconButton(onClick = onNotifications) {
                Icon(Icons.Filled.Notifications, contentDescription = "Notifications", tint = DeepBlue)
            }
        }
        Spacer(Modifier.height(12.dp))

        // Weather card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = BrightBlue.copy(alpha = 0.12f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("18°C", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Text("Partly Cloudy · H: 23° L: 17°", fontSize = 12.sp, color = TextSecondary)
                }
                Icon(Icons.Filled.WbCloudy, contentDescription = null, tint = BrightBlue, modifier = Modifier.size(36.dp))
            }
        }

        Spacer(Modifier.height(16.dp))

        // FR3: one-tap SOS shortcut
        Button(
            onClick = onSos,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AlertRed),
            shape = RoundedCornerShape(28.dp)
        ) {
            Icon(Icons.Filled.Warning, contentDescription = null, tint = Color.White)
            Spacer(Modifier.width(8.dp))
            Text("SOS Emergency", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(Modifier.height(16.dp))

        // Quick-access grid: Walk With Me, Crime Map, Directory, Chat
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            QuickAccessItem("Walk With Me", Icons.AutoMirrored.Filled.DirectionsWalk, onWalkWithMe)
            QuickAccessItem("Crime Map", Icons.Filled.Map, onCrimeMap)
            QuickAccessItem("Directory", Icons.Filled.ContactPhone, onDirectory)
            QuickAccessItem("Chat", Icons.AutoMirrored.Filled.Chat, onChat)
        }

        Spacer(Modifier.height(20.dp))
        Text("Nearby Alerts", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Spacer(Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(MockRepository.incidents) { incident -> NearbyAlertCard(incident) }
        }

        Spacer(Modifier.height(20.dp))
        Text("Recent Community Posts", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun QuickAccessItem(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable(onClick = onClick)) {
        Box(
            modifier = Modifier.size(52.dp).clip(CircleShape).background(DeepBlue.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = DeepBlue)
        }
        Spacer(Modifier.height(4.dp))
        Text(label, fontSize = 11.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
    }
}

@Composable
private fun NearbyAlertCard(incident: Incident) {
    Card(
        modifier = Modifier.width(220.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Warning, contentDescription = null, tint = AlertRed, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(incident.category, fontSize = 12.sp, color = AlertRed, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(4.dp))
            Text(incident.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 2)
            Spacer(Modifier.height(4.dp))
            Text(incident.timeAgo, fontSize = 11.sp, color = TextSecondary)
        }
    }
}