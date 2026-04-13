package com.example.gamifikalt_fitnessz_alkalmazas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import com.example.gamifikalt_fitnessz_alkalmazas.ui.theme.CardBackground
import com.example.gamifikalt_fitnessz_alkalmazas.ui.theme.ProgressGreen
import com.example.gamifikalt_fitnessz_alkalmazas.ui.theme.ProgressOrange
import com.example.gamifikalt_fitnessz_alkalmazas.ui.theme.ProgressRed
import com.example.gamifikalt_fitnessz_alkalmazas.ui.theme.ProgressYellow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun ChallengeScreen(
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var loading by remember { mutableStateOf(true) }
    var errorText by remember { mutableStateOf<String?>(null) }
    var dailyChallenge by remember { mutableStateOf<DailyChallengeResponse?>(null) }

    fun loadChallenge() {
        loading = true
        errorText = null

        ApiClient.create(context).getDailyChallenge()
            .enqueue(object : Callback<DailyChallengeResponse> {
                override fun onResponse(
                    call: Call<DailyChallengeResponse>,
                    response: Response<DailyChallengeResponse>
                ) {
                    loading = false
                    if (response.isSuccessful && response.body() != null) {
                        dailyChallenge = response.body()
                    } else {
                        errorText = "A mai kihívás betöltése sikertelen"
                    }
                }

                override fun onFailure(call: Call<DailyChallengeResponse>, t: Throwable) {
                    loading = false
                    errorText = "Hálózati hiba: ${t.message ?: "ismeretlen"}"
                }
            })
    }

    LaunchedEffect(Unit) {
        loadChallenge()
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
                text = "Mai kihívás",
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Napi extra cél és jutalom",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (loading) {
                ChallengeCard {
                    Text("Betöltés...", style = MaterialTheme.typography.bodyLarge)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (errorText != null) {
                ChallengeCard {
                    Text(
                        text = errorText!!,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            dailyChallenge?.let { challenge ->
                ChallengeCard {
                    Text(
                        text = challenge.challenge.title,
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = challenge.challenge.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Haladás",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "${challenge.progress.current} / ${challenge.progress.target}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { challenge.progress.ratio.toFloat().coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp),
                        color = challengeProgressColor(challenge.progress.ratio.toFloat()),
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Jutalom: +${challenge.challenge.reward_xp} XP",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = if (challenge.is_completed) "Kihívás teljesítve ✓" else "Még folyamatban",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (challenge.is_completed) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                ChallengeCard {
                    Text(
                        text = "Típus",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = readableChallengeType(challenge.challenge.challenge_type),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (challenge.challenge.category != null) {
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Kategória: ${readableChallengeCategory(challenge.challenge.category)}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (challenge.challenge.activity_type != null) {
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Aktivitás: ${readableChallengeActivityType(challenge.challenge.activity_type)}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onBackClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Text("Vissza", style = MaterialTheme.typography.labelLarge)
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun ChallengeCard(
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            content()
        }
    }
}

fun challengeProgressColor(progress: Float) = when {
    progress < 0.25f -> ProgressRed
    progress < 0.5f -> ProgressOrange
    progress < 0.75f -> ProgressYellow
    else -> ProgressGreen
}

fun readableChallengeType(type: String): String {
    return when (type) {
        "activity_duration" -> "Aktivitás időtartam"
        "activity_points" -> "Kategória pontcél"
        "activity_points_total" -> "Összes aktivitáspont"
        "protein_min" -> "Fehérjecél"
        "food_log_count" -> "Naplózási cél"
        else -> type
    }
}

fun readableChallengeCategory(category: String): String {
    return when (category) {
        "cardio" -> "Cardio"
        "strength" -> "Strength"
        "mental" -> "Mental"
        else -> category
    }
}

fun readableChallengeActivityType(type: String): String {
    return when (type) {
        "running" -> "Running"
        "walking" -> "Walking"
        "cycling" -> "Cycling"
        "swimming" -> "Swimming"
        "weightlifting" -> "Weightlifting"
        "bodyweight" -> "Bodyweight"
        "meditation" -> "Meditation"
        "yoga" -> "Yoga"
        "reading" -> "Reading"
        else -> type
    }
}