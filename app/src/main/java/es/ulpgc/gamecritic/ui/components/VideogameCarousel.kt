package es.ulpgc.gamecritic.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import es.ulpgc.gamecritic.model.Videogame

@Composable
fun VideogameCarousel(
    videogames: List<Videogame>,
    categoryName: String,
    modifier: Modifier = Modifier,
    onVideogameClick: (Videogame) -> Unit
) {
    Column(
        modifier = modifier
            .background(Color(0xFFF0ECE3))
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = categoryName,
            style = MaterialTheme.typography.titleLarge.copy(
                color = Color.Black,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier
                .padding(top = 8.dp, bottom = 4.dp)
                .align(Alignment.CenterHorizontally)
        )
        LazyRow(modifier = Modifier.padding(vertical = 8.dp)) {
            items(videogames.size) { index ->
                val game = videogames[index]
                Card(
                    modifier = Modifier
                        .width(260.dp)
                        .height(270.dp)
                        .padding(horizontal = 12.dp)
                        .clickable { onVideogameClick(game) },
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE0E0E0)), // gris card login
                    elevation = CardDefaults.cardElevation(10.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                                .background(Color(0xFFB3B3B3)) // gris input login
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(game.imageCarousel),
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
                                style = MaterialTheme.typography.titleLarge.copy(
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                            Text(
                                text = game.subtitle,
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF666666)),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            if (game.category.isNotEmpty()) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    game.category.forEach { cat ->
                                        Surface(
                                            shape = RoundedCornerShape(50),
                                            color = Color(0xFFF4D73E), // amarillo login
                                            modifier = Modifier.padding(end = 8.dp)
                                        ) {
                                            Text(
                                                text = cat,
                                                color = Color.Black,
                                                style = MaterialTheme.typography.labelMedium,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
