package com.example.gamifikalt_fitnessz_alkalmazas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun RegistrationScreen(
    onRegisterSuccess: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordAgain by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }

    var loading by remember { mutableStateOf(false) }
    var generalError by remember { mutableStateOf<String?>(null) }
    var successText by remember { mutableStateOf<String?>(null) }

    var emailError by remember { mutableStateOf<String?>(null) }
    var usernameError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var passwordAgainError by remember { mutableStateOf<String?>(null) }
    var genderError by remember { mutableStateOf<String?>(null) }
    var ageError by remember { mutableStateOf<String?>(null) }
    var heightError by remember { mutableStateOf<String?>(null) }
    var weightError by remember { mutableStateOf<String?>(null) }

    fun clearErrors() {
        generalError = null
        emailError = null
        usernameError = null
        passwordError = null
        passwordAgainError = null
        genderError = null
        ageError = null
        heightError = null
        weightError = null
    }

    fun firstErrorFromJson(errorBody: String?, field: String): String? {
        if (errorBody.isNullOrBlank()) return null
        return try {
            val root = JSONObject(errorBody)
            val errors = root.optJSONObject("errors") ?: return null
            val array = errors.optJSONArray(field) ?: return null
            if (array.length() > 0) array.optString(0) else null
        } catch (_: Exception) {
            null
        }
    }

    fun translateError(field: String, backendMessage: String?): String? {
        if (backendMessage.isNullOrBlank()) return null

        return when (field) {
            "email" -> when {
                backendMessage.contains("already been taken", ignoreCase = true) ->
                    "Ez az e-mail cím már használatban van"
                backendMessage.contains("required", ignoreCase = true) ->
                    "Az e-mail megadása kötelező"
                backendMessage.contains("email", ignoreCase = true) ->
                    "Adj meg egy érvényes e-mail címet"
                else -> backendMessage
            }

            "name" -> when {
                backendMessage.contains("required", ignoreCase = true) ->
                    "A felhasználónév megadása kötelező"
                else -> backendMessage
            }

            "password" -> when {
                backendMessage.contains("required", ignoreCase = true) ->
                    "A jelszó megadása kötelező"
                backendMessage.contains("min", ignoreCase = true) ->
                    "A jelszónak legalább 8 karakter hosszúnak kell lennie"
                else -> backendMessage
            }

            "gender" -> when {
                backendMessage.contains("required", ignoreCase = true) ->
                    "A nem kiválasztása kötelező"
                backendMessage.contains("in", ignoreCase = true) ->
                    "Csak Férfi vagy Nő választható"
                else -> backendMessage
            }

            "age" -> when {
                backendMessage.contains("required", ignoreCase = true) ->
                    "A kor megadása kötelező"
                else -> backendMessage
            }

            "height" -> when {
                backendMessage.contains("required", ignoreCase = true) ->
                    "A magasság megadása kötelező"
                else -> backendMessage
            }

            "weight" -> when {
                backendMessage.contains("required", ignoreCase = true) ->
                    "A súly megadása kötelező"
                else -> backendMessage
            }

            else -> backendMessage
        }
    }

    fun validateLocal(): Boolean {
        clearErrors()
        successText = null
        var valid = true

        if (!email.contains("@")) {
            emailError = "Adj meg egy érvényes e-mail címet"
            valid = false
        }

        if (username.isBlank()) {
            usernameError = "A felhasználónév megadása kötelező"
            valid = false
        }

        if (password.length < 8) {
            passwordError = "A jelszónak legalább 8 karakter hosszúnak kell lennie"
            valid = false
        }

        if (passwordAgain != password) {
            passwordAgainError = "A két jelszó nem egyezik"
            valid = false
        }

        if (gender.isBlank()) {
            genderError = "A nem kiválasztása kötelező"
            valid = false
        }

        if (age.toIntOrNull() == null) {
            ageError = "Adj meg egy érvényes kort"
            valid = false
        }

        if (height.toIntOrNull() == null) {
            heightError = "Adj meg egy érvényes magasságot"
            valid = false
        }

        if (weight.toDoubleOrNull() == null) {
            weightError = "Adj meg egy érvényes súlyt"
            valid = false
        }

        return valid
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                shape = RoundedCornerShape(28.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "Regisztráció",
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            emailError = null
                            generalError = null
                            successText = null
                        },
                        label = { Text("E-mail") },
                        singleLine = true,
                        isError = emailError != null,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (emailError != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = emailError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = username,
                        onValueChange = {
                            username = it
                            usernameError = null
                            generalError = null
                            successText = null
                        },
                        label = { Text("Felhasználónév") },
                        singleLine = true,
                        isError = usernameError != null,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (usernameError != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = usernameError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            passwordError = null
                            generalError = null
                            successText = null
                        },
                        label = { Text("Jelszó") },
                        singleLine = true,
                        isError = passwordError != null,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Min. 8 karakter",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (passwordError != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = passwordError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = passwordAgain,
                        onValueChange = {
                            passwordAgain = it
                            passwordAgainError = null
                            generalError = null
                            successText = null
                        },
                        label = { Text("Jelszó újra") },
                        singleLine = true,
                        isError = passwordAgainError != null,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (passwordAgainError != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = passwordAgainError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Nem",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = {
                                gender = "male"
                                genderError = null
                                generalError = null
                                successText = null
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Férfi")
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        OutlinedButton(
                            onClick = {
                                gender = "female"
                                genderError = null
                                generalError = null
                                successText = null
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Nő")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = when (gender) {
                            "male" -> "Férfi"
                            "female" -> "Nő"
                            else -> ""
                        },
                        onValueChange = {},
                        readOnly = true,
                        isError = genderError != null,
                        label = { Text("Kiválasztott nem") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (genderError != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = genderError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = age,
                        onValueChange = {
                            age = it
                            ageError = null
                            generalError = null
                            successText = null
                        },
                        label = { Text("Kor") },
                        singleLine = true,
                        isError = ageError != null,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (ageError != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = ageError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = height,
                        onValueChange = {
                            height = it
                            heightError = null
                            generalError = null
                            successText = null
                        },
                        label = { Text("Magasság (cm)") },
                        singleLine = true,
                        isError = heightError != null,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (heightError != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = heightError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = weight,
                        onValueChange = {
                            weight = it
                            weightError = null
                            generalError = null
                            successText = null
                        },
                        label = { Text("Súly (kg)") },
                        singleLine = true,
                        isError = weightError != null,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (weightError != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = weightError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    if (generalError != null) {
                        Text(
                            text = generalError!!,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    if (successText != null) {
                        Text(
                            text = successText!!,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    Button(
                        onClick = {
                            if (!validateLocal()) return@Button

                            loading = true
                            generalError = null
                            successText = null

                            ApiClient.create(context)
                                .register(
                                    RegisterRequest(
                                        name = username.trim(),
                                        email = email.trim(),
                                        password = password,
                                        gender = gender,
                                        age = age.toInt(),
                                        height = height.toInt(),
                                        weight = weight.toDouble()
                                    )
                                )
                                .enqueue(object : Callback<AuthResponse> {
                                    override fun onResponse(
                                        call: Call<AuthResponse>,
                                        response: Response<AuthResponse>
                                    ) {
                                        loading = false

                                        if (response.isSuccessful && response.body() != null) {
                                            successText = "Sikeres regisztráció!"
                                            scope.launch {
                                                delay(1200)
                                                onRegisterSuccess()
                                            }
                                        } else {
                                            clearErrors()

                                            val errorBody = response.errorBody()?.string()

                                            emailError = translateError(
                                                "email",
                                                firstErrorFromJson(errorBody, "email")
                                            )

                                            usernameError = translateError(
                                                "name",
                                                firstErrorFromJson(errorBody, "name")
                                            )

                                            passwordError = translateError(
                                                "password",
                                                firstErrorFromJson(errorBody, "password")
                                            )

                                            genderError = translateError(
                                                "gender",
                                                firstErrorFromJson(errorBody, "gender")
                                            )

                                            ageError = translateError(
                                                "age",
                                                firstErrorFromJson(errorBody, "age")
                                            )

                                            heightError = translateError(
                                                "height",
                                                firstErrorFromJson(errorBody, "height")
                                            )

                                            weightError = translateError(
                                                "weight",
                                                firstErrorFromJson(errorBody, "weight")
                                            )

                                            if (
                                                emailError == null &&
                                                usernameError == null &&
                                                passwordError == null &&
                                                genderError == null &&
                                                ageError == null &&
                                                heightError == null &&
                                                weightError == null
                                            ) {
                                                generalError = "Regisztráció sikertelen"
                                            }
                                        }
                                    }

                                    override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                                        loading = false
                                        generalError = "Hálózati hiba: ${t.message ?: "ismeretlen"}"
                                    }
                                })
                        },
                        enabled = !loading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(if (loading) "Regisztráció..." else "Regisztráció")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(
                        onClick = onBackClick,
                        enabled = !loading,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Vissza a bejelentkezéshez")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegistrationScreenPreview() {
    Gamifikalt_Fitnessz_AlkalmazasTheme {
        RegistrationScreen()
    }
}