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
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import coil.compose.AsyncImage
import es.ulpgc.gamecritic.model.Comment
import androidx.compose.runtime.LaunchedEffect
import es.ulpgc.gamecritic.viewmodel.UserSummary
import es.ulpgc.gamecritic.ui.FollowersFollowingDialog

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    navController: NavController? = null,
    onLogout: () -> Unit = {},
    profileId: String? = null // si se pasa, mostramos ese perfil en lugar del propio
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showFollowersDialog by remember { mutableStateOf(false) }
    var showFollowingDialog by remember { mutableStateOf(false) }

    // Si se nos pasa profileId, cargar ese perfil
    LaunchedEffect(profileId) {
        if (!profileId.isNullOrBlank()) {
            viewModel.loadProfileById(profileId)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0ECE3))
            .padding(24.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (viewModel.profileIcon.isNotBlank()) {
                    Image(
                        painter = rememberAsyncImagePainter(viewModel.profileIcon),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFD9D9D9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("", color = Color.Gray)
                    }
                }
                Spacer(modifier = Modifier.width(24.dp))
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(
                        text = viewModel.name.uppercase(),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "@" + viewModel.username.uppercase(),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Black
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = viewModel.description,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Black
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                // Mostrar Editar/Cerrar sesión solo si el perfil mostrado coincide con el usuario en sesión
                val isOwnProfile = viewModel.currentUid != null && viewModel.currentUid == viewModel.user?.id

                if (isOwnProfile) {
                    Button(
                        onClick = { navController?.navigate("edit_profile") },
                        enabled = navController != null,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF4D73E)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Editar usuario", fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                    Button(
                        onClick = { showLogoutDialog = true },
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cerrar sesión", fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                } else {
                    // Perfil de otro usuario: mostrar botón seguir / dejar de seguir
                    if (viewModel.isFollowed) {
                        Button(
                            onClick = { viewModel.unfollow() },
                            enabled = !viewModel.isLoadingFollowAction && viewModel.currentUid != null,
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Dejar de seguir", fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    } else {
                        Button(
                            onClick = { viewModel.follow() },
                            enabled = !viewModel.isLoadingFollowAction && viewModel.currentUid != null,
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF4D73E)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Seguir", fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Contador de valoraciones / comentarios del usuario
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable {
                        // opcional: podríamos navegar o abrir detalle de valoraciones
                    }
                ) {
                    Text(
                        text = "Valoraciones",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black)
                    )
                    Text(
                        text = viewModel.userComments.size.toString(),
                        style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black)
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable {
                        viewModel.loadFollowers(viewModel.user?.id)
                        showFollowersDialog = true
                    }
                ) {
                    Text(
                        text = "Seguidores",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black)
                    )
                    Text(
                        text = viewModel.followersCount.toString(),
                        style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black)
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable {
                        viewModel.loadFollowing(viewModel.user?.id)
                        showFollowingDialog = true
                    }
                ) {
                    Text(
                        text = "Seguidos",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black)
                    )
                    Text(
                        text = viewModel.followingCount.toString(),
                        style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black)
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            UserCommentsSection(
                comments = viewModel.userComments,
                isLoading = viewModel.isLoadingComments,
                errorMessage = viewModel.commentsError
            )
        }

        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = {
                    Text(
                        text = "Cerrar sesión",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.Black
                    )
                },
                text = {
                    Text(
                        text = "¿Seguro que deseas cerrar sesión?",
                        style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
                        color = Color.Black
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        showLogoutDialog = false
                        onLogout()
                    }) {
                        Text("Sí, cerrar sesión", color = Color.Red, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) {
                        Text("Cancelar", color = Color.Black)
                    }
                },
                containerColor = Color(0xFFE0E0E0)
            )
        }

        if (showFollowersDialog) {
            FollowersFollowingDialog(
                title = "Seguidores",
                users = viewModel.followersList,
                isLoading = viewModel.isLoadingFollowers,
                onDismiss = { showFollowersDialog = false },
                onUserClick = { userId ->
                    showFollowersDialog = false
                    navController?.navigate("profile/$userId")
                }
            )
        }

        if (showFollowingDialog) {
            FollowersFollowingDialog(
                title = "Seguidos",
                users = viewModel.followingList,
                isLoading = viewModel.isLoadingFollowing,
                onDismiss = { showFollowingDialog = false },
                onUserClick = { userId ->
                    showFollowingDialog = false
                    navController?.navigate("profile/$userId")
                }
            )
        }
    }
}

@Composable
private fun UserCommentsSection(
    comments: List<Comment>,
    isLoading: Boolean,
    errorMessage: String?
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Comentarios del usuario",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.Black,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        when {
            isLoading -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            errorMessage != null -> {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            comments.isEmpty() -> {
                Text(
                    text = "El usuario aún no ha comentado ningún videojuego.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 400.dp)
                ) {
                    items(comments) { comment ->
                        UserCommentCard(comment)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun UserCommentCard(comment: Comment) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = RoundedCornerShape(12.dp), color = Color.LightGray) {
                        AsyncImage(
                            model = comment.videogameImage,
                            contentDescription = comment.videogameTitle,
                            modifier = Modifier.size(36.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = comment.videogameTitle.ifBlank { "Videojuego desconocido" },
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.Black
                        )
                        Text(
                            text = comment.createdAtFormatted.ifBlank { "Fecha no disponible" },
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF666666)
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "★ ${comment.rating}",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color(0xFFF4D73E)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = comment.content,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black
            )
        }
    }
}
