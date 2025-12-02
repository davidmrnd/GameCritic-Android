package es.ulpgc.gamecritic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import es.ulpgc.gamecritic.navigation.NavGraph
import es.ulpgc.gamecritic.ui.LoginScreen
import es.ulpgc.gamecritic.ui.RegisterScreen
import es.ulpgc.gamecritic.ui.components.BottomNavBar
import es.ulpgc.gamecritic.ui.theme.GameCriticTheme
import androidx.navigation.NavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val auth = FirebaseAuth.getInstance()

        setContent {
            GameCriticTheme {
                var isLoggedIn by remember { mutableStateOf(auth.currentUser != null) }
                var showRegister by remember { mutableStateOf(false) }

                if (!isLoggedIn) {
                    if (showRegister) {
                        RegisterScreen(
                            onRegisterSuccess = {
                                isLoggedIn = true
                                showRegister = false
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

                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    LaunchedEffect(navBackStackEntry) {
                        val r = navBackStackEntry?.destination?.route
                        val userIdArg = navBackStackEntry?.arguments?.getString("userId")
                        val currentUid = auth.currentUser?.uid
                        currentRoute.value = when {
                            r != null && (r == "home" || r == "search" || r == "following" || r == "profile") -> r
                            r != null && r.startsWith("user_profile") && userIdArg != null && currentUid != null && userIdArg == currentUid -> "profile"
                            else -> r ?: ""
                        }
                    }

                    DisposableEffect(navController) {
                        val listener = NavController.OnDestinationChangedListener { _, destination, arguments ->
                            val r = destination.route
                            val userIdArg = arguments?.getString("userId")
                            val currentUid = auth.currentUser?.uid
                            val newRoute = when {
                                r != null && (r == "home" || r == "search" || r == "following" || r == "profile") -> r
                                r != null && r.startsWith("user_profile") && userIdArg != null && currentUid != null && userIdArg == currentUid -> "profile"
                                else -> r ?: ""
                            }
                            currentRoute.value = newRoute
                        }
                        navController.addOnDestinationChangedListener(listener)
                        onDispose { navController.removeOnDestinationChangedListener(listener) }
                    }

                    val onLogout: () -> Unit = {
                        auth.signOut()
                        isLoggedIn = false
                        showRegister = false
                    }

                    Surface {
                        Column {
                            Box(modifier = Modifier.weight(1f)) {
                                NavGraph(
                                    navController = navController,
                                    startDestination = "home",
                                    onLogout = onLogout
                                )
                            }
                            BottomNavBar(selectedRoute = currentRoute.value) { route ->
                                if (route == "profile") {
                                    navController.navigate("profile") {
                                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                } else if (route == "following") {
                                    navController.navigate("following") {
                                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                } else {
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
}