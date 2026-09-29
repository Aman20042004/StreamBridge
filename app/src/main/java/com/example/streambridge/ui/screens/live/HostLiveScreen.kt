package com.example.streambridge.ui.screens.live



import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HostLiveScreen(
    sessionName: String = "Kotlin Study Group",
    onEndSession: () -> Unit = {},
    onChat: () -> Unit = {},
    onAskRequests: () -> Unit = {},
    onParticipants: () -> Unit = {}
) {

    var micEnabled by remember {
        mutableStateOf(true)
    }

    var cameraEnabled by remember {
        mutableStateOf(true)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF020817))
    ) {

        /*
         * MAIN VIDEO AREA
         */

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 12.dp,
                    end = 12.dp,
                    top = 12.dp,
                    bottom = 118.dp
                )
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF080F20))
        ) {

            // Mock video background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF172554),
                                Color(0xFF080F20),
                                Color(0xFF020817)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF2563EB),
                                        Color(0xFF8B5CF6)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text = "A",
                            color = Color.White,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(14.dp)
                    )

                    Text(
                        text = "You",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text = "Camera preview",
                        color = Color(0xFF64748B),
                        fontSize = 12.sp
                    )
                }
            }

            /*
             * TOP SESSION INFO
             */

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color.Black.copy(alpha = 0.55f)
                ) {

                    Row(
                        modifier = Modifier.padding(
                            horizontal = 11.dp,
                            vertical = 7.dp
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEF4444))
                        )

                        Spacer(
                            modifier = Modifier.width(7.dp)
                        )

                        Text(
                            text = "LIVE",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.width(9.dp)
                )

                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    color = Color.Black.copy(alpha = 0.55f)
                ) {

                    Text(
                        text = sessionName,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 8.dp
                        )
                    )
                }

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                IconButton(
                    onClick = { }
                ) {

                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More",
                        tint = Color.White
                    )
                }
            }

            /*
             * PARTICIPANT PREVIEW
             */

            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                ParticipantPreview(
                    initials = "R",
                    name = "Rahul"
                )

                ParticipantPreview(
                    initials = "A",
                    name = "Ananya"
                )

                ParticipantPreview(
                    initials = "+8",
                    name = "More"
                )
            }
        }

        /*
         * BOTTOM CONTROL PANEL
         */

        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding(),
            color = Color(0xFF080F20)
        ) {

            Column(
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 12.dp
                )
            ) {

                /*
                 * QUICK ACTIONS
                 */

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    ControlButton(
                        icon = if (micEnabled) {
                            Icons.Default.Mic
                        } else {
                            Icons.Default.MicOff
                        },
                        label = if (micEnabled) {
                            "Mute"
                        } else {
                            "Unmute"
                        },
                        onClick = {
                            micEnabled = !micEnabled
                        }
                    )

                    ControlButton(
                        icon = if (cameraEnabled) {
                            Icons.Default.Videocam
                        } else {
                            Icons.Default.VideocamOff
                        },
                        label = if (cameraEnabled) {
                            "Camera"
                        } else {
                            "Camera Off"
                        },
                        onClick = {
                            cameraEnabled = !cameraEnabled
                        }
                    )

                    ControlButton(
                        icon = Icons.Default.Groups,
                        label = "People",
                        onClick = onParticipants
                    )

                    ControlButton(
                        icon = Icons.Default.Chat,
                        label = "Chat",
                        onClick = onChat
                    )

                    ControlButton(
                        icon = Icons.Default.QuestionAnswer,
                        label = "Ask",
                        onClick = onAskRequests
                    )
                }

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                /*
                 * END SESSION
                 */

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    onClick = onEndSession,
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF3B1118)
                ) {

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector = Icons.Default.CallEnd,
                            contentDescription = null,
                            tint = Color(0xFFF87171)
                        )

                        Spacer(
                            modifier = Modifier.width(8.dp)
                        )

                        Text(
                            text = "End Session",
                            color = Color(0xFFF87171),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}


/* =========================================================
   CONTROL BUTTON
   ========================================================= */

@Composable
private fun ControlButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Surface(
            modifier = Modifier.size(46.dp),
            onClick = onClick,
            shape = CircleShape,
            color = Color(0xFF141E34)
        ) {

            Box(
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Color.White,
                    modifier = Modifier.size(21.dp)
                )
            }
        }

        Spacer(
            modifier = Modifier.height(5.dp)
        )

        Text(
            text = label,
            color = Color(0xFF94A3B8),
            fontSize = 9.sp
        )
    }
}


/* =========================================================
   PARTICIPANT PREVIEW
   ========================================================= */

@Composable
private fun ParticipantPreview(
    initials: String,
    name: String
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(
                    width = 1.dp,
                    color = Color(0xFF334155),
                    shape = RoundedCornerShape(12.dp)
                )
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF1D4ED8),
                            Color(0xFF7C3AED)
                        )
                    ),
                ),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = initials,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(
            modifier = Modifier.height(3.dp)
        )

        Text(
            text = name,
            color = Color.White,
            fontSize = 8.sp
        )
    }
}