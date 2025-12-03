package es.ulpgc.gamecritic.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Person
import es.ulpgc.gamecritic.util.ImageUtils
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import coil.compose.AsyncImage
import androidx.compose.ui.Modifier
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.layout.Box

sealed class BottomNavItem(val route: String, val label: String, val icon: ImageVector) {
    object Home : BottomNavItem("home", "Inicio", Icons.Filled.Home)
    object Search : BottomNavItem("search", "Buscar", Icons.Filled.Search)
    object Following : BottomNavItem("following", "Siguiendo", Icons.Filled.Person)
    object Profile : BottomNavItem("profile", "Perfil", Icons.Filled.Person)
}

@Composable
fun BottomNavBar(selectedRoute: String, onItemSelected: (String) -> Unit, profileIcon: String) {
    NavigationBar {
        listOf(
            BottomNavItem.Home,
            BottomNavItem.Search,
            BottomNavItem.Following,
            BottomNavItem.Profile
        ).forEach { item ->
            val isProfile = item.route == BottomNavItem.Profile.route
            NavigationBarItem(
                selected = selectedRoute == item.route,
                onClick = { onItemSelected(item.route) },
                icon = {
                    if (isProfile) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(modifier = Modifier.size(32.dp)) {
                                val decodedBitmap = ImageUtils.decodeToImageBitmapOrNull(profileIcon)
                                if (decodedBitmap != null) {
                                    Image(
                                        bitmap = decodedBitmap,
                                        contentDescription = item.label,
                                        modifier = Modifier.matchParentSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    AsyncImage(
                                        model = profileIcon,
                                        contentDescription = item.label,
                                        modifier = Modifier.matchParentSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }
                    } else {
                        Icon(item.icon, contentDescription = item.label)
                    }
                },
                label = { Text(item.label) }
            )
        }
    }
}
