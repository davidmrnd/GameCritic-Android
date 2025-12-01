package es.ulpgc.gamecritic.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import es.ulpgc.gamecritic.viewmodel.FollowingViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import es.ulpgc.gamecritic.model.Comment

@Composable
fun FollowingScreen(
    followingViewModel: FollowingViewModel = viewModel()
) {
    val comments by followingViewModel.comments.collectAsState()
    val grouped by followingViewModel.groupedComments.collectAsState()
    val isLoading by followingViewModel.isLoading.collectAsState()
    val error by followingViewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        followingViewModel.loadFollowingComments()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0ECE3))
            .padding(8.dp)
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            error != null -> {
                Text(
                    text = error ?: "Error desconocido",
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            grouped.isEmpty() -> { // Usamos grouped como referencia principal
                Text(
                    text = "No hay actividad reciente de tus seguidos.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Cada entrada: un usuario con su carrusel de videojuegos comentados
                    items(grouped.entries.toList()) { entry ->
                        val username = entry.key
                        val userComments = entry.value
                        UserCarouselItem(username = username, comments = userComments)
                    }
                }
            }
        }
    }
}

@Composable
fun UserCarouselItem(username: String, comments: List<Comment>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Cabecera: avatar (del primer comentario) + username
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            val avatarUrl = comments.firstOrNull()?.userProfileIcon
            Image(
                painter = rememberAsyncImagePainter(avatarUrl),
                contentDescription = "Avatar usuario",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "@$username",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Carrusel horizontal de videojuegos comentados por este usuario
        LazyRow(
            contentPadding = PaddingValues(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(comments) { comment ->
                VideoCard(comment)
            }
        }
    }
}

@Composable
fun VideoCard(comment: Comment) {
    Card(
        modifier = Modifier
            .width(180.dp)
            .height(220.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = rememberAsyncImagePainter(comment.videogameImage),
                contentDescription = "Imagen videojuego",
                modifier = Modifier
                    .height(130.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = comment.videogameTitle ?: "Título desconocido",
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 8.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = comment.content ?: "",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .weight(1f),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = comment.createdAtFormatted ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}
