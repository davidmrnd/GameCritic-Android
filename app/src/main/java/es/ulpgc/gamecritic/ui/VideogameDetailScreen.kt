package es.ulpgc.gamecritic.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import es.ulpgc.gamecritic.model.Comment
import es.ulpgc.gamecritic.viewmodel.VideogameProfileViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideogameDetailScreen(
    videogameId: String,
    onBack: () -> Unit
) {
    val viewModel: VideogameProfileViewModel = viewModel()
    val videogame = viewModel.videogame
    val comments = viewModel.comments
    val isLoadingComments = viewModel.isLoadingComments
    val commentsError = viewModel.commentsError

    LaunchedEffect(videogameId) {
        viewModel.loadVideogame(videogameId)
        viewModel.loadComments(videogameId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0ECE3))
    ) {
        TopAppBar(
            title = {
                Text(
                    text = videogame?.title ?: "Detalle del videojuego",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.Black
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFF4D73E)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.Black,
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
            },
            colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFFE0E0E0)
            )
        )

        if (videogame == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Cargando...",
                    style = MaterialTheme.typography.bodyLarge.copy(color = Color.Gray)
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .background(Color(0xFFB3B3B3))
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(videogame.imageProfile),
                        contentDescription = videogame.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFFE0E0E0),
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .offset(y = (-32).dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Text(
                            text = videogame.title,
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = videogame.subtitle,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF666666),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        if (videogame.category.isNotEmpty()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 12.dp)
                            ) {
                                videogame.category.forEach { cat ->
                                    Surface(
                                        shape = RoundedCornerShape(50),
                                        color = Color(0xFFF4D73E),
                                        modifier = Modifier.padding(end = 8.dp)
                                    ) {
                                        Text(
                                            text = cat,
                                            color = Color.Black,
                                            style = MaterialTheme.typography.labelMedium,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                        Text(
                            text = videogame.description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Black,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
                CommentsSection(
                    comments = comments,
                    isLoading = isLoadingComments,
                    errorMessage = commentsError
                )
            }
        }
    }
}

@Composable
private fun CommentsSection(
    comments: List<Comment>,
    isLoading: Boolean,
    errorMessage: String?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Comentarios",
            style = MaterialTheme.typography.titleLarge,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        when {
            isLoading -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Cargando comentarios…")
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
                    text = "Este videojuego todavía no tiene comentarios.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 400.dp)
                ) {
                    items(comments) { comment ->
                        CommentCard(comment)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun CommentCard(comment: Comment) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = CircleShape, color = Color.LightGray) {
                        AsyncImage(
                            model = comment.userProfileIcon,
                            contentDescription = comment.username,
                            modifier = Modifier.size(36.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = comment.username.ifBlank { "Usuario desconocido" },
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.Black
                        )
                        Text(
                            text = comment.createdAtFormatted.ifBlank { comment.createdAt },
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
