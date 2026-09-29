package com.example.streambridge.ui.screens.home



import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Groups
//import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VideoCall
//import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Background = Color(0xFF020817)
private val CardColor = Color(0xFF0B1224)
private val SecondaryText = Color(0xFF94A3B8)
private val Blue = Color(0xFF3B82F6)
private val Purple = Color(0xFF8B5CF6)

data class DemoSession(
    val title: String,
    val host: String,
    val participants: Int,
    val isLive: Boolean
)

@Composable
fun HomeScreen(
    onCreateSession: () -> Unit,
    onJoinSession: () -> Unit,
    onSessionClick: (DemoSession) -> Unit,
    onProfileClick: () -> Unit
){

    var searchQuery by remember {
        mutableStateOf("")
    }

    val sessions = remember {
        listOf(
            DemoSession(
                title = "AI & Machine Learning",
                host = "Rahul Sharma",
                participants = 24,
                isLive = true
            ),
            DemoSession(
                title = "Kotlin Study Group",
                host = "Ananya Singh",
                participants = 12,
                isLive = true
            ),
            DemoSession(
                title = "Web Development",
                host = "Arjun Kumar",
                participants = 18,
                isLive = false
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {

        /* ---------- TOP BAR ---------- */

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 18.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "Good evening 👋",
                    color = SecondaryText,
                    fontSize = 13.sp
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = "Welcome to Stream Bridge",
                    color = Color.White,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Blue,
                                Purple
                            )
                        )
                    ).clickable{onProfileClick()},
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "A",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }

        /* ---------- SEARCH ---------- */

        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 20.dp,
                    vertical = 18.dp
                ),
            singleLine = true,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            },
            placeholder = {
                Text("Search sessions...")
            },
            shape = RoundedCornerShape(14.dp)
        )

        /* ---------- QUICK ACTIONS ---------- */

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            ActionCard(
                modifier = Modifier.weight(1f),
                title = "Create",
                subtitle = "New session",
                icon = Icons.Default.Add,
                onClick = onCreateSession
            )

            ActionCard(
                modifier = Modifier.weight(1f),
                title = "Join",
                subtitle = "Enter a session",
                icon = Icons.Default.VideoCall,
                onClick = onJoinSession
            )
        }

        Spacer(
            modifier = Modifier.height(25.dp)
        )

        /* ---------- LIVE SESSIONS ---------- */

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Live Sessions",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "See all",
                color = Blue,
                fontSize = 13.sp
            )
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 20.dp,
                end = 20.dp,
                bottom = 24.dp
            )
        ) {

            items(
                items = sessions
            ) { session ->

                SessionCard(
                    session = session,
                    onClick = {
                        onSessionClick(session)
                    }
                )
            }
        }
    }
}


/* =========================================================
   ACTION CARD
   ========================================================= */

@Composable
private fun ActionCard(
    modifier: Modifier,
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {

    Surface(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        color = CardColor
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(11.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Blue,
                                Purple
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = title,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(
                modifier = Modifier.height(3.dp)
            )

            Text(
                text = subtitle,
                color = SecondaryText,
                fontSize = 11.sp
            )
        }
    }
}


/* =========================================================
   SESSION CARD
   ========================================================= */

@Composable
private fun SessionCard(
    session: DemoSession,
    onClick: () -> Unit
) {

    Surface(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        color = CardColor
    ) {

        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF2563EB),
                                Color(0xFF7C3AED)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White
                )
            }

            Spacer(
                modifier = Modifier.width(14.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = session.title,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    if (session.isLive) {

                        Spacer(
                            modifier = Modifier.width(7.dp)
                        )

                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(
                                    Color(0xFF22C55E)
                                )
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(5.dp)
                )

                Text(
                    text = "Hosted by ${session.host}",
                    color = SecondaryText,
                    fontSize = 11.sp
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Default.Groups,
                        contentDescription = null,
                        tint = SecondaryText,
                        modifier = Modifier.size(14.dp)
                    )

                    Spacer(
                        modifier = Modifier.width(4.dp)
                    )

                    Text(
                        text = "${session.participants} participants",
                        color = SecondaryText,
                        fontSize = 11.sp
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Open session",
                tint = SecondaryText
            )
        }
    }
}