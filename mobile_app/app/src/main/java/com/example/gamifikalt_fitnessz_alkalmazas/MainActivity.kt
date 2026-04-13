package com.example.gamifikalt_fitnessz_alkalmazas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.gamifikalt_fitnessz_alkalmazas.network.TokenStore
import com.example.gamifikalt_fitnessz_alkalmazas.ui.theme.Gamifikalt_Fitnessz_AlkalmazasTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            var showLogin by remember { mutableStateOf(true) }
            var showRegister by remember { mutableStateOf(false) }
            var showProfile by remember { mutableStateOf(false) }
            var showActivity by remember { mutableStateOf(false) }
            var showConsumption by remember { mutableStateOf(false) }
            var showWeeklyStats by remember { mutableStateOf(false) }
            var showLeaderboards by remember { mutableStateOf(false) }
            var showLeaderboardDetail by remember { mutableStateOf(false) }
            var selectedLeaderboardId by remember { mutableIntStateOf(0) }

            Gamifikalt_Fitnessz_AlkalmazasTheme {
                when {
                    showLogin -> {
                        LoginScreen(
                            onLoginSuccess = {
                                showLogin = false
                                showRegister = false
                                showProfile = false
                                showActivity = false
                                showConsumption = false
                                showWeeklyStats = false
                                showLeaderboards = false
                                showLeaderboardDetail = false
                            },
                            onRegisterClick = {
                                showLogin = false
                                showRegister = true
                            }
                        )
                    }

                    showRegister -> {
                        RegistrationScreen(
                            onRegisterSuccess = {
                                TokenStore.clear(this@MainActivity)
                                showRegister = false
                                showProfile = false
                                showActivity = false
                                showConsumption = false
                                showWeeklyStats = false
                                showLeaderboards = false
                                showLeaderboardDetail = false
                                showLogin = true
                            },
                            onBackClick = {
                                showRegister = false
                                showLogin = true
                            }
                        )
                    }

                    showProfile -> {
                        ProfileScreen(
                            onBackClick = { showProfile = false }
                        )
                    }

                    showActivity -> {
                        ActivityScreen(
                            onBackClick = { showActivity = false }
                        )
                    }

                    showConsumption -> {
                        ConsumptionScreen(
                            onBackClick = { showConsumption = false }
                        )
                    }

                    showWeeklyStats -> {
                        WeeklyStatsScreen(
                            onBackClick = { showWeeklyStats = false }
                        )
                    }

                    showLeaderboardDetail -> {
                        LeaderboardDetailScreen(
                            leaderboardId = selectedLeaderboardId,
                            onBackClick = { showLeaderboardDetail = false }
                        )
                    }

                    showLeaderboards -> {
                        LeaderboardsScreen(
                            onBackClick = { showLeaderboards = false },
                            onOpenLeaderboard = { leaderboardId ->
                                selectedLeaderboardId = leaderboardId
                                showLeaderboards = false
                                showLeaderboardDetail = true
                            }
                        )
                    }

                    else -> {
                        HomeScreen(
                            onProfileClick = { showProfile = true },
                            onActivityClick = { showActivity = true },
                            onConsumptionClick = { showConsumption = true },
                            onWeeklyStatsClick = { showWeeklyStats = true },
                            onLeaderboardsClick = { showLeaderboards = true },
                            onLogout = {
                                TokenStore.clear(this@MainActivity)
                                showProfile = false
                                showRegister = false
                                showActivity = false
                                showConsumption = false
                                showWeeklyStats = false
                                showLeaderboards = false
                                showLeaderboardDetail = false
                                showLogin = true
                            }
                        )
                    }
                }
            }
        }
    }
}