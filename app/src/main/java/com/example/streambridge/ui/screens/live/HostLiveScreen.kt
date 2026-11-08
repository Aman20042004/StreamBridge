package com.example.streambridge.ui.screens.live

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.streambridge.viewmodel.MediaViewModel
import androidx.compose.runtime.collectAsState

@Composable
fun HostLiveScreen(
    sessionName: String,
    sessionId: String,
    participantId: String,
    userId: String,
    mediaViewModel: MediaViewModel,
    onEndSession: () -> Unit = {},
    onChat: () -> Unit = {},
    onAskRequests: () -> Unit = {},
    onParticipants: () -> Unit = {}
) {
    val context = LocalContext.current

    var micEnabled by remember { mutableStateOf(true) }
    var cameraEnabled by remember { mutableStateOf(true) }
    var hasCameraPermission by remember { mutableStateOf(false) }

    // State flow ViewModel se read kar rahe hain taaki re-composition ho jab track aaye
    val localVideoTrack by mediaViewModel.localVideoTrack.collectAsState()

    // Permission Launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted

        if (!isGranted) {
            cameraEnabled = false
        }
    }
    // Screen khulte hi Permission Check aur Camera Start
    LaunchedEffect(Unit) {
        val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            hasCameraPermission = true
     //       if (cameraEnabled) {
               // mediaViewModel.initializeLocalCamera()
          //  }
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Screen band hone par resources free karo taaki background me camera on na rahe
    DisposableEffect(Unit) {
        onDispose {
            mediaViewModel.releaseLocalCamera()
        }
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

            // ACTUAL LOCAL CAMERA PREVIEW
            if (cameraEnabled && hasCameraPermission && localVideoTrack != null) {
                LiveKitVideoView(
                    videoTrack = localVideoTrack,
                    liveKitManager = mediaViewModel.liveKitManager,
                    modifier = Modifier.fillMaxSize(),
                    mirror = true
                )
            } else {
                // Agar camera band hai toh wahi purana mock background dikhayenge
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
                                text = "H",
                                color = Color.White,
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "You",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = if (!hasCameraPermission) "Permission denied" else "Camera off",
                            color = Color(0xFF64748B),
                            fontSize = 12.sp
                        )
                    }
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
                        Spacer(modifier = Modifier.width(7.dp))
                        Text(
                            text = "LIVE",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
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
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 8.dp
                        )
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More",
                        tint = Color.White
                    )
                }
            }

            /*
             * PARTICIPANT PREVIEW (Mock - preserved)
             */
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Tumhara purana ParticipantPreview code yaha aayega (wo class mere paas nahi hai isliye method comment kar raha hoon taaki error na de, usko un-comment karlena)
                // ParticipantPreview(initials = "R", name = "Rahul")
                // ParticipantPreview(initials = "A", name = "Ananya")
                // ParticipantPreview(initials = "+8", name = "More")
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
                /* QUICK ACTIONS */
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // MICROPHONE CONTROL
                    // ... ControlButton class tumhari local hogi, bas method call aise jayega
                    IconButton(onClick = {
                        micEnabled = !micEnabled
                        mediaViewModel.setLiveKitMicrophone(micEnabled)
                    }) {
                        Icon(
                            imageVector = if (micEnabled) Icons.Default.Mic else Icons.Default.MicOff,
                            contentDescription = "Mic Toggle",
                            tint = Color.White
                        )
                    }

                    // CAMERA CONTROL
                    IconButton(onClick = {
                        if (hasCameraPermission) {
                            cameraEnabled = !cameraEnabled
                            mediaViewModel.setLiveKitCamera(cameraEnabled)
                        } else {
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    }) {
                        Icon(
                            imageVector = if (cameraEnabled) Icons.Default.Videocam else Icons.Default.VideocamOff,
                            contentDescription = "Camera Toggle",
                            tint = Color.White
                        )
                    }

                    // PARTICIPANTS
                    IconButton(onClick = onParticipants) {
                        Icon(Icons.Default.Groups, contentDescription = "People", tint = Color.White)
                    }

                    // CHAT
                    IconButton(onClick = onChat) {
                        Icon(Icons.Default.Chat, contentDescription = "Chat", tint = Color.White)
                    }

                    // ASK
                    IconButton(onClick = onAskRequests) {
                        Icon(Icons.Default.QuestionAnswer, contentDescription = "Ask", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                /* END SESSION */
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
                        Spacer(modifier = Modifier.width(8.dp))
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