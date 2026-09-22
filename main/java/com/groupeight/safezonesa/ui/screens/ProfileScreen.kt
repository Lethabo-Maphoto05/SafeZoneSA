package com.groupeight.safezonesa.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.groupeight.safezonesa.data.MockRepository
import com.groupeight.safezonesa.data.SessionManager
import com.groupeight.safezonesa.ui.theme.*

/**
 * Profile tab — the fifth bottom-nav destination (Section 4.7). Surfaces the account and
 * the screens grouped under "Trust, Gamification and AI" (Section 4.6) plus supporting
 * screens (Directory, Volunteer Responders), Settings and Log Out.
 */
@Composable
fun ProfileScreen(
    onReputation: () -> Unit,
    onAiPrediction: () -> Unit,
    onVolunteerResponders: () -> Unit,
    onEmergencyContacts: () -> Unit,
    onMissingPersons: () -> Unit,
    onSettings: () -> Unit,
    onLoggedOut: () -> Unit
) {
    val context = LocalContext.current
    val name = SessionManager.getDisplayName(context)?.takeIf { it.isNotBlank() }
        ?: MockRepository.currentUser.displayName
    val email = SessionManager.getEmail(context)?.takeIf { it.isNotBlank() }
        ?: MockRepository.currentUser.email

    Column(Modifier.fillMaxSize().background(PageBackground)) {
        Column(
            Modifier.fillMaxWidth().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                Modifier.size(72.dp).clip(CircleShape).background(DeepBlue.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) { Icon(Icons.Filled.Person, contentDescription = null, tint = DeepBlue, modifier = Modifier.size(36.dp)) }
            Spacer(Modifier.height(8.dp))
            Text(name, fontWeight = FontWeight.Bold, fontSize = 17.sp)
            Text(email, fontSize = 12.sp, color = TextSecondary)
        }

        ProfileRow("User Reputation", Icons.Filled.EmojiEvents, onReputation)
        ProfileRow("AI Crime Prediction", Icons.Filled.Insights, onAiPrediction)
        ProfileRow("Volunteer Responders", Icons.Filled.VolunteerActivism, onVolunteerResponders)
        ProfileRow("Emergency Contacts", Icons.Filled.ContactPhone, onEmergencyContacts)
        ProfileRow("Missing Persons", Icons.Filled.PersonSearch, onMissingPersons)
        ProfileRow("Settings", Icons.Filled.Settings, onSettings)
        ProfileRow("Log Out", Icons.AutoMirrored.Filled.Logout) {
            SessionManager.logout(context)
            onLoggedOut()
        }
    }
}

@Composable
private fun ProfileRow(label: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onClick).padding(20.dp, 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = DeepBlue)
        Spacer(Modifier.width(14.dp))
        Text(label, modifier = Modifier.weight(1f), fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = TextSecondary)
    }
    HorizontalDivider(color = Color(0xFFEDEFF3))
}