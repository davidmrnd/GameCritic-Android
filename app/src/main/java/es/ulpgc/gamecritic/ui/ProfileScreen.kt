package es.ulpgc.gamecritic.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import es.ulpgc.gamecritic.model.Comment
import es.ulpgc.gamecritic.model.User
import es.ulpgc.gamecritic.util.ImageUtils
import es.ulpgc.gamecritic.viewmodel.ProfileViewModel
import kotlinx.coroutines.delay


private val LightBackground = Color(0xFFF0ECE3)
private val LightSurface = Color(0xFFFFFFFF)
private val TextBlack = Color(0xFF111827)
private val TextDarkGray = Color(0xFF4B5563)
private val MyYellow = Color(0xFFF4D73E)
private val MyYellowDark = Color(0xFFF4D73E)
private val DangerColor = Color(0xFFEF4444)

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

    Scaffold(
        containerColor = LightBackground
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(MyYellow, MyYellowDark),
                            startY = 0f,
                            endY = 1000f
                        )
                    )
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                
                item { Spacer(modifier = Modifier.height(50.dp)) }

                
                item {
                    ProfileHeroCard(
                        viewModel = viewModel,
                        isOwnProfile = isOwnProfile,
                        onEditClick = { navController?.navigate("edit_profile") },
                        onLogoutClick = { showLogoutDialog = true },
                        onFollowersClick = {
                            viewModel.loadFollowers(viewModel.user?.id)
                            showFollowersDialog = true
                        },
                        onFollowingClick = {
                            viewModel.loadFollowing(viewModel.user?.id)
                            showFollowingDialog = true
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 20.dp, bottom = 12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(24.dp)
                                .background(MyYellow, RoundedCornerShape(2.dp))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Actividad reciente",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                color = TextBlack,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                if (viewModel.isLoadingComments) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(30.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = MyYellow)
                        }
                    }
                } else if (viewModel.userComments.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(30.dp), contentAlignment = Alignment.Center) {
                            Text(
                                text = "Aún no hay actividad registrada.",
                                color = TextDarkGray,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                } else {
                    items(viewModel.userComments) { comment ->
                        UserCommentItem(
                            comment = comment,
                            onClick = { if (comment.videogameId.isNotBlank()) onOpenVideogame(comment.videogameId) }
                        )
                    }
                }
            }

            if (!isOwnProfile && navController != null) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .padding(top = 16.dp, start = 16.dp)
                        .shadow(4.dp, CircleShape)
                        .background(LightSurface, CircleShape)
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = TextBlack
                    )
                }
            }
        }

        if (showLogoutDialog) {
            LogoutDialog(onDismiss = { showLogoutDialog = false }, onConfirm = onLogout)
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
                title = "Siguiendo",
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
fun ProfileHeroCard(
    viewModel: ProfileViewModel,
    isOwnProfile: Boolean,
    onEditClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onFollowersClick: () -> Unit,
    onFollowingClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = TextDarkGray.copy(alpha = 0.2f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = LightSurface)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.Top) {
                
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE5E7EB))
                        .shadow(2.dp, CircleShape)
                ) {
                    val decodedBitmap = ImageUtils.decodeToImageBitmapOrNull(viewModel.profileIcon)
                    if (decodedBitmap != null) {
                        Image(bitmap = decodedBitmap, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    } else if (viewModel.profileIcon.isNotBlank()) {
                        AsyncImage(model = viewModel.profileIcon, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = null,
                            modifier = Modifier.padding(20.dp).fillMaxSize(),
                            tint = TextDarkGray
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = viewModel.name.ifBlank { "Usuario" },
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = TextBlack
                        )
                    )
                    Text(
                        text = "@${viewModel.username.lowercase()}",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextDarkGray)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = viewModel.description.ifBlank { "Sin descripción." },
                        style = MaterialTheme.typography.bodySmall.copy(color = TextBlack, lineHeight = 18.sp),
                        maxLines = 3
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = Color(0xFFF3F4F6))
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                ProfileStatItem(label = "Reseñas", count = viewModel.userComments.size.toString(), onClick = {})
                ProfileStatItem(label = "Seguidores", count = viewModel.followersCount.toString(), onClick = onFollowersClick)
                ProfileStatItem(label = "Siguiendo", count = viewModel.followingCount.toString(), onClick = onFollowingClick)
            }
            Spacer(modifier = Modifier.height(24.dp))

            if (isOwnProfile) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = onEditClick,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MyYellow),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Outlined.Edit, null, tint = TextBlack, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Editar", color = TextBlack, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = onLogoutClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE2E2)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Outlined.ExitToApp, null, tint = DangerColor)
                    }
                }
            } else {
                val isFollowed = viewModel.isFollowed
                Button(
                    onClick = { if (isFollowed) viewModel.unfollow() else viewModel.follow() },
                    enabled = !viewModel.isLoadingFollowAction && viewModel.currentUid != null,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isFollowed) LightBackground else MyYellow
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = if (isFollowed) BorderStroke(1.dp, TextDarkGray) else null
                ) {
                    if (viewModel.isLoadingFollowAction) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = TextBlack)
                    } else {
                        Text(
                            text = if (isFollowed) "Siguiendo" else "Seguir",
                            color = TextBlack,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileStatItem(label: String, count: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = count,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
            color = TextBlack
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
            color = TextDarkGray
        )
    }
}

@Composable
fun UserCommentItem(comment: Comment, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable(onClick = onClick)
            .shadow(2.dp, RoundedCornerShape(12.dp), spotColor = TextDarkGray.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = LightSurface),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            
            AsyncImage(
                model = comment.videogameImage,
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFE5E7EB)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = comment.videogameTitle,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextBlack),
                        maxLines = 1
                    )

                    Surface(
                        color = MyYellow.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, null, modifier = Modifier.size(10.dp), tint = Color(0xFFD4B20C))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(text = comment.rating.toString(), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = comment.content,
                    style = MaterialTheme.typography.bodySmall.copy(color = TextDarkGray),
                    maxLines = 2,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
fun LogoutDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = LightSurface,
        title = { Text("Cerrar Sesión", fontWeight = FontWeight.Bold, color = TextBlack) },
        text = { Text("¿Estás seguro de que quieres salir?", color = TextDarkGray) },
        confirmButton = {
            Button(
                onClick = { onDismiss(); onConfirm() },
                colors = ButtonDefaults.buttonColors(containerColor = DangerColor)
            ) { Text("Salir", color = Color.White) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = TextBlack) }
        }
    )
}
