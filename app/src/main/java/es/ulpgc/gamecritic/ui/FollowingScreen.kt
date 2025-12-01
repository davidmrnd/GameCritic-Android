package es.ulpgc.gamecritic.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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

@Composable
fun FollowingScreen(
    followingViewModel: FollowingViewModel = viewModel()
) {
    val comments by followingViewModel.comments.collectAsState()
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
            comments.isEmpty() -> {
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
                    items(comments) { comment ->
                        CommentFeedItem(comment)
                    }
                }
            }
        }
    }
}

@Composable
fun CommentFeedItem(comment: es.ulpgc.gamecritic.model.Comment) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 8.dp)
            .background(Color.White, shape = MaterialTheme.shapes.medium)
            .padding(12.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(comment.userProfileIcon),
            contentDescription = "Foto de perfil",
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "@${comment.username}",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = comment.videogameTitle,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = Color(0xFF3A3A3A)
            )
            Text(
                text = comment.content,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray,
                maxLines = 2
            )
            Text(
                text = comment.createdAtFormatted,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Image(
            painter = rememberAsyncImagePainter(comment.videogameImage),
            contentDescription = "Imagen videojuego",
            modifier = Modifier
                .size(56.dp)
                .clip(MaterialTheme.shapes.small),
            contentScale = ContentScale.Crop
        )
    }
}
