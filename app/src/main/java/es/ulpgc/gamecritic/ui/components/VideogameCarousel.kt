package es.ulpgc.gamecritic.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import es.ulpgc.gamecritic.model.Videogame

private val LightSurface = Color(0xFFFFFFFF)
private val TextBlack = Color(0xFF111827)
private val TextDarkGray = Color(0xFF4B5563)
private val MyYellow = Color(0xFFF4D73E)

@Composable
fun VideogameCarousel(
    videogames: List<Videogame>,
    categoryName: String,
    modifier: Modifier = Modifier,
    onVideogameClick: (Videogame) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        if (categoryName.isNotBlank()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(start = 20.dp, bottom = 12.dp, top = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(24.dp)
                        .background(MyYellow, RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = categoryName,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = TextBlack,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(
                items = videogames,
                key = { it.id }
            ) { game ->
                CarouselItem(game, onVideogameClick)
            }
        }
    }
}

@Composable
fun CarouselItem(
    game: Videogame,
    onClick: (Videogame) -> Unit
) {
    Card(
        modifier = Modifier
            .width(260.dp)
            .height(280.dp)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = TextDarkGray.copy(alpha = 0.2f)
            )
            .clickable { onClick(game) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = LightSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(Color(0xFFE5E7EB))
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(game.imageCarousel)
                        .crossfade(true)
                        .build(),
                    contentDescription = game.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = game.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = TextBlack,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = game.subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextDarkGray
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.weight(1f))

                if (game.category.isNotEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        game.category.take(2).forEach { cat ->
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
                        if(game.category.size > 2) {
                            Text(
                                text = "+${game.category.size - 2}",
                                style = MaterialTheme.typography.labelSmall.copy(color = TextDarkGray)
                            )
                        }
                    }
                }
            }
        }
    }
}