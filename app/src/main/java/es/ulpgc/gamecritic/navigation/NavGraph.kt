package es.ulpgc.gamecritic.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import es.ulpgc.gamecritic.ui.HomeScreen
import es.ulpgc.gamecritic.ui.SearchScreen
import es.ulpgc.gamecritic.ui.FollowingScreen
import es.ulpgc.gamecritic.ui.ProfileScreen

@Composable
fun NavGraph(navController: NavHostController, startDestination: String) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable("home") { HomeScreen() }
        composable("search") { SearchScreen() }
        composable("following") { FollowingScreen() }
        composable("profile") { ProfileScreen() }
    }
}
