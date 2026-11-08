package com.example.streambridge.ui.screens.ask

// For host


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import com.example.streambridge.viewmodel.InteractionViewModel


private val Background = Color(0xFF080B18)
private val CardColor = Color(0xFF12182A)
private val Primary = Color(0xFF8B5CF6)
private val Green = Color(0xFF22C55E)
private val Red = Color(0xFFEF4444)
private val TextPrimary = Color.White
private val TextSecondary = Color(0xFF9CA3B5)

@Composable
fun AskRequestsScreen(
    sessionId: String,
    ownerId: String,
    isOwner: Boolean,
    interactionViewModel: InteractionViewModel,
    onBack: () -> Unit
) {
    val requests by interactionViewModel.askRequests.collectAsState()
    val isLoading by interactionViewModel.isLoading.collectAsState()
    val isSubmitting by interactionViewModel.isSubmitting.collectAsState()
    val error by interactionViewModel.error.collectAsState()
    val successMessage by interactionViewModel.successMessage.collectAsState()

    var selectedStatus by remember(sessionId) {
        mutableStateOf("ALL")
    }

    LaunchedEffect(sessionId, selectedStatus) {
        interactionViewModel.loadAskRequests(
            sessionId = sessionId,
            status = selectedStatus.takeUnless { it == "ALL" }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(18.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
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
                    text = "Ask Requests",
                    color = TextPrimary,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = if (isOwner) "Manage audience requests" else "View requests",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }

            Icon(
                imageVector = Icons.Default.QuestionAnswer,
                contentDescription = null,
                tint = Primary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("ALL", "PENDING", "ACCEPTED", "REJECTED").forEach { status ->
                FilterChip(
                    selected = selectedStatus == status,
                    onClick = { selectedStatus = status },
                    label = {
                        Text(
                            text = status.replaceFirstChar { it.uppercase() },
                            fontSize = 11.sp
                        )
                    }
                )
            }
        }

        error?.let { message ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = message, color = Color.Red)
        }

        successMessage?.let { message ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = message, color = Primary)
        }

        Spacer(modifier = Modifier.height(16.dp))

        when {
            isLoading && requests.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Primary)
                }
            }

            requests.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No requests found.",
                        color = TextSecondary
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = requests,
                        key = { it.id }
                    ) { request ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            color = CardColor
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "User: ${request.userId}",
                                        modifier = Modifier.weight(1f),
                                        color = TextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )

                                    Text(
                                        text = request.status,
                                        color = when (request.status) {
                                            "ACCEPTED" -> Green
                                            "REJECTED" -> Color.Red
                                            else -> Primary
                                        },
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                if (!request.message.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(12.dp))

                                    Text(
                                        text = request.message,
                                        color = TextPrimary,
                                        fontSize = 14.sp,
                                        lineHeight = 20.sp
                                    )
                                }

                                if (isOwner && ownerId.isNotBlank() &&
                                    request.status == "PENDING"
                                ) {
                                    Spacer(modifier = Modifier.height(16.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Button(
                                            onClick = {
                                                interactionViewModel.acceptAsk(
                                                    askId = request.id,
                                                    ownerId = ownerId,
                                                    sessionId = sessionId
                                                )
                                            },
                                            modifier = Modifier.weight(1f),
                                            enabled = !isSubmitting
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Accept")
                                        }

                                        OutlinedButton(
                                            onClick = {
                                                interactionViewModel.rejectAsk(
                                                    askId = request.id,
                                                    ownerId = ownerId,
                                                    sessionId = sessionId
                                                )
                                            },
                                            modifier = Modifier.weight(1f),
                                            enabled = !isSubmitting
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = null
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Reject")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}