package com.example.gamifikalt_fitnessz_alkalmazas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.gamifikalt_fitnessz_alkalmazas.ui.theme.Gamifikalt_Fitnessz_AlkalmazasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var showLogin by remember { mutableStateOf(true) }

            Gamifikalt_Fitnessz_AlkalmazasTheme {
                if (showLogin) {
                    LoginScreen(
                        onLoginClick = { email, password -> },
                        onRegisterClick = { showLogin = false }
                    )
                } else {
                    RegistrationScreen(
                        onRegisterClick = { email, password -> },
                        onBackClick = { showLogin = true }
                    )
                }
            }
        }
    }
}
