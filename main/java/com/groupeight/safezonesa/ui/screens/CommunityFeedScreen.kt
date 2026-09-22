package com.groupeight.safezonesa.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.groupeight.safezonesa.data.MockRepository
import com.groupeight.safezonesa.model.Incident
import com.groupeight.safezonesa.model.IncidentStatus
import com.groupeight.safezonesa.ui.theme.*
import com.groupeight.safezonesa.viewmodel.IncidentsViewModel

/**
 * Section 4.3: FR2 — lists incident reports, filterable by category, with reporter info,
 * photo proof, severity/category tag, like/comment/share, and the 10-confirmation status.
 * Backed by GET /api/incidents, POST /api/incidents and PATCH /api/incidents/{id}/confirm
 * (Section 5.3) via [IncidentsViewModel].
 */
@Composable
fun CommunityFeedScreen(viewModel: IncidentsViewModel = viewModel()) {
    var selectedFilter by remember { mutableStateOf("All") }
    val uiState by viewModel.uiState.collectAsState()

    var showNewPost by remember { mutableStateOf(false) }
    var posting by remember { mutableStateOf(false) }
    var postError by remember { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxSize().background(PageBackground)) {
        Row(
            Modifier.fillMaxWidth().padding(16.dp, 16.dp, 16.dp, 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Community Feed", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            IconButton(onClick = { showNewPost = true }) {
                Icon(Icons.Filled.AddCircle, contentDescription = "New post", tint = DeepBlue)
            }
        }

        LazyRow(
            Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(MockRepository.feedFilters) { filter ->
                FilterChip(
                    selected = filter == selectedFilter,
                    onClick = { selectedFilter = filter },
                    label = { Text(filter) }
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        when {
            uiState.isLoading -> {
                Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = DeepBlue)
                }
            }
            uiState.errorMessage != null -> {
                Column(
                    Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(uiState.errorMessage!!, color = AlertRed, fontSize = 13.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { viewModel.loadIncidents() }) { Text("Retry") }
                }
            }
            uiState.incidents.isEmpty() -> {
                Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("No incidents reported yet.", color = TextSecondary, fontSize = 13.sp)
                }
            }
            else -> {
                val visible = uiState.incidents.filter {
                    selectedFilter == "All" || it.category.contains(selectedFilter, ignoreCase = true)
                }
                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(visible, key = { it.incidentId }) { incident ->
                        IncidentCard(incident, onConfirm = { viewModel.confirmIncident(incident.incidentId) })
                    }
                }
            }
        }
    }

    if (showNewPost) {
        NewPostDialog(
            defaultAnonymous = false,
            isSubmitting = posting,
            errorMessage = postError,
            onDismiss = { showNewPost = false; postError = null },
            onSubmit = { category, title, desc, anon ->
                posting = true
                postError = null
                viewModel.postIncident(category, title, desc, anon) { error ->
                    posting = false
                    if (error == null) showNewPost = false else postError = error
                }
            }
        )
    }
}

@Composable
private fun IncidentCard(incident: Incident, onConfirm: () -> Unit) {
    Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(36.dp).background(DeepBlue.copy(alpha = 0.12f), RoundedCornerShape(18.dp)),
                    contentAlignment = Alignment.Center
                ) { Icon(Icons.Filled.Person, contentDescription = null, tint = DeepBlue) }
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(incident.reporterDisplayName ?: "Anonymous", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("${incident.category} · ${incident.timeAgo}", fontSize = 11.sp, color = TextSecondary)
                }
                Spacer(Modifier.weight(1f))
                AssistChip(
                    onClick = {},
                    label = { Text(if (incident.status == IncidentStatus.VERIFIED) "Verified" else "Unverified", fontSize = 10.sp) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (incident.status == IncidentStatus.VERIFIED) RiskLow.copy(alpha = 0.15f) else RiskMedium.copy(alpha = 0.15f)
                    )
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(incident.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(Modifier.height(4.dp))
            Text(incident.description, fontSize = 13.sp, color = TextSecondary)
            Spacer(Modifier.height(10.dp))
            Text(
                "${incident.confirmationCount}/${incident.confirmationsRequired} confirmations",
                fontSize = 11.sp, color = DeepBlue, fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                ActionIcon(Icons.Filled.ThumbUp, "Like")
                ActionIcon(Icons.Filled.ChatBubbleOutline, "Comment")
                ActionIcon(Icons.Filled.Share, "Share")
                ActionIcon(
                    Icons.Filled.CheckCircleOutline,
                    "Confirm",
                    modifier = Modifier.clickable(
                        enabled = incident.status != IncidentStatus.VERIFIED,
                        onClick = onConfirm
                    )
                )
            }
        }
    }
}

@Composable
private fun ActionIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = label, tint = TextSecondary, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(4.dp))
        Text(label, fontSize = 11.sp, color = TextSecondary)
    }
}