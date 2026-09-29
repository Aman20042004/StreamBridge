package com.example.streambridge.ui.screens.chat



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Background = Color(0xFF080B18)
private val CardColor = Color(0xFF12182A)
private val Primary = Color(0xFF3B82F6)
private val TextPrimary = Color.White
private val TextSecondary = Color(0xFF9CA3B5)

data class ChatMessage(
    val name: String,
    val message: String,
    val isMe: Boolean
)

@Composable
fun ChatScreen(
    onBack: () -> Unit
) {
    val message = remember { mutableStateOf("") }

    val messages = listOf(
        ChatMessage(
            name = "Rahul",
            message = "Can you explain coroutines?",
            isMe = false
        ),
        ChatMessage(
            name = "Priya",
            message = "Yes, that would be helpful!",
            isMe = false
        ),
        ChatMessage(
            name = "You",
            message = "Sure, let's discuss it.",
            isMe = true
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {

        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }

            Column {
                Text(
                    text = "Live Chat",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Kotlin Study Group",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        // Messages
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            reverseLayout = false
        ) {
            items(messages) { chat ->

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (chat.isMe) {
                        Arrangement.End
                    } else {
                        Arrangement.Start
                    }
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth(0.82f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (chat.isMe) {
                                    Primary.copy(alpha = 0.25f)
                                } else {
                                    CardColor
                                }
                            )
                            .padding(12.dp)
                    ) {

                        Text(
                            text = chat.name,
                            color = if (chat.isMe) {
                                Primary
                            } else {
                                TextSecondary
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = chat.message,
                            color = TextPrimary,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // Message Input
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            OutlinedTextField(
                value = message.value,
                onValueChange = { message.value = it },
                modifier = Modifier.weight(1f),
                singleLine = true,
                placeholder = {
                    Text(
                        text = "Type a message...",
                        color = TextSecondary
                    )
                },
                shape = RoundedCornerShape(18.dp)
            )

            Spacer(modifier = Modifier.size(8.dp))

            IconButton(
                onClick = {
                    if (message.value.isNotBlank()) {
                        message.value = ""
                    }
                },
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Primary)
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send",
                    tint = Color.White
                )
            }
        }
    }
}