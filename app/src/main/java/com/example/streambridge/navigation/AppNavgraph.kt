package com.example.streambridge.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.streambridge.data.local.SessionManager
import com.example.streambridge.ui.history.SessionHistoryScreen
import com.example.streambridge.ui.screens.ask.AskRequestScreen
import com.example.streambridge.ui.screens.ask.AskRequestsScreen
import com.example.streambridge.ui.screens.auth.LoginScreen
import com.example.streambridge.ui.screens.auth.SignUpScreen
import com.example.streambridge.ui.screens.chat.ChatScreen
import com.example.streambridge.ui.screens.create.CreateSessionScreen

import com.example.streambridge.ui.screens.home.HomeScreen
import com.example.streambridge.ui.screens.join.JoinSessionScreen
import com.example.streambridge.ui.screens.live.HostLiveScreen
import com.example.streambridge.ui.screens.loby.SessionLobbyScreen
import com.example.streambridge.ui.screens.participants.ParticipantLiveScreen
import com.example.streambridge.ui.screens.participants.ParticipantsScreen
import com.example.streambridge.ui.screens.prejoin.PreJoinScreen
import com.example.streambridge.ui.screens.profile.ProfileScreen
import com.example.streambridge.ui.screens.sessions.SessionDetailsScreen
import com.example.streambridge.ui.screens.sessions.SessionSummaryScreen
import com.example.streambridge.ui.screens.settings.SettingsScreen
import com.example.streambridge.ui.screens.splash.SplashScreen
import com.example.streambridge.viewmodel.InteractionViewModel
import com.example.streambridge.viewmodel.MediaViewModel
import com.example.streambridge.viewmodel.MediaViewModelFactory
import com.example.streambridge.viewmodel.ParticipantViewModel
import com.example.streambridge.viewmodel.SessionViewModel
import kotlinx.coroutines.flow.first

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavGraph() {

    val context = LocalContext.current
    val sessionManager = remember {
        SessionManager(context)
    }

    val backStack = rememberNavBackStack(Splash)

    NavDisplay(
        backStack = backStack,
        onBack = {
            if (backStack.size > 1) {
                backStack.removeLastOrNull()
            }
        },
        entryProvider = entryProvider {

            entry<Splash> {
                LaunchedEffect(Unit) {
                    val savedSession = sessionManager.session.first()
                    backStack.clear()
                    if (savedSession != null) {
                        backStack.add(Home)
                    } else {
                        backStack.add(Login)
                    }
                }
                SplashScreen(
                    onGetStarted = {
                        backStack.clear()
                        backStack.add(Login)
                    },
                    onLogin = {
                        backStack.clear()
                        backStack.add(Login)
                    }
                )
            }

            entry<Login> {
                LoginScreen(
                    onLogin = {
                        backStack.clear()
                        backStack.add(Home)
                    },
                    onSignUp = {
                        backStack.clear()
                        backStack.add(SignUp)
                    }
                )
            }

            entry<SignUp> {
                SignUpScreen(
                    onSignUp = {
                        backStack.add(Home)
                    },
                    onLogin = {
                        backStack.removeLastOrNull()
                    }
                )
            }

            entry<Home> {
                HomeScreen(
                    sessionManager = sessionManager,
                    onCreateSession = {
                        backStack.add(CreateSession)
                    },
                    onJoinSession = {
                        backStack.add(JoinSession)
                    },
                    onSessionClick = { session ->
                        backStack.add(
                            SessionDetails(
                                sessionId = session.id ?: return@HomeScreen
                            )
                        )
                    },
                    onProfileClick = {
                        backStack.add(Profile)
                    }
                )
            }

            entry<CreateSession> {
                val userSession by sessionManager.session.collectAsState(initial = null)

                CreateSessionScreen(
                    sessionManager = sessionManager,
                    onBack = {
                        backStack.removeLastOrNull()
                    },
                    onNext = { title, description, accessMode ->
                        val ownerId = userSession?.userId

                        if (!ownerId.isNullOrBlank()) {
                            backStack.add(
                                PreJoin(
                                    ownerId = ownerId,
                                    title = title,
                                    description = description,
                                    accessMode = accessMode
                                )
                            )
                        }
                    }
                )
            }

            entry<PreJoin> { route ->
                PreJoinScreen(
                    ownerId = route.ownerId ?: "",
                    sessionTitle = route.title ?: "",
                    description = route.description ?: "",
                    accessMode = route.accessMode ?: "",
                    onBack = {
                        backStack.removeLastOrNull()
                    },
                    onCreate = { createdSession ->
                        backStack.add(
                            HostLive(
                                sessionId = createdSession.id ?: ""
                            )
                        )
                    },
                    onCancel = {
                        backStack.removeLastOrNull()
                    }
                )
            }

            entry<SessionLobby> { lobby ->
                val sessionViewModel: SessionViewModel = viewModel()
                val session by sessionViewModel.session.collectAsState()
                val userSession by sessionManager.session.collectAsState(initial = null)

                LaunchedEffect(lobby.sessionId) {
                    sessionViewModel.getSession(lobby.sessionId)
                }

                if (session != null) {
                    val currentSession = session!!

                    LaunchedEffect(lobby.sessionId, currentSession.status) {
                        if (currentSession.status == "LIVE" &&
                            backStack.lastOrNull() == lobby
                        ) {
                            backStack.removeLastOrNull()
                            backStack.add(HostLive(lobby.sessionId))
                        }
                    }

                    SessionLobbyScreen(
                        session = currentSession,
                        onBack = {
                            if (backStack.lastOrNull() == lobby) {
                                backStack.removeLastOrNull()
                            }
                        },
                        onStartSession = {
                            userSession?.userId?.let { ownerId ->
                                sessionViewModel.startSession(
                                    sessionId = lobby.sessionId,
                                    ownerId = ownerId
                                )
                            }
                        },
                        onShare = { /* Later */ },
                        onCopyCode = { /* Later */ }
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF020817)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF5B3DF5))
                    }
                }
            }

            entry<Participants> { participantsEntry ->
                ParticipantsScreen(
                    sessionId = participantsEntry.sessionId,
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }

            entry<HostLive> { hostLive ->
                val sessionViewModel: SessionViewModel = viewModel()
                val session by sessionViewModel.session.collectAsState()
                val userSession by sessionManager.session.collectAsState(initial = null)

                val mediaViewModel: MediaViewModel = viewModel(
                    factory = MediaViewModelFactory(context)
                )

                LaunchedEffect(hostLive.sessionId) {
                    sessionViewModel.getSession(hostLive.sessionId)
                }

                session?.let { currentSession ->
                    LaunchedEffect(hostLive.sessionId, currentSession.status) {
                        if (currentSession.status == "ENDED" &&
                            backStack.lastOrNull() == hostLive
                        ) {
                            backStack.removeLastOrNull()
                            backStack.add(SessionSummary(hostLive.sessionId))
                        }
                    }
                    LaunchedEffect(hostLive.sessionId, userSession?.userId) {
                        val userId = userSession?.userId

                        if (!userId.isNullOrBlank()) {
                            mediaViewModel.startHostLiveKit(
                                sessionId = hostLive.sessionId,
                                userId = userId
                            )
                        }
                    }

                    HostLiveScreen(
                        sessionName = currentSession.title,
                        sessionId = hostLive.sessionId,
                        participantId = "host-${userSession?.userId.orEmpty()}",
                        userId = userSession?.userId.orEmpty(),
                        mediaViewModel = mediaViewModel,
                        onEndSession = {
                            userSession?.userId?.let { ownerId ->
                                sessionViewModel.endSession(
                                    sessionId = hostLive.sessionId,
                                    ownerId = ownerId
                                )
                            }
                        },
                        onChat = {
                            if (backStack.lastOrNull() == hostLive) {
                                backStack.add(Chat(hostLive.sessionId))
                            }
                        },
                        onAskRequests = {
                            if (backStack.lastOrNull() == hostLive) {
                                backStack.add(AskRequests(hostLive.sessionId))
                            }
                        },
                        onParticipants = {
                            if (backStack.lastOrNull() == hostLive) {
                                backStack.add(Participants(hostLive.sessionId))
                            }
                        }
                    )
                }
            }

            entry<ParticipantLive> { participantLive ->
                val participantViewModel: ParticipantViewModel = viewModel()
                val sessionViewModel: SessionViewModel = viewModel()
                val userSession by sessionManager.session.collectAsState(initial = null)
                val isLoading by participantViewModel.isLoading.collectAsState()
                val error by participantViewModel.error.collectAsState()
                val session by sessionViewModel.session.collectAsState()

                val mediaViewModel: MediaViewModel = viewModel(
                    factory = MediaViewModelFactory(context)
                )

                LaunchedEffect(participantLive.sessionId) {
                    sessionViewModel.getSession(participantLive.sessionId)
                }

                session
                    ?.takeIf { it.id == participantLive.sessionId }
                    ?.let { currentSession ->
                        LaunchedEffect(
                            participantLive.sessionId,
                            participantLive.participantId,
                            userSession?.userId
                        ) {
                            val userId = userSession?.userId

                            if (!userId.isNullOrBlank()) {
                                mediaViewModel.joinParticipantLiveKit(
                                    sessionId = participantLive.sessionId,
                                    participantId = participantLive.participantId,
                                    userId = userId
                                )
                            }
                        }
                        ParticipantLiveScreen(
                            sessionName = currentSession.title,
                            sessionId = participantLive.sessionId,
                            participantId = participantLive.participantId,
                            userId = userSession?.userId.orEmpty(),
                            mediaViewModel = mediaViewModel,
                            onLeaveSession = {
                                if (!isLoading) {
                                    participantViewModel.leaveSession(
                                        participantId = participantLive.participantId,
                                        onSuccess = {
                                            backStack.add(
                                                SessionSummary(participantLive.sessionId)
                                            )
                                        }
                                    )
                                }
                            },
                            onChat = {
                                backStack.add(Chat(participantLive.sessionId))
                            },
                            onAsk = {
                                backStack.add(AskRequest(participantLive.sessionId))
                            },
                            onParticipants = {
                                backStack.add(Participants(participantLive.sessionId))
                            }
                        )
                    }
                    ?: run {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                if (isLoading) {
                    CircularProgressIndicator()
                }

                error?.let { message ->
                    Text(
                        text = message,
                        color = Color.Red
                    )
                }
            }

            entry<Chat> { chat ->
                val interactionViewModel: InteractionViewModel = viewModel()
                val userSession by sessionManager.session.collectAsState(initial = null)

                ChatScreen(
                    sessionId = chat.sessionId,
                    userId = userSession?.userId.orEmpty(),
                    interactionViewModel = interactionViewModel,
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }

            entry<AskRequest> { ask ->
                val interactionViewModel: InteractionViewModel = viewModel()
                val userSession by sessionManager.session.collectAsState(initial = null)

                AskRequestScreen(
                    sessionId = ask.sessionId,
                    userId = userSession?.userId.orEmpty(),
                    interactionViewModel = interactionViewModel,
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }

            entry<AskRequests> { asks ->
                val interactionViewModel: InteractionViewModel = viewModel()
                val userSession by sessionManager.session.collectAsState(initial = null)
                val sessionViewModel: SessionViewModel = viewModel()
                val session by sessionViewModel.session.collectAsState()

                LaunchedEffect(asks.sessionId) {
                    sessionViewModel.getSession(asks.sessionId)
                }

                session?.takeIf { it.id == asks.sessionId }?.let { currentSession ->
                    val currentUserId = userSession?.userId.orEmpty()
                    val isOwner = currentUserId.isNotBlank() &&
                            currentUserId == currentSession.ownerId

                    AskRequestsScreen(
                        sessionId = asks.sessionId,
                        ownerId = currentUserId,
                        isOwner = isOwner,
                        interactionViewModel = interactionViewModel,
                        onBack = {
                            backStack.removeLastOrNull()
                        }
                    )
                } ?: run {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            entry<SessionSummary> { summary ->
                val sessionViewModel: SessionViewModel = viewModel()
                val session by sessionViewModel.session.collectAsState()

                LaunchedEffect(summary.sessionId) {
                    sessionViewModel.getSession(summary.sessionId)
                }

                session?.let { currentSession ->
                    SessionSummaryScreen(
                        session = currentSession,
                        onBackHome = {
                            backStack.clear()
                            backStack.add(Home)
                        }
                    )
                }
            }

            entry<JoinSession> {
                JoinSessionScreen(
                    onBack = {
                        backStack.removeLastOrNull()
                    },
                    onJoin = {
                        // backStack.add(ParticipantLive)
                    }
                )
            }

            entry<Profile> {
                ProfileScreen(
                    sessionManager = sessionManager,
                    onBack = {
                        backStack.removeLastOrNull()
                    },
                    onSettings = {
                        backStack.add(Settings)
                    },
                    onLogout = {
                        backStack.clear()
                        backStack.add(Login)
                    }
                )
            }

            entry<Settings> {
                SettingsScreen(
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }

            entry<SessionDetails> { details ->
                val participantViewModel: ParticipantViewModel = viewModel()
                val sessionViewModel: SessionViewModel = viewModel()
                val userSession by sessionManager.session.collectAsState(initial = null)
                val myParticipant by participantViewModel.myParticipant.collectAsState()
                val isLoading by participantViewModel.isLoading.collectAsState()
                val error by participantViewModel.error.collectAsState()
                val session by sessionViewModel.session.collectAsState()

                LaunchedEffect(details.sessionId) {
                    sessionViewModel.getSession(details.sessionId)
                }

                LaunchedEffect(myParticipant?.id) {
                    val participant = myParticipant
                    if (
                        participant != null &&
                        participant.sessionId == details.sessionId &&
                        participant.status == "JOINED"
                    ) {
                        participant.id?.let { participantId ->
                            backStack.add(
                                ParticipantLive(
                                    sessionId = details.sessionId,
                                    participantId = participantId
                                )
                            )
                        }
                    }
                }

                if (session == null) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    SessionDetailsScreen(
                        sessionName = session!!.title,
                        description = session!!.description.toString(),
                        participants = 0,
                        isPrivate = false,
                        onBack = {
                            backStack.removeLastOrNull()
                        },
                        onJoin = {
                            val userId = userSession?.userId
                            if (userId.isNullOrBlank()) {
                                participantViewModel.clearError()
                            } else if (!isLoading) {
                                participantViewModel.joinSession(
                                    sessionId = details.sessionId,
                                    userId = userId
                                )
                            }
                        }
                    )
                }

                if (isLoading) {
                    CircularProgressIndicator()
                }

                error?.let { message ->
                    Text(
                        text = message,
                        color = Color.Red
                    )
                }
            }

            entry<SessionHistory> {
                SessionHistoryScreen(
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
        }
    )
}