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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.example.gamifikalt_fitnessz_alkalmazas.network.ProfileResponse
import com.example.gamifikalt_fitnessz_alkalmazas.network.UpdateProfileRequest
import com.example.gamifikalt_fitnessz_alkalmazas.ui.theme.CardBackground
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun ProfileScreen(
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var gender by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }

    var loading by remember { mutableStateOf(true) }
    var messageText by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        ApiClient.create(context).getProfile().enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(
                call: Call<ProfileResponse>,
                response: Response<ProfileResponse>
            ) {
                loading = false

                if (response.isSuccessful && response.body() != null) {
                    val profile = response.body()!!
                    gender = profile.gender ?: ""
                    age = profile.age?.toString() ?: ""
                    height = profile.height?.toString() ?: ""
                    weight = profile.weight?.toString() ?: ""
                } else {
                    messageText = "Profil betöltése sikertelen"
                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                loading = false
                messageText = "Hálózati hiba: ${t.message ?: "ismeretlen"}"
            }
        })
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
                text = "Profil",
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Személyes adatok kezelése",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (loading) {
                ProfileCard {
                    Text("Betöltés...", style = MaterialTheme.typography.bodyLarge)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (messageText != null) {
                ProfileCard {
                    Text(
                        text = messageText!!,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (messageText == "Profil mentve!") {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.error
                        }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            ProfileCard {
                Text(
                    text = "Alapadatok",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(18.dp))

                OutlinedTextField(
                    value = when (gender) {
                        "male" -> "Férfi"
                        "female" -> "Nő"
                        else -> ""
                    },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Nem") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it },
                    label = { Text("Kor") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = height,
                    onValueChange = { height = it },
                    label = { Text("Magasság (cm)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("Súly (kg)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val request = UpdateProfileRequest(
                        gender = gender.ifBlank { null },
                        age = age.toIntOrNull(),
                        height = height.toIntOrNull(),
                        weight = weight.toDoubleOrNull()
                    )

                    ApiClient.create(context).updateProfile(request)
                        .enqueue(object : Callback<ProfileResponse> {
                            override fun onResponse(
                                call: Call<ProfileResponse>,
                                response: Response<ProfileResponse>
                            ) {
                                messageText = if (response.isSuccessful) {
                                    "Profil mentve!"
                                } else {
                                    "Mentés sikertelen"
                                }
                            }

                            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                                messageText = "Hálózati hiba"
                            }
                        })
                },
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
                    text = "Mentés",
                    style = MaterialTheme.typography.labelLarge
                )
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
                Text(
                    text = "Vissza",
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun ProfileCard(
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