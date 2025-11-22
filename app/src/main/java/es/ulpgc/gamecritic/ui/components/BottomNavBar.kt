package es.ulpgc.gamecritic.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.Modifier

sealed class BottomNavItem(val route: String, val label: String, val icon: ImageVector) {
    object Home : BottomNavItem("home", "Inicio", Icons.Filled.Home)
    object Search : BottomNavItem("search", "Buscar", Icons.Filled.Search)
    object Following : BottomNavItem("following", "Siguiendo", Icons.Filled.Person)
    object Profile : BottomNavItem("profile", "Perfil", Icons.Filled.Person)
}

@Composable
fun BottomNavBar(selectedRoute: String, onItemSelected: (String) -> Unit) {
    NavigationBar {
        listOf(
            BottomNavItem.Home,
            BottomNavItem.Search,
            BottomNavItem.Following,
            BottomNavItem.Profile
        ).forEach { item ->
            NavigationBarItem(
                selected = selectedRoute == item.route,
                onClick = { onItemSelected(item.route) },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}
