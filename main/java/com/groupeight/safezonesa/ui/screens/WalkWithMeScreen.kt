package com.groupeight.safezonesa.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.Dash
import com.google.android.gms.maps.model.Gap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PatternItem
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.groupeight.safezonesa.data.MockRepository
import com.groupeight.safezonesa.ui.components.SafeZoneTopBar
import com.groupeight.safezonesa.ui.components.rememberLocationPermissionState
import com.groupeight.safezonesa.ui.theme.*

/**
 * Section 4.4: FR4 — live route to destination, the designated Guardian contact with
 * one-tap Message/Call, walk start time and expected arrival, and an End Walk control.
 * Server-side: auto-escalates to SOS if the walk times out, deviates, or stalls (FR4);
 * shake-to-alert is also always available.
 */
@Composable
fun WalkWithMeScreen(onBack: () -> Unit) {
    val walk = MockRepository.activeWalk
    val (locationGranted, requestLocation) = rememberLocationPermissionState()

    // Illustrative start/destination points around the mock incident area; replace with
    // the live GPS trail from PATCH /api/walks/{id}/location once the walk session is real.
    val start = LatLng(-26.1470, 27.8420)
    val destination = LatLng(-26.1390, 27.8500)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(-26.1430, 27.8460), 14f)
    }

    Column(Modifier.fillMaxSize().background(PageBackground)) {
        SafeZoneTopBar("Walk With Me", onBack)

        Box(
            Modifier.fillMaxWidth().height(220.dp).padding(16.dp).clip(RoundedCornerShape(16.dp))
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = locationGranted),
                uiSettings = MapUiSettings(myLocationButtonEnabled = false, zoomControlsEnabled = false, scrollGesturesEnabled = false)
            ) {
                Marker(state = MarkerState(position = start), title = "Start")
                Marker(state = MarkerState(position = destination), title = walk.destination)
                Polyline(
                    points = listOf(start, destination),
                    color = RoyalBlue,
                    width = 8f,
                    pattern = listOf(Dash(20f), Gap(10f))
                )
            }
            if (!locationGranted) {
                OutlinedButton(
                    onClick = requestLocation,
                    modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp)
                ) { Text("Enable location", fontSize = 10.sp) }
            }
        }

        Card(
            Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Your Guardian", fontSize = 12.sp, color = TextSecondary)
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Person, contentDescription = null, tint = DeepBlue)
                    Spacer(Modifier.width(8.dp))
                    Column(Modifier.weight(1f)) {
                        Text(walk.guardianName, fontWeight = FontWeight.Bold)
                        Text(walk.guardianNumber, fontSize = 12.sp, color = TextSecondary)
                    }
                    val context = androidx.compose.ui.platform.LocalContext.current
                    IconButton(onClick = {
                        val intent = android.content.Intent(android.content.Intent.ACTION_DIAL, android.net.Uri.parse("tel:${walk.guardianNumber}"))
                        context.startActivity(intent)
                    }) {
                        Icon(Icons.Filled.Call, contentDescription = "Call", tint = DeepBlue)
                    }
                    IconButton(onClick = {
                        val intent = android.content.Intent(android.content.Intent.ACTION_SENDTO, android.net.Uri.parse("smsto:${walk.guardianNumber}"))
                        context.startActivity(intent)
                    }) {
                        Icon(Icons.AutoMirrored.Filled.Message, contentDescription = "Message", tint = DeepBlue)
                    }
                }

                Spacer(Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(Modifier.height(12.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Walk in Progress", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("Start Time", fontSize = 11.sp, color = TextSecondary)
                        Text(walk.startTime, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Expected Arrival", fontSize = 11.sp, color = TextSecondary)
                        Text(walk.expectedArrival, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { /* TODO: POST /api/walks/{id}/complete */ },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue)
                ) { Text("END WALK") }
            }
        }
    }
}