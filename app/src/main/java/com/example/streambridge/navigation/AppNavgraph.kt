package com.example.streambridge.navigation




import androidx.compose.runtime.Composable

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

@Composable
fun AppNavGraph() {

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

                SplashScreen(
                    onGetStarted = {
                        backStack.add(Login)
                    },
                    onLogin = {
                        backStack.add(Login)
                    }
                )
            }

            entry<Login> {

                LoginScreen(
                    onLogin = {
                        backStack.add(Home)
                    },
                    onSignUp = {
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
                    onCreateSession = {
                        backStack.add(CreateSession)
                    },
                    onJoinSession = {
                      backStack.add(JoinSession)
                    },
                    onSessionClick = {
                        session -> backStack.add(
                            SessionDetails(
                                sessionId = session.title
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
                    onBack = {
                        backStack.removeLastOrNull()
                    },
                    onCreate = {
                      backStack.add(SessionLobby)
                    }
                )
            }

            entry<SessionLobby> {

                SessionLobbyScreen(
                    onBack = {
                        backStack.removeLastOrNull()
                    },
                    onStartSession = {
                        backStack.add(ParticipantLive)
                    },
                    onShare = {
                        // Share later
                    },
                    onCopyCode = {
                        // Copy later
                    }
                )
            }

            entry<HostLive> {

                HostLiveScreen(
                    onEndSession = {
                        backStack.add(SessionSummary)
                    },
                    onChat = {
                        // Chat screen later
                    },
                    onAskRequests = {
                        backStack.add(AskRequests)
                    },
                    onParticipants = {
                        // Participants panel later
                    }
                )
            }

            entry<ParticipantLive> {

                ParticipantLiveScreen(
                    onLeaveSession = {
                        backStack.add(SessionSummary)
                    },
                    onChat = {
                       backStack.add(Chat)
                    },
                    onAsk = {
                        backStack.add(AskRequest)
                    },
                    onParticipants = {
                        backStack.add(Participants)
                    }
                )
            }

            entry<Participants> {
                ParticipantsScreen(
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

            entry<SessionSummary> {
                SessionSummaryScreen(
                    onBackHome = {
                        backStack.add(Home)
                    }
                )
            }

            entry<JoinSession> {
                JoinSessionScreen(
                    onBack = {
                        backStack.removeLastOrNull()
                    },
                    onJoin = {
                        backStack.add(ParticipantLive)
                    }
                )
            }

            entry<Profile> {
                ProfileScreen(
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

            entry<SessionDetails> {
                SessionDetailsScreen(
                    sessionName = "Kotlin Study Group",
                    description = "Learn Kotlin and modern Android development together.",
                    participants = 12,
                    isPrivate = false,

                    onBack = {
                        backStack.removeLastOrNull()
                    },

                    onJoin = {
                        backStack.add(ParticipantLive)
                    }
                )
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