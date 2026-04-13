package com.example.gamifikalt_fitnessz_alkalmazas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.gamifikalt_fitnessz_alkalmazas.network.ApiClient
import com.example.gamifikalt_fitnessz_alkalmazas.network.DailyChallengeResponse
import com.example.gamifikalt_fitnessz_alkalmazas.network.XpSummaryResponse
import com.example.gamifikalt_fitnessz_alkalmazas.ui.theme.CardBackground
import com.example.gamifikalt_fitnessz_alkalmazas.ui.theme.ProgressGreen
import com.example.gamifikalt_fitnessz_alkalmazas.ui.theme.ProgressOrange
import com.example.gamifikalt_fitnessz_alkalmazas.ui.theme.ProgressRed
import com.example.gamifikalt_fitnessz_alkalmazas.ui.theme.ProgressYellow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun HomeScreen(
    onProfileClick: () -> Unit = {},
    onActivityClick: () -> Unit = {},
    onConsumptionClick: () -> Unit = {},
    onWeeklyStatsClick: () -> Unit = {},
    onLeaderboardsClick: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var loading by remember { mutableStateOf(true) }
    var errorText by remember { mutableStateOf<String?>(null) }
    var xpSummary by remember { mutableStateOf<XpSummaryResponse?>(null) }
    var dailyChallenge by remember { mutableStateOf<DailyChallengeResponse?>(null) }

    fun loadDashboard() {
        loading = true
        errorText = null

        ApiClient.create(context).getXpSummary()
            .enqueue(object : Callback<XpSummaryResponse> {
                override fun onResponse(
                    call: Call<XpSummaryResponse>,
                    response: Response<XpSummaryResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        xpSummary = response.body()
                    } else {
                        errorText = "Az XP adatok betöltése sikertelen"
                    }

                    ApiClient.create(context).getDailyChallenge()
                        .enqueue(object : Callback<DailyChallengeResponse> {
                            override fun onResponse(
                                call: Call<DailyChallengeResponse>,
                                response: Response<DailyChallengeResponse>
                            ) {
                                loading = false
                                if (response.isSuccessful && response.body() != null) {
                                    dailyChallenge = response.body()
                                } else if (errorText == null) {
                                    errorText = "A napi kihívás betöltése sikertelen"
                                }
                            }

                            override fun onFailure(call: Call<DailyChallengeResponse>, t: Throwable) {
                                loading = false
                                if (errorText == null) {
                                    errorText = "Hálózati hiba: ${t.message ?: "ismeretlen"}"
                                }
                            }
                        })
                }

                override fun onFailure(call: Call<XpSummaryResponse>, t: Throwable) {
                    loading = false
                    errorText = "Hálózati hiba: ${t.message ?: "ismeretlen"}"
                }
            })
    }

    LaunchedEffect(Unit) {
        loadDashboard()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(20.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "LvlUp!",
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Napi áttekintés",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (loading) {
                DashboardCard {
                    Text("Betöltés...", style = MaterialTheme.typography.bodyLarge)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (errorText != null) {
                DashboardCard {
                    Text(
                        text = errorText!!,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            xpSummary?.let { xp ->
                val levelRange = (xp.next_level_xp - xp.current_level_xp_min).coerceAtLeast(1)
                val levelProgress = xp.xp_in_level.toFloat() / levelRange.toFloat()

                val activityProgress = (xp.today_activity_xp / 100f).coerceIn(0f, 1f)
                val nutritionProgress = (xp.today_nutrition_xp / 100f).coerceIn(0f, 1f)

                DashboardCard {
                    Text(
                        text = "Szint ${xp.level}",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Össz XP: ${xp.xp}",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Következő szint: ${xp.next_level_xp} XP",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Még szükséges: ${xp.xp_needed_for_next_level} XP",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    LinearProgressIndicator(
                        progress = { levelProgress.coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Streak",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "${xp.current_streak}",
                                style = MaterialTheme.typography.headlineMedium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Legjobb: ${xp.best_streak} nap",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Column {
                            Text(
                                text = "Mai össz XP",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "${xp.today_total_xp}",
                                style = MaterialTheme.typography.headlineMedium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Mai eredmény",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                DashboardCard {
                    Text(
                        text = "Mai teljesítmény",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Aktivitás",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "${xp.today_activity_xp} / 100 XP",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { activityProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp),
                        color = progressColor(activityProgress),
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "Táplálkozás",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "${xp.today_nutrition_xp} / 100 XP",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { nutritionProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp),
                        color = progressColor(nutritionProgress),
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            if (dailyChallenge != null) {
                DashboardCard {
                    Text(
                        text = "Mai kihívás",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = dailyChallenge!!.challenge.title,
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = dailyChallenge!!.challenge.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "${dailyChallenge!!.progress.current} / ${dailyChallenge!!.progress.target}",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { dailyChallenge!!.progress.ratio.toFloat().coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp),
                        color = progressColor(dailyChallenge!!.progress.ratio.toFloat()),
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (dailyChallenge!!.is_completed) "Teljesítve ✓" else "Folyamatban",
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (dailyChallenge!!.is_completed) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            DashboardCard {
                Text(
                    text = "Gyorsmenü",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(12.dp))

                HorizontalDivider(color = MaterialTheme.colorScheme.outline)

                Spacer(modifier = Modifier.height(16.dp))

                DashboardButton(
                    text = "Aktivitás",
                    onClick = onActivityClick
                )

                Spacer(modifier = Modifier.height(12.dp))

                DashboardButton(
                    text = "Fogyasztás",
                    onClick = onConsumptionClick
                )

                Spacer(modifier = Modifier.height(12.dp))

                DashboardButton(
                    text = "Ranglisták",
                    onClick = onLeaderboardsClick
                )

                Spacer(modifier = Modifier.height(12.dp))

                DashboardButton(
                    text = "Heti statisztika",
                    onClick = onWeeklyStatsClick
                )

                Spacer(modifier = Modifier.height(12.dp))

                DashboardButton(
                    text = "Profil",
                    onClick = onProfileClick
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = "Kijelentkezés",
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun DashboardCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        tonalElevation = 2.dp,
        shadowElevation = 2.dp,
        color = CardBackground
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            content()
        }
    }
}

@Composable
fun DashboardButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

fun progressColor(progress: Float) = when {
    progress < 0.25f -> ProgressRed
    progress < 0.5f -> ProgressOrange
    progress < 0.75f -> ProgressYellow
    else -> ProgressGreen
}