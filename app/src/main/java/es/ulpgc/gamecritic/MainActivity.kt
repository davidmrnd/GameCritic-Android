package es.ulpgc.gamecritic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import es.ulpgc.gamecritic.auth.LoginScreen
import es.ulpgc.gamecritic.ui.theme.GameCriticTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GameCriticTheme {
                var isLoggedIn by remember { mutableStateOf(false) }
                    if (!isLoggedIn) {
                        LoginScreen(
                            onLoginSuccess = { isLoggedIn = true }
                        )
                    } else {
                        HomeScreen()
                    }
            }
        }
    }
}