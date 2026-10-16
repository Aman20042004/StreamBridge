package com.example.streambridge.ui.screens.create


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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import com.example.streambridge.data.local.SessionManager
import com.example.streambridge.data.model.Session
import com.example.streambridge.viewmodel.SessionViewModel

@Composable
fun CreateSessionScreen(
    sessionManager: SessionManager,
    onBack: () -> Unit = {},
    onCreate: (Session) -> Unit = {}
)  {

    val sessionViewModel: SessionViewModel = viewModel()

    val userSession by sessionManager.session.collectAsState(initial = null)

    val session by sessionViewModel.session.collectAsState()
    val isLoading by sessionViewModel.isLoading.collectAsState()
    val error by sessionViewModel.error.collectAsState()

    var title by remember {
        mutableStateOf("")
    }

    var description by remember {
        mutableStateOf("")
    }

    var isPublic by remember {
        mutableStateOf(true)
    }
    LaunchedEffect(session) {
        session?.let {
            onCreate(it)
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF020817))
            .padding(horizontal = 20.dp)
    ) {

        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = onBack
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Text(
                text = if (isLoading) "Creating..." else "Create Session",
                color = Color.White,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(
            modifier = Modifier.height(25.dp)
        )

        // Header icon
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .size(58.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF2563EB),
                            Color(0xFF8B5CF6)
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = Icons.Default.VideoCall,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(30.dp)
            )
        }

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        Text(
            text = "Start a new session",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(7.dp)
        )

        Text(
            text = "Create a space where people can connect,\nstream and interact together.",
            color = Color(0xFF94A3B8),
            fontSize = 13.sp,
            lineHeight = 19.sp
        )

        Spacer(
            modifier = Modifier.height(28.dp)
        )

        // Session title
        OutlinedTextField(
            value = title,
            onValueChange = {
                title = it
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = {
                Text("Session Name")
            },
            placeholder = {
                Text("e.g. Kotlin Study Group")
            },
            shape = RoundedCornerShape(14.dp)
        )

        Spacer(
            modifier = Modifier.height(15.dp)
        )

        // Description
        OutlinedTextField(
            value = description,
            onValueChange = {
                description = it
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            label = {
                Text("Description")
            },
            placeholder = {
                Text("What is this session about?")
            },
            maxLines = 4,
            shape = RoundedCornerShape(14.dp)
        )

        Spacer(
            modifier = Modifier.height(25.dp)
        )

        Text(
            text = "Session Access",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        // Public
        AccessOption(
            selected = isPublic,
            icon = Icons.Default.Public,
            title = "Public",
            description = "Anyone with the session can join",
            onClick = {
                isPublic = true
            }
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        // Private
        AccessOption(
            selected = !isPublic,
            icon = Icons.Default.Lock,
            title = "Private",
            description = "Only people with access can join",
            onClick = {
                isPublic = false
            }
        )

        Spacer(
            modifier = Modifier.weight(1f)
        )

        Button(
                onClick = {
                    val ownerId = userSession?.userId

                    if (ownerId != null && title.isNotBlank()) {
                        sessionViewModel.createSession(
                            ownerId = ownerId,
                            title = title.trim(),
                            description = description.trim().ifBlank { null },
                            accessMode = if (isPublic) {
                                "ANYONE_WITH_LINK"
                            } else {
                                "SELECTED_USERS_ONLY"
                            }
                        )
                    }
                },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(15.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF5B3DF5)
            )
        ) {

            Text(
                text = "Create Session",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )
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
            .background(
                color = if (selected) {
                    Color(0xFF111C35)
                } else {
                    Color(0xFF080F20)
                },
                shape = RoundedCornerShape(15.dp)
            )
            .padding(
                horizontal = 12.dp,
                vertical = 9.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (selected) {
                Color(0xFF8B5CF6)
            } else {
                Color(0xFF64748B)
            },
            modifier = Modifier.size(23.dp)
        )

        Spacer(
            modifier = Modifier.size(12.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = title,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = description,
                color = Color(0xFF64748B),
                fontSize = 11.sp
            )
        }

        RadioButton(
            selected = selected,
            onClick = onClick
        )
    }
}