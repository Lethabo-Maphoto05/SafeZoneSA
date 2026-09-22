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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.groupeight.safezonesa.data.MockRepository
import com.groupeight.safezonesa.model.VolunteerResponder
import com.groupeight.safezonesa.ui.components.SafeZoneTopBar
import com.groupeight.safezonesa.ui.theme.*
import androidx.compose.ui.draw.clip

/**
 * Section 4.6: FR17 — lists local active CPF patrollers, first-aid volunteers, and
 * security firms with a Request Assistance action and estimated distance/time away.
 */
@Composable
fun VolunteerRespondersScreen(onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().background(PageBackground)) {
        SafeZoneTopBar("Volunteer Responders", onBack)
        Text(
            "Active Responders Nearby",
            fontSize = 13.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp, 12.dp, 16.dp, 4.dp)
        )
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(MockRepository.volunteerResponders) { r -> ResponderRow(r) }
        }
        Button(
            onClick = { /* TODO: POST assistance request tied to nearest responder(s) */ },
            modifier = Modifier.fillMaxWidth().padding(16.dp).height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue)
        ) { Text("REQUEST ASSISTANCE") }
    }
}

@Composable
private fun ResponderRow(r: VolunteerResponder) {
    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(40.dp).clip(CircleShape).background(DeepBlue.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) { Icon(Icons.Filled.Person, contentDescription = null, tint = DeepBlue) }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(r.type, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(r.name, fontSize = 12.sp, color = TextSecondary)
            }
            Text(r.distanceAway, fontSize = 12.sp, color = DeepBlue, fontWeight = FontWeight.Bold)
        }
    }
}
