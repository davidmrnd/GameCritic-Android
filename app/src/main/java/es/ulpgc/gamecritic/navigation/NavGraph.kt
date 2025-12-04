package es.ulpgc.gamecritic.navigation

import android.annotation.SuppressLint
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
import es.ulpgc.gamecritic.ui.AddCommentScreen
import es.ulpgc.gamecritic.ui.ExploreScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import es.ulpgc.gamecritic.model.SearchTab
import es.ulpgc.gamecritic.viewmodel.ProfileViewModel

@SuppressLint("UnrememberedGetBackStackEntry")
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
                },
                onRecentSearchNavigate = { recent ->
                    when (recent.tab) {
                        SearchTab.VIDEOGAMES -> navController.navigate("videogame_detail/${recent.itemId}")
                        SearchTab.USERS -> navController.navigate("user_profile/${recent.itemId}")
                    }
                }
            )
        }
        composable("following") { FollowingScreen(navController = navController) }
        composable("profile") { backStackEntry ->
            val profileViewModel: ProfileViewModel = viewModel(viewModelStoreOwner = backStackEntry)
            ProfileScreen(
                viewModel = profileViewModel,
                navController = navController,
                onLogout = onLogout,
                onOpenUserProfile = { id -> navController.navigate("user_profile/${id}") },
                onOpenVideogame = { gameId -> navController.navigate("videogame_detail/${gameId}") }
            )
        }
        composable("user_profile/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            ProfileScreen(
                navController = navController,
                onLogout = onLogout,
                profileId = userId,
                onOpenUserProfile = { id -> navController.navigate("user_profile/${id}") },
                onOpenVideogame = { gameId -> navController.navigate("videogame_detail/${gameId}") }
            )
        }
        composable("edit_profile") { backStackEntry ->
            val profileBackStackEntry = navController.getBackStackEntry("profile")
            val profileViewModel: ProfileViewModel = viewModel(viewModelStoreOwner = profileBackStackEntry)
            EditProfileScreen(
                viewModel = profileViewModel,
                onCancel = { navController.popBackStack() },
                onSave = { navController.popBackStack() }
            )
        }
        composable("videogame_detail/{videogameId}") { backStackEntry ->
            val videogameId = backStackEntry.arguments?.getString("videogameId") ?: return@composable
            VideogameDetailScreen(
                videogameId = videogameId,
                onBack = { navController.popBackStack() },
                onAddComment = { id -> navController.navigate("add_comment/$id") },
                onOpenUserProfile = { userId -> navController.navigate("user_profile/${userId}") }
            )
        }
        composable("add_comment/{videogameId}") { backStackEntry ->
            val videogameId = backStackEntry.arguments?.getString("videogameId") ?: return@composable
            AddCommentScreen(
                videogameId = videogameId,
                onBack = { navController.popBackStack() },
                onCommentSaved = { navController.popBackStack() }
            )
        }
        composable("explore") {
            ExploreScreen(navController = navController)
        }
    }
}
