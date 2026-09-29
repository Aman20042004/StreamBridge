package com.example.streambridge.ui.screens.participants



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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
private val CardColor = Color(0xFF11162A)
private val TextPrimary = Color.White
private val TextSecondary = Color(0xFF9CA3B5)
private val NeonBlue = Color(0xFF3B82F6)
private val Green = Color(0xFF22C55E)

data class Participant(
    val name: String,
    val role: String,
    val isMuted: Boolean
)

@Composable
fun ParticipantsScreen(
    onBack: () -> Unit
) {
    val search = remember { mutableStateOf("") }

    val participants = listOf(
        Participant("Aman", "Host", false),
        Participant("Rahul Sharma", "Participant", false),
        Participant("Priya Singh", "Participant", true),
        Participant("Aditya Kumar", "Participant", false),
        Participant("Neha Verma", "Participant", true)
    )

    val filteredParticipants = participants.filter {
        it.name.contains(search.value, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 20.dp)
    ) {

        Spacer(modifier = Modifier.height(20.dp))

        // Top bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = TextPrimary
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Participants",
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "${participants.size} people in session",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Search
        OutlinedTextField(
            value = search.value,
            onValueChange = { search.value = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = {
                Text(
                    text = "Search participants",
                    color = TextSecondary
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = TextSecondary
                )
            },
            shape = RoundedCornerShape(14.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "IN THIS SESSION",
            color = TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            filteredParticipants.forEach { participant ->

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = CardColor
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        // Avatar
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(NeonBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = participant.name
                                    .first()
                                    .uppercase(),
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.size(12.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = participant.name,
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold
                                )

                                if (participant.role == "Host") {
                                    Spacer(modifier = Modifier.size(8.dp))

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = NeonBlue.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = "HOST",
                                            modifier = Modifier.padding(
                                                horizontal = 7.dp,
                                                vertical = 3.dp
                                            ),
                                            color = NeonBlue,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(3.dp))

                            Text(
                                text = participant.role,
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }

                        Icon(
                            imageVector = if (participant.isMuted) {
                                Icons.Default.MicOff
                            } else {
                                Icons.Default.Mic
                            },
                            contentDescription = null,
                            tint = if (participant.isMuted) {
                                TextSecondary
                            } else {
                                Green
                            },
                            modifier = Modifier.size(21.dp)
                        )
                    }
                }
            }
        }
    }
}