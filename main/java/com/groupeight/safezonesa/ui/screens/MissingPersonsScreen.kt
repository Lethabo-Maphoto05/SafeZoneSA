package com.groupeight.safezonesa.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.groupeight.safezonesa.data.MockRepository
import com.groupeight.safezonesa.model.MissingPerson
import com.groupeight.safezonesa.ui.components.SafeZoneTopBar
import com.groupeight.safezonesa.ui.theme.*
import com.groupeight.safezonesa.util.ShareHelper

/**
 * Section 4.5: FR11 — missing-person cards with last-seen location/time, physical
 * description, phone number, and a working Share action that opens the Android share
 * sheet so the alert can be sent to other apps to raise awareness.
 */
@Composable
fun MissingPersonsScreen(onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().background(PageBackground)) {
        SafeZoneTopBar("Missing Persons", onBack)
        Text(
            "Help bring our loved ones home. Tap SHARE to send an alert to WhatsApp, Facebook, SMS or any other app.",
            fontSize = 12.sp, color = TextSecondary,
            modifier = Modifier.padding(16.dp, 12.dp, 16.dp, 4.dp)
        )
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(MockRepository.missingPersons) { person -> MissingPersonCard(person) }
        }
    }
}

@Composable
private fun MissingPersonCard(person: MissingPerson) {
    val context = LocalContext.current

    Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(Modifier.padding(14.dp)) {
            Box(
                Modifier.size(64.dp).clip(CircleShape).background(DeepBlue.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) { Icon(Icons.Filled.Person, contentDescription = null, tint = DeepBlue) }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(person.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text("Last seen: ${person.lastSeenLocation}", fontSize = 12.sp, color = TextSecondary)
                Text(person.lastSeenTime, fontSize = 12.sp, color = TextSecondary)
                Text(person.description, fontSize = 12.sp, color = TextSecondary)
                Text("Contact: ${person.contactNumber}", fontSize = 12.sp, color = TextSecondary)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { ShareHelper.shareMissingPerson(context, person) },
                        modifier = Modifier.weight(1f).height(38.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue)
                    ) { Text("SHARE", fontSize = 12.sp) }
                    OutlinedButton(
                        onClick = { ShareHelper.copyMissingPerson(context, person) },
                        modifier = Modifier.height(38.dp)
                    ) { Text("COPY", fontSize = 12.sp) }
                }
            }
        }
    }
}