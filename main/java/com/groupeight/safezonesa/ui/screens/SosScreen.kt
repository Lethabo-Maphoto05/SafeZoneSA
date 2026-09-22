package com.groupeight.safezonesa.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.groupeight.safezonesa.data.MockRepository
import com.groupeight.safezonesa.ui.components.rememberLocationPermissionState
import com.groupeight.safezonesa.ui.theme.AlertRed
import com.groupeight.safezonesa.ui.theme.AlertRedDark
import com.groupeight.safezonesa.ui.theme.Navy
import com.groupeight.safezonesa.viewmodel.SosState
import com.groupeight.safezonesa.viewmodel.SosViewModel

/**
 * Section 4.4: large, purposefully impossible-to-miss hold-to-send SOS control on a dark,
 * high-contrast background, with a plain-language checklist of what happens on activation.
 * Direct application of FR3. Wired to [SosViewModel], which POSTs to /api/sos (Section 5.3)
 * with the device's live location.
 */
@Composable
fun SosScreen(viewModel: SosViewModel = viewModel()) {
    var category by remember { mutableStateOf<String?>(null) }
    val state by viewModel.state.collectAsState()
    val (locationGranted, requestLocation) = rememberLocationPermissionState()

    LaunchedEffect(Unit) {
        if (!locationGranted) requestLocation()
    }

    Column(
        Modifier.fillMaxSize().background(Navy).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))
        Text("SOS Emergency", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(28.dp))

        val circleColor = when (state) {
            is SosState.Sent -> AlertRedDark
            is SosState.Sending -> AlertRedDark
            else -> AlertRed
        }

        Box(
            modifier = Modifier
                .size(180.dp)
                .clip(CircleShape)
                .background(circleColor)
                .clickable(enabled = state !is SosState.Sending) {
                    viewModel.sendSos(category ?: "Other")
                },
            contentAlignment = Alignment.Center
        ) {
            when (state) {
                is SosState.Sending -> CircularProgressIndicator(color = Color.White)
                is SosState.Sent -> Text("SOS SENT", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp, textAlign = TextAlign.Center)
                else -> Text("TAP TO\nSEND SOS", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp, textAlign = TextAlign.Center)
            }
        }

        val failure = state as? SosState.Failed
        if (failure != null) {
            Spacer(Modifier.height(16.dp))
            Text(failure.message, color = Color.White, fontSize = 12.sp, textAlign = TextAlign.Center)
        }

        Spacer(Modifier.height(28.dp))
        Text("Emergency category", color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
        Spacer(Modifier.height(8.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(MockRepository.emergencyCategories) { cat ->
                FilterChip(
                    selected = category == cat,
                    onClick = { category = cat },
                    label = { Text(cat, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color.White,
                        selectedLabelColor = AlertRed,
                        labelColor = Color.White,
                        containerColor = Color.White.copy(alpha = 0.1f)
                    )
                )
            }
        }

        Spacer(Modifier.height(28.dp))
        ChecklistLine("Your location will be shared")
        ChecklistLine("Emergency contacts will be notified")
        ChecklistLine("Nearby community members alerted")
        ChecklistLine("Emergency SMS will be sent")
    }
}

@Composable
private fun ChecklistLine(text: String) {
    Row(Modifier.padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(text, color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp)
    }
}
