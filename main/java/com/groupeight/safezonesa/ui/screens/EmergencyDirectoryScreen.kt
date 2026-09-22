package com.groupeight.safezonesa.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.groupeight.safezonesa.data.MockRepository
import com.groupeight.safezonesa.model.DirectoryEntry
import com.groupeight.safezonesa.ui.components.SafeZoneTopBar
import com.groupeight.safezonesa.ui.theme.*

/**
 * Section 4.5: FR18 — one-tap access to SAPS Emergency, Ambulance, Fire Department,
 * Campus Security, Community Security, Traffic Department, plus a searchable local
 * ward/municipal/CPF directory.
 */
@Composable
fun EmergencyDirectoryScreen(onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().background(PageBackground)) {
        SafeZoneTopBar("Emergency Directory", onBack)
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(MockRepository.directory) { entry -> DirectoryRow(entry) }
        }
    }
}

@Composable
private fun DirectoryRow(entry: DirectoryEntry) {
    val context = androidx.compose.ui.platform.LocalContext.current
    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(
            Modifier.fillMaxWidth().padding(16.dp, 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(entry.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(entry.number, fontSize = 12.sp, color = TextSecondary)
            }
            IconButton(
                onClick = {
                    // ACTION_DIAL opens the dialer pre-filled — no CALL_PHONE runtime
                    // permission needed, unlike ACTION_CALL which places the call directly.
                    val intent = android.content.Intent(
                        android.content.Intent.ACTION_DIAL,
                        android.net.Uri.parse("tel:${entry.number}")
                    )
                    context.startActivity(intent)
                },
                modifier = Modifier.background(DeepBlue, RoundedCornerShape(50))
            ) { Icon(Icons.Filled.Call, contentDescription = "Call ${entry.name}", tint = Color.White) }
        }
    }
}
