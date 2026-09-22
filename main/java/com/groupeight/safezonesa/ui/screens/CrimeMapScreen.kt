package com.groupeight.safezonesa.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.groupeight.safezonesa.data.MockRepository
import com.groupeight.safezonesa.model.Severity
import com.groupeight.safezonesa.ui.components.rememberLocationPermissionState
import com.groupeight.safezonesa.ui.theme.*

/**
 * Section 4.3: FR7 filterable crime map (Today / This Week / This Month), with pins for
 * incidents, police stations, and hospitals, feeding into the FR14 Suburb Safety Score.
 *
 * Renders a live Google Map (maps-compose) seeded with the mock incident pins from
 * Section 5's example data. Wire GET /api/map/pins?lat=&lng=&radius=&range= (Section 5.3)
 * in place of MockRepository.incidents once the API is live, and set a real
 * @string/google_maps_key (Section 5.4) before running on a device.
 */
@Composable
fun CrimeMapScreen() {
    var selectedRange by remember { mutableStateOf("Today") }
    val ranges = listOf("Today", "This Week", "This Month")
    val (locationGranted, requestLocation) = rememberLocationPermissionState()

    // Centred roughly over the Soweto / Protea Glen area used by the mock incident data.
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(-26.1420, 27.8470), 13f)
    }

    Column(Modifier.fillMaxSize().background(PageBackground)) {
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Crime Map", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                ranges.forEach { range ->
                    FilterChip(selected = range == selectedRange, onClick = { selectedRange = range }, label = { Text(range, fontSize = 11.sp) })
                }
            }
        }

        Box(
            Modifier.fillMaxWidth().weight(1f).padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(16.dp))
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = locationGranted),
                uiSettings = MapUiSettings(myLocationButtonEnabled = locationGranted, zoomControlsEnabled = false)
            ) {
                MockRepository.incidents.forEach { incident ->
                    val hue = when (incident.severity) {
                        Severity.HIGH, Severity.CRITICAL -> BitmapDescriptorFactory.HUE_RED
                        Severity.MEDIUM -> BitmapDescriptorFactory.HUE_ORANGE
                        Severity.LOW -> BitmapDescriptorFactory.HUE_YELLOW
                    }
                    Marker(
                        state = MarkerState(position = LatLng(incident.latitude, incident.longitude)),
                        title = incident.title,
                        snippet = "${incident.category} · ${incident.status.name.lowercase()}",
                        icon = BitmapDescriptorFactory.defaultMarker(hue)
                    )
                }
            }

            if (!locationGranted) {
                OutlinedButton(
                    onClick = requestLocation,
                    modifier = Modifier.align(Alignment.BottomEnd).padding(12.dp)
                ) { Text("Enable location", fontSize = 11.sp) }
            }
        }

        Spacer(Modifier.height(12.dp))

        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            LegendItem(Icons.Filled.Whatshot, "Incidents", AlertRed)
            LegendItem(Icons.Filled.Whatshot, "Hotspots", RiskMedium)
            LegendItem(Icons.Filled.LocalPolice, "Police", DeepBlue)
            LegendItem(Icons.Filled.LocalHospital, "Hospitals", RiskLow)
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun LegendItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = label, tint = color)
        Text(label, fontSize = 10.sp, color = TextSecondary)
    }
}
