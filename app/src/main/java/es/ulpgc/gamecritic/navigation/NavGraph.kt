package es.ulpgc.gamecritic.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import es.ulpgc.gamecritic.ui.HomeScreen
import es.ulpgc.gamecritic.ui.SearchScreen
import es.ulpgc.gamecritic.ui.FollowingScreen
import es.ulpgc.gamecritic.ui.ProfileScreen
import es.ulpgc.gamecritic.ui.EditProfileScreen
import es.ulpgc.gamecritic.ui.VideogameDetailScreen

@Composable
fun NavGraph(navController: NavHostController, startDestination: String, onLogout: () -> Unit) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable("home") { HomeScreen(navController = navController) }
        composable("search") {
            SearchScreen(
                onVideogameClick = { game ->
                    navController.navigate("videogame_detail/${game.id}")
                },
                onUserClick = { user ->
                    navController.navigate("user_profile/${user.id}")
                }
            )
        }
        composable("following") { FollowingScreen() }
        composable("profile") { ProfileScreen(navController = navController, onLogout = onLogout) }
        composable("user_profile/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            ProfileScreen(navController = navController, onLogout = onLogout, profileId = userId)
        }
        composable("edit_profile") {
            EditProfileScreen(
                onCancel = { navController.popBackStack() },
                onSave = { navController.popBackStack() }
            )
        }
        composable("videogame_detail/{videogameId}") { backStackEntry ->
            val videogameId = backStackEntry.arguments?.getString("videogameId") ?: return@composable
            VideogameDetailScreen(
                videogameId = videogameId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
