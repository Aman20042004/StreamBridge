package com.example.streambridge.ui.screens.participants





import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.streambridge.data.realtime.LiveKitManager
import com.example.streambridge.ui.screens.live.LiveKitVideoView
import com.example.streambridge.viewmodel.MediaViewModel
import io.livekit.android.events.RoomEvent
import io.livekit.android.events.collect
import io.livekit.android.room.track.Track
import io.livekit.android.room.track.VideoTrack

@Composable
fun ParticipantLiveScreen(
    sessionName: String,
    sessionId: String,
    participantId: String,
    userId: String,
    mediaViewModel: MediaViewModel,
    liveKitManager: LiveKitManager = mediaViewModel.liveKitManager,
    onLeaveSession: () -> Unit = {},
    onChat: () -> Unit = {},
    onAsk: () -> Unit = {},
    onParticipants: () -> Unit = {}
) {
    var micEnabled by remember { mutableStateOf(true) }
    var speakerEnabled by remember { mutableStateOf(true) }

    val room = liveKitManager.currentRoom

    // Remember me wrap karne se UI track ko update kar payega (agar live change hota hai)
    val localVideoTrack by remember(room) {
        mutableStateOf(liveKitManager.getLocalCameraTrack())
    }

    var remoteVideoTrack by remember(room) { mutableStateOf<VideoTrack?>(null) }

    /*
     * REMOTE HOST VIDEO TRACK
     */
    LaunchedEffect(room) {
        if (room == null) {
            remoteVideoTrack = null
            return@LaunchedEffect
        }

        remoteVideoTrack = room.remoteParticipants.values
            .firstNotNullOfOrNull { participant ->
                participant.getTrackPublication(Track.Source.CAMERA)?.track as? VideoTrack
            }

        room.events.collect { event ->
            when (event) {
                is RoomEvent.TrackSubscribed -> {
                    val track = event.track as? VideoTrack
                    if (track != null) {
                        remoteVideoTrack = track
                    }
                }
                is RoomEvent.TrackUnsubscribed -> {
                    if (event.track == remoteVideoTrack) {
                        remoteVideoTrack = null
                    }
                }
                else -> Unit
            }
        }
    }

    /*
     * MAIN SCREEN
     */
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF020817))
    ) {
        /* MAIN STREAM */
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 118.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF080F20))
        ) {

            /* HOST VIDEO */
            if (room != null && remoteVideoTrack != null) {
                // YAHAN SE 'room = room' HATA DIYA HAI
                LiveKitVideoView(
                    videoTrack = remoteVideoTrack,
                    liveKitManager = mediaViewModel.liveKitManager,
                    modifier = Modifier.fillMaxSize(),
                    mirror = false
                )
            } else {
                /* HOST PLACEHOLDER */
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Color(0xFF172554), Color(0xFF080F20), Color(0xFF020817))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(105.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(Color(0xFF2563EB), Color(0xFF8B5CF6))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "A", color = Color.White, fontSize = 38.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Text(text = "Host", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Waiting for host video...", color = Color(0xFF64748B), fontSize = 11.sp)
                    }
                }
            }

            /* TOP INFO */
            Row(
                modifier = Modifier.fillMaxWidth().padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color.Black.copy(alpha = 0.55f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 11.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFEF4444))
                        )
                        Spacer(modifier = Modifier.width(7.dp))
                        Text(text = "LIVE", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.width(9.dp))

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
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }

            /* LOCAL SELF PREVIEW */
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(14.dp)
                    .size(width = 92.dp, height = 118.dp),
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF111827)
            ) {
                if (room != null && localVideoTrack != null) {
                    // YAHAN SE BHI 'room = room' HATA DIYA HAI
                    LiveKitVideoView(
                        videoTrack = localVideoTrack,
                        liveKitManager = mediaViewModel.liveKitManager,
                        modifier = Modifier.fillMaxSize(),
                        mirror = true
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Camera Off", color = Color.White, fontSize = 10.sp)
                    }
                }
            }
        }

        /* BOTTOM CONTROLS */
        Surface(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().navigationBarsPadding(),
            color = Color(0xFF080F20)
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ParticipantControl(
                        icon = if (micEnabled) Icons.Default.Mic else Icons.Default.MicOff,
                        label = if (micEnabled) "Mute" else "Unmute",
                        onClick = {
                            val newState = !micEnabled
                            micEnabled = newState
                            mediaViewModel.setLiveKitMicrophone(newState)
                        }
                    )
                    ParticipantControl(
                        icon = Icons.Default.VolumeUp,
                        label = if (speakerEnabled) "Speaker" else "Muted",
                        onClick = { speakerEnabled = !speakerEnabled }
                    )
                    ParticipantControl(icon = Icons.Default.Groups, label = "People", onClick = onParticipants)
                    ParticipantControl(icon = Icons.Default.Chat, label = "Chat", onClick = onChat)
                    ParticipantControl(icon = Icons.Default.QuestionAnswer, label = "Ask", onClick = onAsk)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    onClick = onLeaveSession,
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
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Leave Session", color = Color(0xFFF87171), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

// Ye helper composable yahan daal diya taaki 'ParticipantControl' ka error na aaye
@Composable
fun ParticipantControl(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp)
    ) {
        IconButton(onClick = onClick) {
            Icon(imageVector = icon, contentDescription = label, tint = Color.White)
        }
        Text(text = label, color = Color.White, fontSize = 10.sp)
    }
}
