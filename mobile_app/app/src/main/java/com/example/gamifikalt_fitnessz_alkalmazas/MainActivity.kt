package com.example.gamifikalt_fitnessz_alkalmazas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.gamifikalt_fitnessz_alkalmazas.network.TokenStore
import com.example.gamifikalt_fitnessz_alkalmazas.ui.theme.Gamifikalt_Fitnessz_AlkalmazasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current

            var showLogin by remember { mutableStateOf(true) }
            var loggedIn by remember { mutableStateOf(false) }

            Gamifikalt_Fitnessz_AlkalmazasTheme {
                when {
                    loggedIn -> {
                        HomeScreen(
                            onLogout = {
                                TokenStore.clear(context)
                                loggedIn = false
                                showLogin = true
                            }
                        )
                    }

                    showLogin -> {
                        LoginScreen(
                            onLoginSuccess = {
                                loggedIn = true
                            },
                            onRegisterClick = { showLogin = false }
                        )
                    }

                    else -> {
                        RegistrationScreen(
                            onRegisterSuccess = {showLogin = true },
                            onBackClick = { showLogin = true }
                        )
                    }
                }
            }
        }
    }
}
