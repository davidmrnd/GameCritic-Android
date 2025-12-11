package es.ulpgc.gamecritic.ui

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import es.ulpgc.gamecritic.model.Comment
import es.ulpgc.gamecritic.util.ImageUtils
import es.ulpgc.gamecritic.viewmodel.FollowingViewModel
import es.ulpgc.gamecritic.viewmodel.UserComments
import coil.compose.rememberAsyncImagePainter

private val LightBackground = Color(0xFFF0ECE3)
private val StarColor = Color(0xFFD4B20C)

@Composable
fun FollowingScreen(
    navController: NavHostController,
    followingViewModel: FollowingViewModel = viewModel()
) {
    val userComments by followingViewModel.userComments.collectAsState()
    val isLoading by followingViewModel.isLoading.collectAsState()
    val error by followingViewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        followingViewModel.loadFollowingComments()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground)
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
            userComments.isEmpty() -> {
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
                    items(userComments) { userComment ->
                        UserCarouselItem(userComment, navController)
                    }
                }
            }
        }
    }
}

@Composable
fun UserCarouselItem(userComments: UserComments, navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .clickable { navController.navigate("user_profile/${userComments.userId}") }
        ) {
            UserProfileImage(
                imageData = userComments.userProfileIcon,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "@${userComments.username}",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(userComments.comments) { comment ->
                VideoCard(comment, navController)
            }
        }
    }
}

@Composable
fun VideoCard(comment: Comment, navController: NavHostController) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .width(180.dp)
            .height(245.dp)
            .clickable { comment.videogameId.let { navController.navigate("videogame_detail/$it") } }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = rememberAsyncImagePainter(comment.videogameImage),
                contentDescription = null,
                modifier = Modifier
                    .height(115.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = comment.videogameTitle,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 8.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            ReadOnlyRatingBar(
                rating = comment.rating,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = comment.content,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .weight(1f),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = comment.createdAtFormatted,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
fun ReadOnlyRatingBar(
    rating: Double,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        (1..5).forEach { index ->
            val icon = when {
                rating >= index -> Icons.Filled.Star
                rating >= index - 0.5 -> Icons.AutoMirrored.Filled.StarHalf
                else -> Icons.Filled.Star
            }

            val tint = if (rating >= index - 0.5) StarColor else Color.LightGray.copy(alpha = 0.5f)

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun UserProfileImage(imageData: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val imageBitmap: ImageBitmap? = ImageUtils.decodeToImageBitmapOrNull(imageData)
    if (imageBitmap != null) {
        Image(
            painter = BitmapPainter(imageBitmap),
            contentDescription = null,
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    } else {
        Image(
            painter = rememberAsyncImagePainter(imageData),
            contentDescription = null,
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    }
}
