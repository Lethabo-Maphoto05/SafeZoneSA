package com.groupeight.safezonesa.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.groupeight.safezonesa.ui.components.SafeZoneTopBar
import com.groupeight.safezonesa.ui.theme.PageBackground
import com.groupeight.safezonesa.ui.theme.TextSecondary

/** Section 4.5: FR8 — categorised quick-report list for non-crime hazards, in a single press. */
@Composable
fun BewareScreen(onBack: () -> Unit) {
    data class BewareCategory(val label: String, val icon: ImageVector)

    val categories = listOf(
        BewareCategory("Dangerous Road", Icons.Filled.Warning),
        BewareCategory("Suspicious Vehicle", Icons.Filled.DirectionsCar),
        BewareCategory("Unsafe Taxi", Icons.Filled.LocalTaxi),
        BewareCategory("Broken Streetlight", Icons.Filled.Lightbulb),
        BewareCategory("Pothole", Icons.Filled.ReportProblem),
        BewareCategory("Flood", Icons.Filled.Water),
        BewareCategory("Hijacking Hotspot", Icons.Filled.Warning)
    )

    Column(Modifier.fillMaxSize().background(PageBackground)) {
        SafeZoneTopBar("Beware", onBack)
        Text(
            "Report dangerous or unsafe situations",
            fontSize = 12.sp, color = TextSecondary,
            modifier = Modifier.padding(16.dp, 12.dp, 16.dp, 4.dp)
        )
        LazyColumn {
            items(categories) { cat ->
                Row(
                    Modifier.fillMaxWidth()
                        .clickable { /* TODO: capture photo + GPS pin + description, POST as a Beware report (FR8) */ }
                        .padding(16.dp, 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(cat.icon, contentDescription = null, tint = TextSecondary)
                    Spacer(Modifier.width(14.dp))
                    Text(cat.label, modifier = Modifier.weight(1f), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = TextSecondary)
                }
                HorizontalDivider(color = Color(0xFFEDEFF3))
            }
        }
    }
}