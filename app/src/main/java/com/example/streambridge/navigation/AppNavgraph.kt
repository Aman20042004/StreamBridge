package com.example.streambridge.navigation





import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import com.example.streambridge.viewmodel.SessionViewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
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
import com.example.streambridge.ui.screens.profile.ProfileScreen
import com.example.streambridge.ui.screens.sessions.SessionDetailsScreen
import com.example.streambridge.ui.screens.settings.SettingsScreen
import com.example.streambridge.ui.screens.splash.SplashScreen
import com.example.streambridge.ui.screens.sessions.SessionSummaryScreen
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import com.example.streambridge.data.local.SessionManager
import com.example.streambridge.viewmodel.InteractionViewModel
import com.example.streambridge.viewmodel.ParticipantViewModel
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
                        backStack.add(Home)
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

                CreateSessionScreen(
                    sessionManager = sessionManager,
                    onBack = {
                        backStack.removeLastOrNull()
                    },
                    onCreate = { session ->
                        session.id?.let { sessionId ->
                            if (backStack.lastOrNull() is CreateSession) {
                                backStack.removeLastOrNull()
                            }
                            backStack.add(SessionLobby(sessionId))
                        }

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

                session?.let { currentSession ->

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

                        onShare = {
                            // Later
                        },

                        onCopyCode = {
                            // Later
                        }
                    )
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

                    HostLiveScreen(
                        sessionName = currentSession.title,

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
                val isLoading by participantViewModel.isLoading.collectAsState()
                val error by participantViewModel.error.collectAsState()

                ParticipantLiveScreen(
                    onLeaveSession = {
                        if (!isLoading) {
                            participantViewModel.leaveSession(
                                participantId = participantLive.participantId,
                                onSuccess = {
                                    backStack.add(SessionSummary(participantLive.sessionId))
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

                if (isLoading) {
                    androidx.compose.material3.CircularProgressIndicator()
                }

                error?.let { message ->
                    androidx.compose.material3.Text(
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
                    androidx.compose.foundation.layout.Box(
                        modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        androidx.compose.material3.CircularProgressIndicator()
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

                val userSession by sessionManager.session
                    .collectAsState(initial = null)

                val myParticipant by participantViewModel.myParticipant
                    .collectAsState()

                val isLoading by participantViewModel.isLoading
                    .collectAsState()

                val error by participantViewModel.error
                    .collectAsState()

                val session by sessionViewModel.session
                    .collectAsState()

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
                    androidx.compose.foundation.layout.Box(
                        modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        androidx.compose.material3.CircularProgressIndicator()
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
                    androidx.compose.material3.CircularProgressIndicator()
                }

                error?.let { message ->
                    androidx.compose.material3.Text(
                        text = message,
                        color = androidx.compose.ui.graphics.Color.Red
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