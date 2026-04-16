package com.example.gamifikalt_fitnessz_alkalmazas

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gamifikalt_fitnessz_alkalmazas.network.ApiClient
import com.example.gamifikalt_fitnessz_alkalmazas.network.LoginRequest
import com.example.gamifikalt_fitnessz_alkalmazas.network.TokenStore
import com.example.gamifikalt_fitnessz_alkalmazas.network.AuthResponse
import com.example.gamifikalt_fitnessz_alkalmazas.ui.theme.Gamifikalt_Fitnessz_AlkalmazasTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit = {},
    onRegisterClick: () -> Unit = {}
) {
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var loading by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf<String?>(null) }

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

            Image(
                painter = painterResource(id = R.drawable.lvlup_logo),
                contentDescription = "LvlUp Logo",
                modifier = Modifier.size(180.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            Card(
                shape = RoundedCornerShape(28.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                ),
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(24.dp)
                ) {

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
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

                    Spacer(modifier = Modifier.height(24.dp))

                    if (errorText != null) {
                        Text(
                            text = errorText!!,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    Button(
                        onClick = {

                            errorText = null
                            loading = true

                            val api = ApiClient.create(context)

                            api.login(LoginRequest(email.trim(), password))
                                .enqueue(object : Callback<AuthResponse> {

                                    override fun onResponse(
                                        call: Call<AuthResponse>,
                                        response: Response<AuthResponse>
                                    ) {

                                        loading = false

                                        if (response.isSuccessful && response.body() != null) {
                                            TokenStore.save(context, response.body()!!.token)
                                            onLoginSuccess()
                                        } else {
                                            errorText = "Hibás email vagy jelszó"
                                        }
                                    }

                                    override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                                        loading = false
                                        errorText = "Hálózati hiba: ${t.message ?: "ismeretlen"}"
                                    }
                                })
                        },
                        enabled = !loading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(if (loading) "Bejelentkezés..." else "Bejelentkezés")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(
                        onClick = onRegisterClick,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Még nincs profilod? Regisztrálj")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    Gamifikalt_Fitnessz_AlkalmazasTheme {
        LoginScreen()
    }
}