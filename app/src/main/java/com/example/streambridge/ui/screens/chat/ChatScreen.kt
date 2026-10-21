package com.example.streambridge.ui.screens.chat



import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Arrangement
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
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.streambridge.viewmodel.InteractionViewModel

private val Background = Color(0xFF080B18)
private val CardColor = Color(0xFF12182A)
private val Primary = Color(0xFF3B82F6)
private val TextPrimary = Color.White
private val TextSecondary = Color(0xFF9CA3B5)

@Composable
fun ChatScreen(
    sessionId: String,
    userId: String,
    interactionViewModel: InteractionViewModel,
    onBack: () -> Unit
) {
    val comments by interactionViewModel.comments.collectAsState()
    val isLoading by interactionViewModel.isLoading.collectAsState()
    val isSubmitting by interactionViewModel.isSubmitting.collectAsState()
    val error by interactionViewModel.error.collectAsState()
    val successMessage by interactionViewModel.successMessage.collectAsState()

    var message by remember(sessionId) { mutableStateOf("") }

    LaunchedEffect(sessionId) {
        interactionViewModel.loadComments(sessionId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
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

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Live Chat",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Session comments",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }

            IconButton(
                onClick = { interactionViewModel.loadComments(sessionId) }
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh comments",
                    tint = Primary
                )
            }
        }

        if (error != null) {
            Text(
                text = error.orEmpty(),
                color = Color.Red,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }

        if (successMessage != null) {
            Text(
                text = successMessage.orEmpty(),
                color = Primary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }

        when {
            isLoading && comments.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Primary)
                }
            }

            comments.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No comments yet. Start the conversation!",
                        color = TextSecondary
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = comments,
                        key = { it.id }
                    ) { comment ->
                        val isMe = comment.userId == userId

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (isMe) {
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
                                        if (isMe) {
                                            Primary.copy(alpha = 0.25f)
                                        } else {
                                            CardColor
                                        }
                                    )
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = if (isMe) "You" else comment.userId,
                                    color = if (isMe) Primary else TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = comment.message,
                                    color = TextPrimary,
                                    fontSize = 14.sp
                                )

                                if (isMe) {
                                    TextButton(
                                        onClick = {
                                            interactionViewModel.deleteComment(
                                                commentId = comment.id,
                                                userId = userId,
                                                sessionId = sessionId
                                            )
                                        },
                                        enabled = !isSubmitting
                                    ) {
                                        Text(
                                            text = "Delete",
                                            color = Color.Red
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = message,
                onValueChange = {
                    if (it.length <= 1000) message = it
                },
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        text = "Type a message...",
                        color = TextSecondary
                    )
                },
                supportingText = {
                    Text(
                        text = "${message.length}/1000",
                        color = TextSecondary
                    )
                },
                shape = RoundedCornerShape(18.dp),
                maxLines = 4
            )

            Spacer(modifier = Modifier.size(8.dp))

            IconButton(
                onClick = {
                    interactionViewModel.sendComment(
                        sessionId = sessionId,
                        userId = userId,
                        message = message,
                        onSuccess = { message = "" }
                    )
                },
                enabled = message.isNotBlank() &&
                        message.length <= 1000 &&
                        userId.isNotBlank() &&
                        !isSubmitting,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Primary)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send comment",
                        tint = Color.White
                    )
                }
            }
        }
    }
}