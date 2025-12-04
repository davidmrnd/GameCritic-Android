package es.ulpgc.gamecritic.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Edit
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import es.ulpgc.gamecritic.model.Comment
import es.ulpgc.gamecritic.repository.UserRepository
import es.ulpgc.gamecritic.util.ImageUtils
import es.ulpgc.gamecritic.viewmodel.VideogameProfileViewModel


private val LightBackground = Color(0xFFF0ECE3)
private val LightSurface = Color(0xFFFFFFFF)
private val TextBlack = Color(0xFF111827)
private val TextDarkGray = Color(0xFF4B5563)
private val MyYellow = Color(0xFFF4D73E)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideogameDetailScreen(
    videogameId: String,
    onBack: () -> Unit,
    onAddComment: (String) -> Unit,
    onOpenUserProfile: (String) -> Unit
) {
    val viewModel: VideogameProfileViewModel = viewModel()
    val videogame = viewModel.videogame
    val comments = viewModel.comments
    val isLoadingComments = viewModel.isLoadingComments
    val commentsError = viewModel.commentsError

    val userRepository = remember { UserRepository() }
    val currentUserId = remember { userRepository.getCurrentUserId() }
    val hasUserComment = remember(comments, currentUserId) {
        currentUserId != null && comments.any { it.userId == currentUserId }
    }

    LaunchedEffect(videogameId) {
        viewModel.loadVideogame(videogameId)
        viewModel.loadComments(videogameId)
    }

    Scaffold(
        containerColor = LightBackground
    ) { paddingValues ->
        if (videogame == null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MyYellow)
            }
        } else {
            
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 80.dp) 
                ) {
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp) 
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(videogame.imageProfile)
                                .crossfade(true)
                                .build(),
                            contentDescription = videogame.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .align(Alignment.BottomCenter)
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.4f))
                                    )
                                )
                        )
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .offset(y = (-40).dp) 
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(16.dp),
                                spotColor = MyYellow.copy(alpha = 0.4f) 
                            ),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = LightSurface)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp)
                        ) {
                            
                            if (videogame.category.isNotEmpty()) {
                                Row(
                                    modifier = Modifier.padding(bottom = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    videogame.category.take(3).forEach { cat ->
                                        Surface(
                                            shape = RoundedCornerShape(50),
                                            color = MyYellow.copy(alpha = 0.2f),
                                            border = BorderStroke(1.dp, MyYellow),
                                            modifier = Modifier.padding(end = 6.dp)
                                        ) {
                                            Text(
                                                text = cat.uppercase(),
                                                color = TextBlack,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 10.sp
                                                ),
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            Text(
                                text = videogame.title,
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextBlack
                                ),
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            Text(
                                text = videogame.subtitle,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = TextDarkGray
                                ),
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            HorizontalDivider(color = Color(0xFFE5E7EB))

                            Text(
                                text = "Sinopsis",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
                            )
                            Text(
                                text = videogame.description,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = TextBlack.copy(alpha = 0.8f),
                                    lineHeight = 22.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(20.dp))

                            
                            Button(
                                onClick = { onAddComment(videogameId) },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MyYellow),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = if (hasUserComment) Icons.Outlined.Edit else Icons.Default.Star,
                                    contentDescription = null,
                                    tint = TextBlack,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (hasUserComment) "EDITAR MI RESEÑA" else "VALORAR ESTE JUEGO",
                                    fontWeight = FontWeight.Bold,
                                    color = TextBlack
                                )
                            }
                        }
                    }
                    
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = (-20).dp)
                            .padding(horizontal = 16.dp)
                    ) {
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .height(24.dp)
                                    .background(MyYellow, RoundedCornerShape(2.dp))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Reseñas de la comunidad",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextBlack
                                )
                            )
                        }
                        CommentsSection(
                            comments = comments,
                            isLoading = isLoadingComments,
                            errorMessage = commentsError,
                            onOpenUserProfile = onOpenUserProfile
                        )
                    }
                }

                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .padding(top = 40.dp, start = 16.dp) 
                        .align(Alignment.TopStart)
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
    }
}

@Composable
private fun CommentsSection(
    comments: List<Comment>,
    isLoading: Boolean,
    errorMessage: String?,
    onOpenUserProfile: (String) -> Unit
) {
    when {
        isLoading -> {
            Box(Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MyYellow)
            }
        }
        errorMessage != null -> {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(8.dp)
            )
        }
        comments.isEmpty() -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color.Gray.copy(alpha = 0.3f),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Sé el primero en opinar",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextDarkGray),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        else -> {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                comments.forEach { comment ->
                    CommentCard(
                        comment = comment,
                        onClick = { if (comment.userId.isNotBlank()) onOpenUserProfile(comment.userId) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CommentCard(comment: Comment, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = TextDarkGray.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = LightSurface),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB)) 
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically 
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE5E7EB))
                ) {
                    val decodedBitmap = ImageUtils.decodeToImageBitmapOrNull(comment.userProfileIcon)
                    if (decodedBitmap != null) {
                        Image(
                            bitmap = decodedBitmap,
                            contentDescription = comment.username,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        AsyncImage(
                            model = comment.userProfileIcon,
                            contentDescription = comment.username,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = comment.username.ifBlank { "Usuario desconocido" },
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextBlack)
                    )
                    Text(
                        text = comment.createdAtFormatted.ifBlank { comment.createdAt },
                        style = MaterialTheme.typography.labelSmall.copy(color = TextDarkGray)
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MyYellow.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, MyYellow)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFD4B20C), 
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = comment.rating.toString(),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = TextBlack)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = comment.content,
                style = MaterialTheme.typography.bodyMedium.copy(color = TextBlack, lineHeight = 20.sp)
            )
        }
    }
}