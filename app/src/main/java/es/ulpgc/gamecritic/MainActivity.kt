package es.ulpgc.gamecritic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import es.ulpgc.gamecritic.ui.LoginScreen
import es.ulpgc.gamecritic.ui.RegisterScreen
import es.ulpgc.gamecritic.navigation.NavGraph
import es.ulpgc.gamecritic.ui.components.BottomNavBar
import es.ulpgc.gamecritic.ui.theme.GameCriticTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GameCriticTheme {
                var isLoggedIn by remember { mutableStateOf(false) }
                var showRegister by remember { mutableStateOf(false) }
                if (!isLoggedIn) {
                    if (showRegister) {
                        RegisterScreen(
                            onRegisterSuccess = {
                                isLoggedIn = true
                            },
                            onNavigateToLogin = { showRegister = false }
                        )
                    } else {
                        LoginScreen(
                            onLoginSuccess = {
                                isLoggedIn = true
                            },
                            onNavigateToRegister = { showRegister = true }
                        )
                    }
                } else {
                    val navController = rememberNavController()
                    val currentRoute = remember { mutableStateOf("home") }
                    val onLogout: () -> Unit = {
                        showRegister = false
                        isLoggedIn = false
                    }
                    Surface {
                        Column {
                            androidx.compose.foundation.layout.Box(modifier = Modifier.weight(1f)) {
                                NavGraph(
                                    navController = navController,
                                    startDestination = currentRoute.value,
                                    onLogout = onLogout
                                )
                            }
                            BottomNavBar(selectedRoute = currentRoute.value) { route ->
                                currentRoute.value = route
                                navController.navigate(route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}