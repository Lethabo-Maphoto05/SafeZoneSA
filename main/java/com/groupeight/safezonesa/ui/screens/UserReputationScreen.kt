package com.groupeight.safezonesa.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.groupeight.safezonesa.data.MockRepository
import com.groupeight.safezonesa.ui.components.SafeZoneTopBar
import com.groupeight.safezonesa.ui.theme.*
import androidx.compose.ui.draw.clip

/**
 * Section 4.6: displays Community Guardian level, points earned, verified-report count,
 * and awarded badges. A gamification layer added beyond the brief to give confirmed
 * reports visible credibility and encourage consistent, truthful FR15 participation.
 */
@Composable
fun UserReputationScreen(onBack: () -> Unit) {
    val user = MockRepository.currentUser

    Column(Modifier.fillMaxSize().background(PageBackground)) {
        SafeZoneTopBar("User Reputation", onBack)

        Column(Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                Modifier.size(76.dp).clip(CircleShape).background(DeepBlue.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) { Icon(Icons.Filled.Person, contentDescription = null, tint = DeepBlue, modifier = Modifier.size(40.dp)) }
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(user.displayName, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Spacer(Modifier.width(4.dp))
                Icon(Icons.Filled.Verified, contentDescription = "Verified", tint = DeepBlue, modifier = Modifier.size(16.dp))
            }
            Text(user.guardianLevel, fontSize = 12.sp, color = TextSecondary)

            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                StatColumn(user.pointsEarned.toString(), "Points Earned")
                StatColumn(user.reportsVerified.toString(), "Reports Verified")
            }

            Spacer(Modifier.height(20.dp))
            Text("Recent Badges", fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                user.badges.forEach { badge -> BadgeItem(badge) }
            }
        }
    }
}

@Composable
private fun StatColumn(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = DeepBlue)
        Text(label, fontSize = 11.sp, color = TextSecondary)
    }
}

@Composable
private fun BadgeItem(label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier.size(52.dp).clip(RoundedCornerShape(14.dp)).background(BrightBlue.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) { Icon(Icons.Filled.EmojiEvents, contentDescription = label, tint = BrightBlue) }
        Spacer(Modifier.height(4.dp))
        Text(label, fontSize = 10.sp)
    }
}
