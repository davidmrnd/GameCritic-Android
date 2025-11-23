package es.ulpgc.gamecritic.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import es.ulpgc.gamecritic.viewmodel.VideogameCarouselViewModel
import es.ulpgc.gamecritic.ui.components.VideogameCarousel

@Composable
fun HomeScreen() {
    val carouselViewModel: VideogameCarouselViewModel = viewModel()
    val videogames = carouselViewModel.videogames

    LaunchedEffect(Unit) {
        carouselViewModel.loadVideogamesByCategory("Novedades")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0ECE3))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "GameCritic",
            style = MaterialTheme.typography.headlineMedium.copy(
                color = Color.Black
            ),
            modifier = Modifier.padding(vertical = 16.dp)
        )
        VideogameCarousel(
            videogames = videogames,
            categoryName = "Novedades"
        )
    }
}
