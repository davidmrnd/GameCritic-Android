package es.ulpgc.gamecritic.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import es.ulpgc.gamecritic.viewmodel.ProfileViewModel
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.navigation.NavController
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import coil.compose.AsyncImage
import es.ulpgc.gamecritic.model.Comment
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.sp
import es.ulpgc.gamecritic.util.ImageUtils
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api

val BackgroundColor = Color(0xFFF0ECE3)
val CardColor = Color.White
val PrimaryAccent = Color(0xFFF4D73E)
val DangerColor = Color(0xFFB91D1D)
val DetailColor = Color(0xFF666666)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    navController: NavController? = null,
    onLogout: () -> Unit = {},
    profileId: String? = null,
    onOpenUserProfile: (String) -> Unit = {},
    onOpenVideogame: (String) -> Unit = {}
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showFollowersDialog by remember { mutableStateOf(false) }
    var showFollowingDialog by remember { mutableStateOf(false) }
    var selectedUserToOpen by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(profileId) {
        if (!profileId.isNullOrBlank()) {
            viewModel.loadProfileById(profileId)
        }
    }

    val isOwnProfile = viewModel.currentUid != null && viewModel.currentUid == viewModel.user?.id

    Column(modifier = Modifier.fillMaxSize().background(BackgroundColor)) {
        TopAppBar(
            title = { Text("") },
            navigationIcon = {
                if (!isOwnProfile && navController != null) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Surface(
                            shape = CircleShape,
                            color = PrimaryAccent
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Volver",
                                tint = Color.Black,
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFE0E0E0))
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    val profileImageModifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .shadow(4.dp, CircleShape)

                    val profileIconData = viewModel.profileIcon
                    val decodedBitmap = ImageUtils.decodeToImageBitmapOrNull(profileIconData)

                    if (decodedBitmap != null) {
                        Image(
                            bitmap = decodedBitmap,
                            contentDescription = "Foto de perfil",
                            modifier = profileImageModifier,
                            contentScale = ContentScale.Crop
                        )
                    } else if (profileIconData.isNotBlank()) {
                        Image(
                            painter = rememberAsyncImagePainter(profileIconData),
                            contentDescription = "Foto de perfil",
                            modifier = profileImageModifier,
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = profileImageModifier.background(Color(0xFFD9D9D9)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("?", color = DetailColor, fontSize = 40.sp)
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = viewModel.name.uppercase(),
                            style = MaterialTheme.typography.headlineSmall.copy(
                                color = Color.Black,
                                fontWeight = FontWeight.ExtraBold
                            )
                        )
                        Text(
                            text = "@" + viewModel.username.lowercase(),
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = DetailColor
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = viewModel.description.ifBlank { "Sin descripción." },
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    ProfileStat(
                        label = "Valoraciones",
                        count = viewModel.userComments.size.toString(),
                        onClick = {}
                    )
                    ProfileStat(
                        label = "Seguidores",
                        count = viewModel.followersCount.toString(),
                        onClick = {
                            viewModel.loadFollowers(viewModel.user?.id)
                            showFollowersDialog = true
                        }
                    )
                    ProfileStat(
                        label = "Seguidos",
                        count = viewModel.followingCount.toString(),
                        onClick = {
                            viewModel.loadFollowing(viewModel.user?.id)
                            showFollowingDialog = true
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))


                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    if (isOwnProfile) {
                        Button(
                            onClick = { navController?.navigate("edit_profile") },
                            enabled = navController != null,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccent, contentColor = Color.Black),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        ) {
                            Text("Editar Perfil", fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { showLogoutDialog = true },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DangerColor, contentColor = Color.Black),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        ) {
                            Text("Cerrar Sesión", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = { if (viewModel.isFollowed) viewModel.unfollow() else viewModel.follow() },
                            enabled = !viewModel.isLoadingFollowAction && viewModel.currentUid != null,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (viewModel.isFollowed) DangerColor else PrimaryAccent,
                                contentColor = if (viewModel.isFollowed) Color.White else Color.Black
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            if (viewModel.isLoadingFollowAction) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                            } else {
                                Text(
                                    text = if (viewModel.isFollowed) "Dejar de seguir" else "Seguir",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Actividad y Comentarios",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp, start = 8.dp)
                )
            }

            items(viewModel.userComments) { comment ->
                UserCommentCard(
                    comment = comment,
                    onClick = { if (comment.videogameId.isNotBlank()) onOpenVideogame(comment.videogameId) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                when {
                    viewModel.isLoadingComments -> {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(color = PrimaryAccent)
                        }
                    }
                    viewModel.commentsError != null -> {
                        Text(
                            text = viewModel.commentsError ?: "Error al cargar comentarios.",
                            color = DangerColor,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    viewModel.userComments.isEmpty() -> {
                        Text(
                            text = "El usuario aún no ha comentado ningún videojuego.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = DetailColor,
                            modifier = Modifier.padding(vertical = 32.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text(text = "Cerrar Sesión", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold), color = Color.Black) },
                text = { Text(text = "¿Seguro que deseas cerrar sesión?", style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center), color = Color.Black) },
                confirmButton = {
                    TextButton(onClick = { showLogoutDialog = false; onLogout() }) {
                        Text("Sí, cerrar sesión", color = DangerColor, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = { TextButton(onClick = { showLogoutDialog = false }) { Text("Cancelar", color = Color.Black) } },
                containerColor = Color(0xFFE0E0E0)
            )
        }

        if (showFollowersDialog) {
            FollowersFollowingDialog(
                title = "Seguidores",
                users = viewModel.followersList,
                isLoading = viewModel.isLoadingFollowers,
                onDismiss = { showFollowersDialog = false },
                onUserClick = { userId -> showFollowersDialog = false; selectedUserToOpen = userId }
            )
        }

        if (showFollowingDialog) {
            FollowersFollowingDialog(
                title = "Seguidos",
                users = viewModel.followingList,
                isLoading = viewModel.isLoadingFollowing,
                onDismiss = { showFollowingDialog = false },
                onUserClick = { userId -> showFollowingDialog = false; selectedUserToOpen = userId }
            )
        }

        LaunchedEffect(selectedUserToOpen) {
            selectedUserToOpen?.let {
                delay(300)
                onOpenUserProfile(it)
                selectedUserToOpen = null
            }
        }
    }
}

@Composable
fun ProfileStat(label: String, count: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = count,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = Color.Black
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = DetailColor
        )
    }
}

@Composable
fun UserCommentCard(comment: Comment, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardColor)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = comment.videogameImage,
                contentDescription = "Imagen del videojuego",
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = comment.videogameTitle,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = comment.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray,
                    maxLines = 2
                )
            }
            Text(
                text = "★ ${comment.rating}",
                fontWeight = FontWeight.Bold,
                color = PrimaryAccent,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
fun FollowersFollowingDialog(
    title: String,
    users: List<es.ulpgc.gamecritic.model.User>,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onUserClick: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryAccent)
                }
            } else if (users.isEmpty()) {
                Text("No hay usuarios para mostrar.", color = DetailColor)
            } else {
                LazyColumn {
                    items(users) { user ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onUserClick(user.id) }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(user.profileIcon),
                                contentDescription = "Avatar",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(user.username, color = Color.Black)
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Cerrar", color = Color.Black, fontWeight = FontWeight.Bold) } },
        containerColor = Color(0xFFE0E0E0)
    )
}

