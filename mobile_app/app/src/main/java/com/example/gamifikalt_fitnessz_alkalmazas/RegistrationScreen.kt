package com.example.gamifikalt_fitnessz_alkalmazas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gamifikalt_fitnessz_alkalmazas.network.ApiClient
import com.example.gamifikalt_fitnessz_alkalmazas.network.AuthResponse
import com.example.gamifikalt_fitnessz_alkalmazas.network.RegisterRequest
import com.example.gamifikalt_fitnessz_alkalmazas.ui.theme.Gamifikalt_Fitnessz_AlkalmazasTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun RegistrationScreen(
    onRegisterSuccess: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordAgain by remember { mutableStateOf("") }

    var loading by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf<String?>(null) }

    val valid = email.contains("@") &&
            username.isNotBlank() &&
            password.length >= 8 &&
            password == passwordAgain

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Regisztráció", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("E-mail") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Felhasználónév") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Jelszó") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Min. 8 karakter",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = passwordAgain,
                onValueChange = { passwordAgain = it },
                label = { Text("Jelszó újra") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (errorText != null) {
                Text(text = errorText!!, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(12.dp))
            }

            Button(
                onClick = {
                    if (!valid) return@Button
                    loading = true
                    errorText = null

                    ApiClient.create(context)
                        .register(RegisterRequest(username.trim(), email.trim(), password))
                        .enqueue(object : Callback<AuthResponse> {
                            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                                loading = false
                                if (response.isSuccessful && response.body() != null) {
                                    onRegisterSuccess()
                                } else {
                                    errorText = "Regisztráció sikertelen"
                                }
                            }

                            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                                loading = false
                                errorText = "Hálózati hiba: ${t.message ?: "ismeretlen"}"
                            }
                        })
                },
                enabled = valid && !loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (loading) "Regisztráció..." else "Regisztráció")
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onBackClick, enabled = !loading) {
                Text("Vissza a bejelentkezéshez")
            }
        }
    }
}

@Preview
@Composable
fun RegistrationScreenPreview() {
    Gamifikalt_Fitnessz_AlkalmazasTheme {
        RegistrationScreen()
    }
}
