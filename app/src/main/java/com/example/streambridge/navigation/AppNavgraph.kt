package com.example.streambridge.navigation





import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
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
import com.example.streambridge.ui.screens.summary.SessionSummaryScreen
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import com.example.streambridge.data.local.SessionManager
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
                            backStack.add(
                                SessionLobby(sessionId)
                            )
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
                    LaunchedEffect(currentSession.status) {
                        if (currentSession.status == "LIVE") {
                            backStack.add(
                                HostLive(lobby.sessionId)
                            )
                        }
                    }
                    SessionLobbyScreen(
                        session = currentSession,
                        onBack = {
                            backStack.removeLastOrNull()
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

            entry<HostLive> {

                hostLive ->val sessionViewModel: SessionViewModel = viewModel()

                val session by sessionViewModel.session.collectAsState()
                val userSession by sessionManager.session.collectAsState(initial = null)
                LaunchedEffect(hostLive.sessionId) {
                    sessionViewModel.getSession(hostLive.sessionId)
                }

                session?.let { currentSession ->
                    LaunchedEffect(currentSession.status) {
                        if (currentSession.status == "ENDED") {
                            backStack.add(
                                SessionSummary(hostLive.sessionId)
                            )
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
                            backStack.add(Chat)
                        },

                        onAskRequests = {
                            backStack.add(AskRequests)
                        },

                        onParticipants = {
                            backStack.add(
                                Participants(hostLive.sessionId)
                            )
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
                                    backStack.add(
                                        SessionSummary(participantLive.sessionId)
                                    )
                                }
                            )
                        }
                    },
                    onChat = {
                        backStack.add(Chat)
                    },
                    onAsk = {
                        backStack.add(AskRequest)
                    },
                    onParticipants = {
                        backStack.add(
                            Participants(participantLive.sessionId)
                        )
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

            entry<Participants> { participantsEntry ->
                ParticipantsScreen(
                    sessionId = participantsEntry.sessionId,
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
            entry<Chat> {
                ChatScreen(
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }

            entry<AskRequest> {
                AskRequestScreen(
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }

            entry<AskRequests> {
                AskRequestsScreen(
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
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

            entry<SessionDetails>   { details ->

                val participantViewModel: ParticipantViewModel = viewModel()
                val userSession by sessionManager.session.collectAsState(initial = null)
                val myParticipant by participantViewModel.myParticipant.collectAsState()
                val isLoading by participantViewModel.isLoading.collectAsState()
                val error by participantViewModel.error.collectAsState()

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

                SessionDetailsScreen(
                    sessionName = "Kotlin Study Group",
                    description = "Learn Kotlin and modern Android development together.",
                    participants = 0,
                    isPrivate = false,

                    onBack = {
                        backStack.removeLastOrNull()
                    },

                    onJoin = {
                        val userId = userSession?.userId

                        if (userId.isNullOrBlank()) {
                            // User session missing; don't send an invalid request.
                            participantViewModel.clearError()
                        } else if (!isLoading) {
                            participantViewModel.joinSession(
                                sessionId = details.sessionId,
                                userId = userId
                            )
                        }
                    }
                )

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