package es.ulpgc.gamecritic.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import es.ulpgc.gamecritic.R
import es.ulpgc.gamecritic.ui.components.VideogameCarousel
import es.ulpgc.gamecritic.viewmodel.VideogameCarouselViewModel

private val LightBackground = Color(0xFFF0ECE3)
private val LightSurface = Color(0xE2FFFFFF)
private val TextBlack = Color(0xFF111827)
private val TextDarkGray = Color(0xFF4B5563)
private val MyYellow = Color(0xFFF4D73E)
private val MyYellowDark = Color(0xFFF4D73E)

@Composable
fun HomeScreen(navController: NavController) {
    val carouselViewModel: VideogameCarouselViewModel = viewModel()
    val videogames = carouselViewModel.videogames
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        carouselViewModel.loadVideogamesByCategory("Novedades")
    }

    Scaffold(
        containerColor = LightBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
            ) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(MyYellow, MyYellowDark),
                                startY = 0f,
                                endY = 1000f
                            )
                        )
                )
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = 40.dp, y = (-40).dp)
                        .background(Color.White.copy(alpha = 0.15f), CircleShape)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, LightBackground)
                            )
                        )
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 56.dp, start = 24.dp, end = 24.dp)
                        .align(Alignment.TopStart),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .shadow(10.dp, CircleShape)
                            .background(Color.White, CircleShape)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_gamecritic),
                            contentDescription = "Logo GameCritic",
                            modifier = Modifier.size(62.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "GAME CRITIC",
                            style = MaterialTheme.typography.displaySmall.copy(
                                color = TextBlack,
                                fontWeight = FontWeight.Black,
                                letterSpacing = (-0.5).sp
                            )
                        )
                        Text(
                            text = "Donde los gamers deciden.",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = TextBlack.copy(alpha = 0.75f),
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-40).dp)
                    .padding(horizontal = 16.dp)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(16.dp),
                            spotColor = MyYellowDark
                        ),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = LightSurface)
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier
                                .width(6.dp)
                                .fillMaxHeight()
                                .align(Alignment.CenterStart)
                                .background(MyYellow)
                        )
                        Column(
                            modifier = Modifier.padding(start = 24.dp, top = 20.dp, end = 20.dp, bottom = 20.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(id = android.R.drawable.ic_menu_compass),
                                    contentDescription = null,
                                    tint = MyYellowDark,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Bienvenido",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        color = TextBlack,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stringResource(R.string.descripcion),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = TextDarkGray,
                                    lineHeight = 22.sp
                                ),
                                textAlign = TextAlign.Start
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { navController.navigate("explore") },
                                shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.buttonColors(containerColor = MyYellow),
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Text(
                                    text = "Explorar",
                                    color = TextBlack,
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 0.dp, start = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 4.dp, height = 26.dp)
                            .background(MyYellow, RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Novedades",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = TextBlack,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                VideogameCarousel(
                    videogames = videogames,
                    categoryName = "",
                    onVideogameClick = { game ->
                        navController.navigate("videogame_detail/${game.id}")
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}