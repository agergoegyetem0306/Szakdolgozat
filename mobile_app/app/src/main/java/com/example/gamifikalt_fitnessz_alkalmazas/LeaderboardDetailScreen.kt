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
import com.example.gamifikalt_fitnessz_alkalmazas.network.LeaderboardDetailResponse
import com.example.gamifikalt_fitnessz_alkalmazas.network.LeaderboardMemberEntryResponse
import com.example.gamifikalt_fitnessz_alkalmazas.ui.theme.CardBackground
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun LeaderboardDetailScreen(
    leaderboardId: Int,
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var loading by remember { mutableStateOf(true) }
    var errorText by remember { mutableStateOf<String?>(null) }
    var detail by remember { mutableStateOf<LeaderboardDetailResponse?>(null) }

    fun loadDetail() {
        loading = true
        errorText = null

        ApiClient.create(context).getLeaderboardDetail(leaderboardId)
            .enqueue(object : Callback<LeaderboardDetailResponse> {
                override fun onResponse(
                    call: Call<LeaderboardDetailResponse>,
                    response: Response<LeaderboardDetailResponse>
                ) {
                    loading = false
                    if (response.isSuccessful && response.body() != null) {
                        detail = response.body()
                    } else {
                        errorText = "A ranglista betöltése sikertelen"
                    }
                }

                override fun onFailure(call: Call<LeaderboardDetailResponse>, t: Throwable) {
                    loading = false
                    errorText = "Hálózati hiba: ${t.message ?: "ismeretlen"}"
                }
            })
    }

    LaunchedEffect(leaderboardId) {
        loadDetail()
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
                text = "Ranglista",
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Heti XP szerinti sorrend",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (loading) {
                LeaderboardDetailCard {
                    Text("Betöltés...", style = MaterialTheme.typography.bodyLarge)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (errorText != null) {
                LeaderboardDetailCard {
                    Text(
                        text = errorText!!,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            detail?.let { data ->
                LeaderboardDetailCard {
                    Text(
                        text = data.leaderboard.name,
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Csatlakozási kód: ${data.leaderboard.join_code}",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Létrehozó: ${data.leaderboard.creator_name ?: "Ismeretlen"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Tagok száma: ${data.leaderboard.member_count}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Hét: ${formatWeekRange(data.leaderboard.week_start, data.leaderboard.week_end)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Saját helyezés: ${data.my_rank ?: "-"}",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Toplista",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(12.dp))

                data.members.forEach { member ->
                    LeaderboardMemberCard(member = member)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

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
fun LeaderboardDetailCard(
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

@Composable
fun LeaderboardMemberCard(
    member: LeaderboardMemberEntryResponse
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (member.is_me) MaterialTheme.colorScheme.primary else CardBackground
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "${member.rank}. hely  ${member.name}",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (member.is_me) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (member.is_me) "Te" else "Résztvevő",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (member.is_me) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = "${member.weekly_xp} XP",
                style = MaterialTheme.typography.titleMedium,
                color = if (member.is_me) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

fun formatWeekRange(start: String, end: String): String {
    return try {
        "${start.takeLast(5).replace("-", ".")} - ${end.takeLast(5).replace("-", ".")}"
    } catch (_: Exception) {
        "$start - $end"
    }
}