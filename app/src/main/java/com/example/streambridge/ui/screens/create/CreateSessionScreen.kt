package com.example.streambridge.ui.screens.create

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.VideoCall
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
import com.example.streambridge.data.local.SessionManager

// Define consistent colors for the Create Session screen
private val Background = Color(0xFF070B18)
private val CardColor = Color(0xFF11182A)
private val BorderColor = Color(0xFF29344D)
private val Purple = Color(0xFF873BFA)
private val Blue = Color(0xFF3678FF)
private val TextPrimary = Color(0xFFF5F6FF)
private val TextSecondary = Color(0xFF9DA8C0)
private val FieldBackground = Color(0xFF0D1322)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateSessionScreen(
    sessionManager: SessionManager,
    onBack: () -> Unit = {},
    onNext: (String, String, String) -> Unit
) {
    val userSession by sessionManager.session.collectAsState(initial = null)

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isPublic by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 20.dp)
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }

            Spacer(Modifier.width(8.dp))

            Text(
                text = "Create Session",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Icon Header
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(
                    Brush.linearGradient(listOf(Blue, Purple)),
                    RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.VideoCall,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(Modifier.height(20.dp))

        // Titles
        Text(
            "Start a new session",
            color = TextPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(8.dp))

        Text(
            "Create a space where people can connect,\nstream and interact together.",
            color = TextSecondary,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )

        Spacer(Modifier.height(30.dp))

        // Input Fields using Material 3 OutlinedTextField with custom colors
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = { Text("Session Name") },
            placeholder = { Text("e.g. Kotlin Study Group") },
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Purple,
                unfocusedBorderColor = BorderColor,
                focusedLabelColor = Purple,
                unfocusedLabelColor = TextSecondary,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedContainerColor = FieldBackground,
                unfocusedContainerColor = FieldBackground
            )
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp),
            label = { Text("Description (Optional)") },
            placeholder = { Text("What is this session about?") },
            maxLines = 4,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Purple,
                unfocusedBorderColor = BorderColor,
                focusedLabelColor = Purple,
                unfocusedLabelColor = TextSecondary,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedContainerColor = FieldBackground,
                unfocusedContainerColor = FieldBackground
            )
        )

        Spacer(Modifier.height(28.dp))

        Text(
            "Session Access",
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(12.dp))

        // Access Options
        AccessOption(
            selected = isPublic,
            icon = Icons.Default.Public,
            title = "Public",
            description = "Anyone with the session can join",
            onClick = { isPublic = true }
        )

        Spacer(Modifier.height(12.dp))

        AccessOption(
            selected = !isPublic,
            icon = Icons.Default.Lock,
            title = "Private",
            description = "Only people with access can join",
            onClick = { isPublic = false }
        )

        Spacer(Modifier.weight(1f))

        // Next Button with gradient
        val isEnabled = userSession?.userId != null && title.isNotBlank()

        Button(
            onClick = {
                val ownerId = userSession?.userId
                if (ownerId != null && title.isNotBlank()) {
                    onNext(
                        title.trim(),
                        description.trim(),
                        if (isPublic) "ANYONE_WITH_LINK" else "SELECTED_USERS_ONLY"
                    )
                }
            },
            enabled = isEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(15.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                disabledContainerColor = Color(0xFF1E2638)
            ),
            contentPadding = PaddingValues(0.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        if (isEnabled) {
                            Brush.horizontalGradient(listOf(Blue, Purple, Color(0xFFB13BEF)))
                        } else {
                            Brush.horizontalGradient(listOf(Color(0xFF1E2638), Color(0xFF1E2638)))
                        },
                        RoundedCornerShape(15.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Next",
                    color = if (isEnabled) Color.White else Color(0xFF64748B),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun AccessOption(
    selected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) Color(0xFF15193B) else CardColor)
            .border(
                1.dp,
                if (selected) Purple.copy(alpha = 0.5f) else BorderColor,
                RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (selected) Color(0xFFB13BEF) else TextSecondary,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = description,
                color = TextSecondary,
                fontSize = 12.sp
            )
        }

        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = Purple,
                unselectedColor = TextSecondary
            )
        )
    }
}