package com.groupeight.safezonesa.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.groupeight.safezonesa.data.MockRepository
import com.groupeight.safezonesa.model.ChatMessage
import com.groupeight.safezonesa.ui.theme.*

/**
 * Section 4.5: FR10 — neighbourhood-specific group chat, admin-moderated, used for daily
 * updates and confirming/explaining reported incidents in real time.
 */
@Composable
fun CommunityChatScreen() {
    var draft by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf(*MockRepository.chatMessages.toTypedArray()) }

    Column(Modifier.fillMaxSize().background(PageBackground)) {
        Column(Modifier.padding(16.dp, 12.dp)) {
            Text("Soweto Community", fontWeight = FontWeight.Bold, fontSize = 17.sp)
            Text("256 members, 18 online", fontSize = 12.sp, color = TextSecondary)
        }

        LazyColumn(
            modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages) { msg -> ChatBubble(msg) }
        }

        Row(
            Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = draft,
                onValueChange = { draft = it },
                placeholder = { Text("Type a message...") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp)
            )
            Spacer(Modifier.width(8.dp))
            IconButton(
                onClick = {
                    if (draft.isNotBlank()) {
                        messages.add(ChatMessage("m-${messages.size}", "You", draft, "just now", isOwn = true))
                        draft = ""
                    }
                },
                modifier = Modifier.background(DeepBlue, RoundedCornerShape(50))
            ) { Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White) }
        }
    }
}

@Composable
private fun ChatBubble(msg: ChatMessage) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = if (msg.isOwn) Arrangement.End else Arrangement.Start) {
        Column(horizontalAlignment = if (msg.isOwn) Alignment.End else Alignment.Start) {
            if (!msg.isOwn) Text(msg.senderName, fontSize = 11.sp, color = TextSecondary)
            Box(
                Modifier
                    .background(if (msg.isOwn) DeepBlue else Color.White, RoundedCornerShape(14.dp))
                    .padding(12.dp)
            ) {
                Text(msg.body, color = if (msg.isOwn) Color.White else TextPrimary, fontSize = 13.sp)
            }
            Text(msg.timeAgo, fontSize = 10.sp, color = TextSecondary)
        }
    }
}