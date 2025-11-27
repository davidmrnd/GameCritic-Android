package es.ulpgc.gamecritic.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import es.ulpgc.gamecritic.model.Videogame
import es.ulpgc.gamecritic.repository.VideogameRepository
import es.ulpgc.gamecritic.ui.components.VideogameCarousel

// Usamos el mismo color de fondo que HomeScreen
private val ExploreBackground = Color(0xFFF0ECE3)
// Color para el texto que contraste bien con el fondo claro
private val DarkTextColor = Color(0xFF333333)

// Colores específicos para el degradado amarillo
private val YellowGradientStart = Color(0xFFF2D53E) // Amarillo vibrante (puede ser un amarillo de tu paleta)
private val YellowGradientEnd = Color(0xFFFFE36D) // Tono más suave del amarillo

@Composable
fun ExploreScreen(navController: NavController) {
    val scrollState = rememberScrollState()
    val repository = remember { VideogameRepository() }

    // Mapa categoria -> lista de juegos
    val videogamesByCategory = remember { mutableStateMapOf<String, List<Videogame>>() }

    LaunchedEffect(Unit) {
        // Cargamos todas las categorías reales desde Firebase y las guardamos en el mapa
        val grouped = repository.getVideogamesGroupedByCategory()
        videogamesByCategory.clear()
        videogamesByCategory.putAll(grouped)
    }

    Scaffold(
        containerColor = ExploreBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            // CABECERA CON DEGRADADO AMARILLO
            ExploreHeaderGradient()

            // Espacio entre la cabecera y el primer carrusel
            Spacer(modifier = Modifier.height(16.dp))

            // Carruseles de videojuegos (Espaciado reducido)
            videogamesByCategory.forEach { (categoryName, games) ->
                if (games.isNotEmpty()) {
                    // Encabezado con barra + nombre de categoría
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 0.dp, start = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(width = 4.dp, height = 26.dp)
                                .background(YellowGradientStart, RoundedCornerShape(2.dp))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = categoryName,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                color = DarkTextColor,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    VideogameCarousel(
                        videogames = games,
                        categoryName = "",
                        onVideogameClick = { game ->
                            navController.navigate("videogame_detail/${game.id}")
                        }
                    )

                    // Espaciador reducido (8.dp) para acercar el título del siguiente carrusel
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            // Espacio al final del último carrusel
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Componente para el encabezado de la vista de exploración con un fondo de degradado amarillo.
 */
@Composable
fun ExploreHeaderGradient() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            // Padding exterior para que no toque los bordes de la pantalla
            .padding(horizontal = 16.dp, vertical = 20.dp)
            // Aplicamos el degradado horizontal con tonos amarillos
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(YellowGradientStart, YellowGradientEnd)
                ),
                shape = RoundedCornerShape(12.dp) // Esquinas redondeadas
            )
            // Padding interno para el contenido
            .padding(20.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Column {
            Text(
                text = "Explora los Géneros",
                // Usamos el color oscuro para el texto para asegurar buena legibilidad sobre el amarillo
                color = DarkTextColor,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Navega por las diferentes categorías y encuentra los títulos mejor valorados.",
                color = DarkTextColor.copy(alpha = 0.7f),
                fontSize = 14.sp
            )
        }
    }
}