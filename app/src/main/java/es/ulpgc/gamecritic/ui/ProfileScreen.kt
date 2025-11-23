package es.ulpgc.gamecritic.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import es.ulpgc.gamecritic.viewmodel.ProfileViewModel
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip

@Composable
fun ProfileScreen(viewModel: ProfileViewModel = viewModel()) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0ECE3))
            .padding(24.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(32.dp))
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (viewModel.profileIcon.isNotBlank()) {
                    Image(
                        painter = rememberAsyncImagePainter(viewModel.profileIcon),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFD9D9D9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("", color = Color.Gray)
                    }
                }
                Spacer(modifier = Modifier.width(24.dp))
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(
                        text = viewModel.name.uppercase(),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = viewModel.username.uppercase(),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Black
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = viewModel.description,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Black
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Valoraciones",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black)
                    )
                    Text(
                        text = "0",
                        style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Seguidores",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black)
                    )
                    Text(
                        text = viewModel.followersCount.toString(),
                        style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Seguidos",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black)
                    )
                    Text(
                        text = viewModel.followingCount.toString(),
                        style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black)
                    )
                }
            }
        }
    }
}
