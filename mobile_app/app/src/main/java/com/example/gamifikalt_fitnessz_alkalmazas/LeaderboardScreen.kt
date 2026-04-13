package com.example.gamifikalt_fitnessz_alkalmazas

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.gamifikalt_fitnessz_alkalmazas.network.CreateLeaderboardRequest
import com.example.gamifikalt_fitnessz_alkalmazas.network.CreateLeaderboardResponse
import com.example.gamifikalt_fitnessz_alkalmazas.network.JoinLeaderboardRequest
import com.example.gamifikalt_fitnessz_alkalmazas.network.JoinLeaderboardResponse
import com.example.gamifikalt_fitnessz_alkalmazas.network.LeaderboardListItemResponse
import com.example.gamifikalt_fitnessz_alkalmazas.ui.theme.CardBackground
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun LeaderboardsScreen(
    onBackClick: () -> Unit = {},
    onOpenLeaderboard: (Int) -> Unit = {}
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var loading by remember { mutableStateOf(true) }
    var errorText by remember { mutableStateOf<String?>(null) }
    var successText by remember { mutableStateOf<String?>(null) }
    var leaderboards by remember { mutableStateOf<List<LeaderboardListItemResponse>>(emptyList()) }

    var showCreateDialog by remember { mutableStateOf(false) }
    var showJoinDialog by remember { mutableStateOf(false) }

    fun loadLeaderboards() {
        loading = true
        errorText = null

        ApiClient.create(context).getLeaderboards()
            .enqueue(object : Callback<List<LeaderboardListItemResponse>> {
                override fun onResponse(
                    call: Call<List<LeaderboardListItemResponse>>,
                    response: Response<List<LeaderboardListItemResponse>>
                ) {
                    loading = false
                    if (response.isSuccessful && response.body() != null) {
                        leaderboards = response.body()!!
                    } else {
                        errorText = "A ranglisták betöltése sikertelen"
                    }
                }

                override fun onFailure(call: Call<List<LeaderboardListItemResponse>>, t: Throwable) {
                    loading = false
                    errorText = "Hálózati hiba: ${t.message ?: "ismeretlen"}"
                }
            })
    }

    LaunchedEffect(Unit) {
        loadLeaderboards()
    }

    if (showCreateDialog) {
        CreateLeaderboardDialog(
            onDismiss = { showCreateDialog = false },
            onSave = { name, joinCode ->
                ApiClient.create(context)
                    .createLeaderboard(CreateLeaderboardRequest(name = name, join_code = joinCode))
                    .enqueue(object : Callback<CreateLeaderboardResponse> {
                        override fun onResponse(
                            call: Call<CreateLeaderboardResponse>,
                            response: Response<CreateLeaderboardResponse>
                        ) {
                            if (response.isSuccessful && response.body() != null) {
                                showCreateDialog = false
                                successText = "A ranglista sikeresen létrejött"
                                loadLeaderboards()
                            } else {
                                errorText = "A ranglista létrehozása sikertelen"
                            }
                        }

                        override fun onFailure(call: Call<CreateLeaderboardResponse>, t: Throwable) {
                            errorText = "Hálózati hiba: ${t.message ?: "ismeretlen"}"
                        }
                    })
            }
        )
    }

    if (showJoinDialog) {
        JoinLeaderboardDialog(
            onDismiss = { showJoinDialog = false },
            onSave = { joinCode ->
                ApiClient.create(context)
                    .joinLeaderboard(JoinLeaderboardRequest(join_code = joinCode))
                    .enqueue(object : Callback<JoinLeaderboardResponse> {
                        override fun onResponse(
                            call: Call<JoinLeaderboardResponse>,
                            response: Response<JoinLeaderboardResponse>
                        ) {
                            if (response.isSuccessful && response.body() != null) {
                                showJoinDialog = false
                                successText = "Sikeresen csatlakoztál a ranglistához"
                                loadLeaderboards()
                            } else {
                                errorText = "A csatlakozás sikertelen"
                            }
                        }

                        override fun onFailure(call: Call<JoinLeaderboardResponse>, t: Throwable) {
                            errorText = "Hálózati hiba: ${t.message ?: "ismeretlen"}"
                        }
                    })
            }
        )
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
                text = "Ranglisták",
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Heti XP alapú versenyek",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (loading) {
                LeaderboardCard {
                    Text("Betöltés...", style = MaterialTheme.typography.bodyLarge)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (errorText != null) {
                LeaderboardCard {
                    Text(
                        text = errorText!!,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (successText != null) {
                LeaderboardCard {
                    Text(
                        text = successText!!,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            LeaderboardCard {
                Text(
                    text = "Műveletek",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        errorText = null
                        successText = null
                        showCreateDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text("Új ranglista létrehozása", style = MaterialTheme.typography.labelLarge)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        errorText = null
                        successText = null
                        showJoinDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Text("Csatlakozás kóddal", style = MaterialTheme.typography.labelLarge)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (leaderboards.isEmpty() && !loading) {
                LeaderboardCard {
                    Text(
                        text = "Még nem vagy benne egyetlen ranglistában sem.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                Text(
                    text = "Saját ranglistáim",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(12.dp))

                leaderboards.forEach { leaderboard ->
                    LeaderboardListItemCard(
                        leaderboard = leaderboard,
                        onClick = { onOpenLeaderboard(leaderboard.id) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

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
fun LeaderboardCard(
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
fun LeaderboardListItemCard(
    leaderboard: LeaderboardListItemResponse,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = leaderboard.name,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Csatlakozási kód: ${leaderboard.join_code}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Tagok száma: ${leaderboard.members_count}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Megnyitás",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun CreateLeaderboardDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, joinCode: String?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var joinCode by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf<String?>(null) }
    var joinCodeError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Új ranglista") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = null
                    },
                    label = { Text("Ranglista neve") },
                    singleLine = true,
                    isError = nameError != null,
                    modifier = Modifier.fillMaxWidth()
                )

                if (nameError != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = nameError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = joinCode,
                    onValueChange = {
                        joinCode = it.uppercase()
                        joinCodeError = null
                    },
                    label = { Text("Csatlakozási kód (opcionális)") },
                    singleLine = true,
                    isError = joinCodeError != null,
                    modifier = Modifier.fillMaxWidth()
                )

                if (joinCodeError != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = joinCodeError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    nameError = null
                    joinCodeError = null

                    var valid = true

                    if (name.isBlank()) {
                        nameError = "Add meg a ranglista nevét"
                        valid = false
                    }

                    if (joinCode.isNotBlank() && joinCode.length < 4) {
                        joinCodeError = "A kód legalább 4 karakter legyen"
                        valid = false
                    }

                    if (!valid) return@Button

                    onSave(name.trim(), joinCode.trim().ifBlank { null })
                },
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Létrehozás")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Mégse")
            }
        }
    )
}

@Composable
fun JoinLeaderboardDialog(
    onDismiss: () -> Unit,
    onSave: (joinCode: String) -> Unit
) {
    var joinCode by remember { mutableStateOf("") }
    var joinCodeError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Csatlakozás ranglistához") },
        text = {
            Column {
                OutlinedTextField(
                    value = joinCode,
                    onValueChange = {
                        joinCode = it.uppercase()
                        joinCodeError = null
                    },
                    label = { Text("Csatlakozási kód") },
                    singleLine = true,
                    isError = joinCodeError != null,
                    modifier = Modifier.fillMaxWidth()
                )

                if (joinCodeError != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = joinCodeError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    joinCodeError = null

                    if (joinCode.isBlank()) {
                        joinCodeError = "Add meg a csatlakozási kódot"
                        return@Button
                    }

                    onSave(joinCode.trim())
                },
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Csatlakozás")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Mégse")
            }
        }
    )
}