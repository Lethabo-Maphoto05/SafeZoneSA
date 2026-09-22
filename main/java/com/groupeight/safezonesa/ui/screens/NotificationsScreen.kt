package com.groupeight.safezonesa.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.groupeight.safezonesa.data.MockRepository
import com.groupeight.safezonesa.model.AppNotification
import com.groupeight.safezonesa.model.NotificationCategory
import com.groupeight.safezonesa.ui.components.SafeZoneTopBar
import com.groupeight.safezonesa.ui.theme.*
import androidx.compose.ui.draw.clip

/**
 * Section 4.5: FR12 — categorised notification centre (All / Alerts / Messages / System),
 * keeping urgent safety alerts visually distinct from ordinary app/system messages.
 */
@Composable
fun NotificationsScreen(onBack: () -> Unit) {
    var tab by remember { mutableStateOf("All") }
    val tabs = listOf("All", "Alerts", "Messages", "System")

    val filtered = MockRepository.notifications.filter {
        when (tab) {
            "Alerts" -> it.category == NotificationCategory.ALERT
            "Messages" -> it.category == NotificationCategory.MESSAGE
            "System" -> it.category == NotificationCategory.SYSTEM
            else -> true
        }
    }

    Column(Modifier.fillMaxSize().background(PageBackground)) {
        SafeZoneTopBar("Notifications", onBack)
        LazyRow(Modifier.padding(16.dp, 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(tabs) { t -> FilterChip(selected = tab == t, onClick = { tab = t }, label = { Text(t) }) }
        }
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(filtered) { n -> NotificationRow(n) }
        }
    }
}

@Composable
private fun NotificationRow(n: AppNotification) {
    val isAlert = n.category == NotificationCategory.ALERT
    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(36.dp).clip(CircleShape)
                    .background((if (isAlert) AlertRed else DeepBlue).copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (isAlert) Icons.Filled.Warning else Icons.Filled.Info,
                    contentDescription = null,
                    tint = if (isAlert) AlertRed else DeepBlue
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(n.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(n.body, fontSize = 12.sp, color = TextSecondary)
            }
            Text(n.timeAgo, fontSize = 11.sp, color = TextSecondary)
        }
    }
}
