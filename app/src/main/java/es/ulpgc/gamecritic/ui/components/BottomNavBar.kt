package es.ulpgc.gamecritic.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import es.ulpgc.gamecritic.util.ImageUtils

private val MyYellow = Color(0xFFF4D73E)
private val TextBlack = Color(0xFF111827)
private val TextGray = Color(0xFF9CA3AF)
private val NavBarBackground = Color(0xFFFFFFFF)

sealed class BottomNavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Home : BottomNavItem("home", "Inicio", Icons.Filled.Home, Icons.Outlined.Home)
    object Search : BottomNavItem("search", "Buscador", Icons.Filled.Search, Icons.Outlined.Search)
    object Following : BottomNavItem("following", "Siguiendo", Icons.Filled.Person, Icons.Outlined.Person)
    object Profile : BottomNavItem("profile", "Perfil", Icons.Filled.Person, Icons.Outlined.Person)
}

@Composable
fun BottomNavBar(selectedRoute: String, onItemSelected: (String) -> Unit, profileIcon: String) {
    NavigationBar(
        containerColor = NavBarBackground,
        tonalElevation = 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                spotColor = Color.Black.copy(alpha = 0.1f)
            )
    ) {
        listOf(
            BottomNavItem.Home,
            BottomNavItem.Search,
            BottomNavItem.Following,
            BottomNavItem.Profile
        ).forEach { item ->
            val isSelected = selectedRoute == item.route
            val isProfile = item.route == BottomNavItem.Profile.route

            NavigationBarItem(
                selected = isSelected,
                onClick = { onItemSelected(item.route) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = TextBlack,
                    selectedTextColor = TextBlack,
                    indicatorColor = MyYellow,
                    unselectedIconColor = TextGray,
                    unselectedTextColor = TextGray
                ),
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 10.sp
                        )
                    )
                },
                alwaysShowLabel = true,
                icon = {
                    if (isProfile) {
                        ProfileIcon(
                            profileIcon = profileIcon,
                            isSelected = isSelected,
                            label = item.label
                        )
                    } else {
                        Icon(
                            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.label,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun ProfileIcon(profileIcon: String, isSelected: Boolean, label: String) {
    Surface(
        shape = CircleShape,
        color = Color.Transparent,
        border = if (isSelected) BorderStroke(2.dp, TextBlack) else null,
        modifier = Modifier.size(28.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
        ) {
            val decodedBitmap = ImageUtils.decodeToImageBitmapOrNull(profileIcon)

            if (decodedBitmap != null) {
                Image(
                    bitmap = decodedBitmap,
                    contentDescription = label,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                AsyncImage(
                    model = profileIcon,
                    contentDescription = label,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}