package com.groupeight.safezonesa.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.groupeight.safezonesa.data.MockRepository
import com.groupeight.safezonesa.model.NeighbourhoodRisk
import com.groupeight.safezonesa.model.RiskLevel
import com.groupeight.safezonesa.ui.components.SafeZoneTopBar
import com.groupeight.safezonesa.ui.theme.*

/**
 * Section 4.6: short-term, area-based risk forecast built on recent verified incident
 * patterns (FR14 Suburb Safety Score, forward-looking). Follows the FR19 classification-
 * not-accusation philosophy — it never characterises the risk level of an individual.
 */
@Composable
fun AiCrimePredictionScreen(onBack: () -> Unit) {
    val risks = MockRepository.neighbourhoodRisks
    val highest = risks.firstOrNull { it.riskLevel == RiskLevel.HIGH }

    Column(Modifier.fillMaxSize().background(PageBackground)) {
        SafeZoneTopBar("AI Crime Prediction", onBack)
        Text(
            "Areas at risk tonight",
            fontSize = 12.sp, color = TextSecondary,
            modifier = Modifier.padding(16.dp, 12.dp, 16.dp, 4.dp)
        )

        if (highest != null) {
            Card(
                Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = RiskHigh)
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("High Risk", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(highest.neighbourhoodName, color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp)
                        Text(highest.trendDescription, color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp)
                    }
                    Icon(Icons.Filled.Warning, contentDescription = null, tint = Color.White)
                }
            }
        }

        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(risks.filter { it != highest }) { risk -> RiskRow(risk) }
        }
    }
}

@Composable
private fun RiskRow(risk: NeighbourhoodRisk) {
    val color = when (risk.riskLevel) {
        RiskLevel.HIGH -> RiskHigh
        RiskLevel.MEDIUM -> RiskMedium
        RiskLevel.LOW -> RiskLow
    }
    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(10.dp).background(color, RoundedCornerShape(5.dp)))
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(risk.neighbourhoodName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(risk.trendDescription, fontSize = 12.sp, color = TextSecondary)
            }
            Text(risk.riskLevel.name.lowercase().replaceFirstChar { it.uppercase() }, fontSize = 12.sp, color = color, fontWeight = FontWeight.Bold)
        }
    }
}
