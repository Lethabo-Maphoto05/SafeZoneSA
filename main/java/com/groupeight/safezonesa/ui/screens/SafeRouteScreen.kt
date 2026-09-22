package com.groupeight.safezonesa.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.groupeight.safezonesa.ui.components.SafeZoneTopBar
import com.groupeight.safezonesa.ui.theme.*

/**
 * Section 4.4: FR13 — From/To input returning the suggested safest route with estimated
 * time/distance, clearly stating what the route avoids (recent robberies, high-crime
 * areas, unsafe roads) rather than always the shortest path.
 */
@Composable
fun SafeRouteScreen(onBack: () -> Unit) {
    var from by remember { mutableStateOf("Campus Residence") }
    var to by remember { mutableStateOf("Protea Glen Ext 12") }

    Column(Modifier.fillMaxSize().background(PageBackground).padding(16.dp)) {
        SafeZoneTopBar("Safe Route", onBack)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(from, { from = it }, label = { Text("From") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(to, { to = it }, label = { Text("To") }, modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.height(16.dp))
        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(Modifier.padding(16.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Safest Route", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = RiskLow)
                }
                Text("12 min (5.4 km)", fontSize = 13.sp, color = TextSecondary)
                Text("Recommended", fontSize = 12.sp, color = RiskLow, fontWeight = FontWeight.Bold)

                Spacer(Modifier.height(14.dp))
                Text("Avoiding", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                AvoidLine("Recent Robberies")
                AvoidLine("High Crime Areas")
                AvoidLine("Unsafe Roads")

                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { /* TODO: GET /api/... route calc against Google Directions API + verified incidents (Section 5.2/5.4) */ },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue)
                ) { Text("START NAVIGATION") }
            }
        }
    }
}

@Composable
private fun AvoidLine(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 3.dp)) {
        Icon(Icons.Filled.Cancel, contentDescription = null, tint = AlertRed, modifier = Modifier.size(14.dp))
        Spacer(Modifier.width(6.dp))
        Text(text, fontSize = 12.sp, color = TextSecondary)
    }
}
