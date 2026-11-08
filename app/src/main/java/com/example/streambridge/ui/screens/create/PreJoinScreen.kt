
package com.example.streambridge.ui.screens.prejoin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.streambridge.data.model.Session
import com.example.streambridge.ui.screens.live.LiveKitVideoView
import com.example.streambridge.viewmodel.MediaViewModel
import com.example.streambridge.viewmodel.MediaViewModelFactory
import com.example.streambridge.viewmodel.SessionViewModel

private val Background = Color(0xFF070B18)
private val CardColor = Color(0xFF11182A)
private val BorderColor = Color(0xFF29344D)
private val Purple = Color(0xFF873BFA)
private val Blue = Color(0xFF3678FF)
private val TextPrimary = Color(0xFFF5F6FF)
private val TextSecondary = Color(0xFF9DA8C0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreJoinScreen(
    ownerId: String,
    sessionTitle: String,
    description: String,
    accessMode: String,
    onBack: () -> Unit,
    onCreate: (Session) -> Unit,
    onCancel: () -> Unit,
    scheduledTime: String = "Ready to start",
    cameraPreview: (@Composable BoxScope.() -> Unit)? = null
) {
    val sessionViewModel: SessionViewModel = viewModel()

    val session by sessionViewModel.session.collectAsState()
    val isLoading by sessionViewModel.isLoading.collectAsState()
    val error by sessionViewModel.error.collectAsState()

    var createRequested by remember { mutableStateOf(false) }
    var navigationHandled by remember { mutableStateOf(false) }
    val mediaViewModel: MediaViewModel = viewModel(factory = MediaViewModelFactory(LocalContext.current))
    val localVideoTrack by mediaViewModel.localVideoTrack.collectAsState()

    LaunchedEffect(Unit) {
        mediaViewModel.initializeLocalCamera() // Screen khulte hi camera on karega
    }
    // Clear previous session/error state when this screen opens.
    LaunchedEffect(Unit) {
        sessionViewModel.resetSessionState()
    }

    // Navigate only after the current request succeeds.
    LaunchedEffect(session, createRequested, error) {
        if (createRequested && session != null && !navigationHandled) {
            navigationHandled = true
            createRequested = false
            onCreate(session!!)
        }

        // Allow retry after an API failure.
        if (createRequested && error != null) {
            createRequested = false
        }
    }

    var showSettings by remember { mutableStateOf(false) }
    var cameraEnabled by remember { mutableStateOf(true) }
    var microphoneEnabled by remember { mutableStateOf(true) }
    var audioEnabled by remember { mutableStateOf(true) }

    var cameraSelection by remember { mutableStateOf("Front Camera") }
    var microphoneSelection by remember {
        mutableStateOf("Built-in Microphone")
    }
    var audioOutput by remember { mutableStateOf("Device Speaker") }
    var videoQuality by remember { mutableStateOf("HD (720p)") }
    var hdEnabled by remember { mutableStateOf(true) }
    var mirrorEnabled by remember { mutableStateOf(true) }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
        ) {

            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 30.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "‹",
                    color = TextPrimary,
                    fontSize = 36.sp,
                    modifier = Modifier.clickable { onBack() }
                )

                Spacer(Modifier.weight(1f))

                Text(
                    text = "◆ Stream Bridge",
                    color = TextPrimary,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.weight(1f))
                Spacer(Modifier.width(30.dp))
            }

            // Heading
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Pre-Join Room",
                    color = TextPrimary,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.weight(1f))

                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color(0xFF073B31))
                        .border(1.dp, Color(0xFF087D61), CircleShape)
                        .padding(horizontal = 12.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        Modifier
                            .size(7.dp)
                            .background(Color(0xFF21E6A1), CircleShape)
                    )

                    Spacer(Modifier.width(7.dp))

                    Text(
                        "Green Room",
                        color = Color(0xFF4DE6B0),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                "Check your camera, mic and settings before going live.",
                color = TextSecondary,
                fontSize = 14.sp,
                lineHeight = 21.sp
            )

            Spacer(Modifier.height(22.dp))

            // Camera preview container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.95f)
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color(0xFF050811))
                    .border(1.dp, BorderColor, RoundedCornerShape(22.dp))
            ) {
                if (cameraEnabled && localVideoTrack != null) {
                    LiveKitVideoView(
                        videoTrack = localVideoTrack,
                        liveKitManager = mediaViewModel.liveKitManager,
                        modifier = Modifier.fillMaxSize(),
                        mirror = true
                    )
                }else {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "▣",
                            fontSize = 42.sp,
                            color = TextSecondary
                        )

                        Spacer(Modifier.height(10.dp))

                        Text(
                            if (cameraEnabled)
                                "Camera preview will appear here"
                            else
                                "Camera is turned off",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }

                // Host label
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xCC080D19))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("♛", color = Color(0xFFFFC94D))

                    Spacer(Modifier.width(6.dp))

                    Text(
                        "You (Host)",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }

                Text(
                    "⛶",
                    color = Color.White,
                    fontSize = 25.sp,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(14.dp)
                )
            }

            Spacer(Modifier.height(20.dp))

            // Device controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PreJoinControl(
                    symbol = "▣",
                    label = "Camera",
                    status = if (cameraEnabled) "On" else "Off",
                    selected = cameraEnabled,
                    onClick = {
                        cameraEnabled = !cameraEnabled
                    }
                )

                PreJoinControl(
                    symbol = "♩",
                    label = "Mic",
                    status = if (microphoneEnabled) "On" else "Off",
                    selected = microphoneEnabled,
                    onClick = {
                        microphoneEnabled = !microphoneEnabled
                    }
                )

                PreJoinControl(
                    symbol = "◖",
                    label = "Audio",
                    status = if (audioEnabled) "Default" else "Off",
                    selected = audioEnabled,
                    onClick = {
                        audioEnabled = !audioEnabled
                    }
                )

                PreJoinControl(
                    symbol = "⚙",
                    label = "Settings",
                    status = "",
                    selected = false,
                    onClick = {
                        showSettings = true
                    }
                )
            }

            Spacer(Modifier.height(24.dp))

            // Session information
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(CardColor)
                    .border(
                        1.dp,
                        BorderColor,
                        RoundedCornerShape(18.dp)
                    )
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(66.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Blue, Purple)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "▶",
                        color = Color.White,
                        fontSize = 27.sp
                    )
                }

                Spacer(Modifier.width(13.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        sessionTitle,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        "◷  $scheduledTime",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                Text(
                    "● Ready",
                    color = Color(0xFF4DE6B0),
                    fontSize = 11.sp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(7.dp))
                        .background(Color(0xFF073B31))
                        .padding(horizontal = 9.dp, vertical = 6.dp)
                )
            }

            Spacer(Modifier.height(18.dp))

            // API error
            error?.let {
                Text(
                    text = it,
                    color = Color(0xFFFF6B6B),
                    fontSize = 13.sp
                )

                Spacer(Modifier.height(12.dp))
            }

            // Go Live button
            Button(
                onClick = {
                    if (
                        !isLoading &&
                        !createRequested &&
                        !navigationHandled
                    ) {
                        createRequested = true

                        sessionViewModel.createSession(
                            ownerId = ownerId,
                            title = sessionTitle,
                            description = description.ifBlank { null },
                            accessMode = accessMode
                        )
                    }
                },
                enabled = !isLoading &&
                        !createRequested &&
                        !navigationHandled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Purple,
                    disabledContainerColor = Color(0xFF35304A)
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                listOf(Blue, Purple, Color(0xFFB13BEF))
                            ),
                            RoundedCornerShape(15.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(23.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            "▣  Go Live",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = onCancel,
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(15.dp),
                border = BorderStroke(
                    1.dp,
                    Color(0xFF35496B)
                )
            ) {
                Text(
                    "↪  Cancel",
                    color = TextPrimary,
                    fontSize = 15.sp
                )
            }
        }
    }

    // Settings bottom sheet
    if (showSettings) {
        ModalBottomSheet(
            onDismissRequest = {
                showSettings = false
            },
            sheetState = sheetState,
            containerColor = Color(0xFF0C1322),
            contentColor = TextPrimary,
            dragHandle = {
                BottomSheetDefaults.DragHandle(
                    color = Color(0xFF7C8498)
                )
            }
        ) {
            PreJoinSettingsSheet(
                cameraSelection = cameraSelection,
                onCameraChange = { cameraSelection = it },
                microphoneSelection = microphoneSelection,
                onMicrophoneChange = { microphoneSelection = it },
                audioOutput = audioOutput,
                onAudioOutputChange = { audioOutput = it },
                videoQuality = videoQuality,
                onVideoQualityChange = { videoQuality = it },
                hdEnabled = hdEnabled,
                onHdChange = { hdEnabled = it },
                mirrorEnabled = mirrorEnabled,
                onMirrorChange = { mirrorEnabled = it },
                onApply = {
                    showSettings = false
                }
            )
        }
    }
}

@Composable
private fun PreJoinControl(
    symbol: String,
    label: String,
    status: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(59.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(
                    if (selected)
                        Color(0xFF27154B)
                    else
                        Color(0xFF171D30)
                )
                .border(
                    1.dp,
                    if (selected) Purple else Color.Transparent,
                    RoundedCornerShape(18.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                symbol,
                color = Color.White,
                fontSize = 24.sp
            )
        }

        Spacer(Modifier.height(7.dp))

        Text(
            label,
            color = TextPrimary,
            fontSize = 12.sp
        )

        if (status.isNotEmpty()) {
            Text(
                status,
                color = TextSecondary,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun PreJoinSettingsSheet(
    cameraSelection: String,
    onCameraChange: (String) -> Unit,
    microphoneSelection: String,
    onMicrophoneChange: (String) -> Unit,
    audioOutput: String,
    onAudioOutputChange: (String) -> Unit,
    videoQuality: String,
    onVideoQualityChange: (String) -> Unit,
    hdEnabled: Boolean,
    onHdChange: (Boolean) -> Unit,
    mirrorEnabled: Boolean,
    onMirrorChange: (Boolean) -> Unit,
    onApply: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 22.dp)
            .padding(bottom = 30.dp)
    ) {
        Text(
            "Pre-Join Settings",
            color = TextPrimary,
            fontSize = 23.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(24.dp))

        SettingsDropdown(
            label = "Camera",
            value = cameraSelection,
            options = listOf(
                "Front Camera",
                "Back Camera"
            ),
            onSelected = onCameraChange
        )

        SettingsDropdown(
            label = "Microphone",
            value = microphoneSelection,
            options = listOf(
                "Built-in Microphone",
                "Bluetooth Microphone",
                "Wired Headset"
            ),
            onSelected = onMicrophoneChange
        )

        SettingsDropdown(
            label = "Audio Output",
            value = audioOutput,
            options = listOf(
                "Device Speaker",
                "Earpiece",
                "Bluetooth Headphones"
            ),
            onSelected = onAudioOutputChange
        )

        SettingsDropdown(
            label = "Video Quality",
            value = videoQuality,
            options = listOf(
                "SD (360p)",
                "HD (720p)",
                "Full HD (1080p)"
            ),
            onSelected = onVideoQualityChange
        )

        HorizontalDivider(
            color = BorderColor,
            modifier = Modifier.padding(vertical = 15.dp)
        )

        SettingsSwitch(
            title = "Enable HD Video",
            checked = hdEnabled,
            onCheckedChange = onHdChange
        )

        SettingsSwitch(
            title = "Mirror My Video",
            checked = mirrorEnabled,
            onCheckedChange = onMirrorChange
        )

        Spacer(Modifier.height(25.dp))

        GradientButton(
            text = "Apply",
            onClick = onApply
        )
    }
}

@Composable
private fun SettingsDropdown(
    label: String,
    value: String,
    options: List<String>,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(bottom = 18.dp)
    ) {
        Text(
            label,
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(8.dp))

        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(13.dp))
                    .background(CardColor)
                    .border(
                        1.dp,
                        BorderColor,
                        RoundedCornerShape(13.dp)
                    )
                    .clickable {
                        expanded = true
                    }
                    .padding(horizontal = 15.dp, vertical = 15.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    value,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    "⌄",
                    color = TextSecondary,
                    fontSize = 20.sp
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                },
                modifier = Modifier.background(CardColor)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                option,
                                color = TextPrimary
                            )
                        },
                        onClick = {
                            onSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsSwitch(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            title,
            color = TextPrimary,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Purple,
                uncheckedThumbColor = TextSecondary,
                uncheckedTrackColor = CardColor
            )
        )
    }
}

@Composable
private fun GradientButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(14.dp),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Purple
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        listOf(Blue, Purple, Color(0xFFB13BEF))
                    ),
                    RoundedCornerShape(14.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}
