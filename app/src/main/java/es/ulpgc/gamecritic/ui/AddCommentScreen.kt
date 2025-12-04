package es.ulpgc.gamecritic.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import es.ulpgc.gamecritic.viewmodel.AddCommentViewModel

private val LightBackground = Color(0xFFF0ECE3)
private val LightSurface = Color(0xFFFFFFFF)
private val TextBlack = Color(0xFF111827)
private val TextDarkGray = Color(0xFF4B5563)
private val MyYellow = Color(0xFFF4D73E)
private val MyYellowDark = Color(0xFFF4D73E)
private val DangerColor = Color(0xFFEF4444)
private val StarColor = Color(0xFFD4B20C)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCommentScreen(
    videogameId: String,
    onBack: () -> Unit,
    onCommentSaved: () -> Unit,
    viewModel: AddCommentViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(videogameId) {
        viewModel.setVideogameId(videogameId)
    }

    Scaffold(
        containerColor = LightBackground
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(MyYellow, MyYellowDark),
                            startY = 0f,
                            endY = 1000f
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(60.dp))

                Text(
                    text = if (uiState.isEditMode) "EDITAR RESEÑA" else "NUEVA RESEÑA",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Black,
                        color = TextBlack,
                        letterSpacing = (-0.5).sp
                    ),
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(bottom = 24.dp)
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(20.dp),
                            spotColor = TextDarkGray.copy(alpha = 0.2f)
                        ),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = LightSurface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "PUNTUACIÓN",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = TextDarkGray,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            (1..5).forEach { index ->
                                val icon = when {
                                    uiState.rating >= index -> Icons.Filled.Star
                                    uiState.rating >= index - 0.5 -> Icons.AutoMirrored.Filled.StarHalf
                                    else -> Icons.Filled.Star
                                }

                                val iconColor = if (uiState.rating >= index - 0.5) StarColor else Color.LightGray

                                IconButton(
                                    onClick = {
                                        val newRating = if (uiState.rating == index.toDouble()) {
                                            index - 0.5
                                        } else {
                                            index.toDouble()
                                        }
                                        viewModel.onRatingChange(newRating)
                                    },
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = "Estrella $index",
                                        tint = iconColor,
                                        modifier = Modifier.size(40.dp)
                                    )
                                }
                            }
                        }

                        if (uiState.ratingError != null) {
                            Text(
                                text = uiState.ratingError!!,
                                color = DangerColor,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        HorizontalDivider(color = Color(0xFFF3F4F6))
                        Spacer(modifier = Modifier.height(24.dp))

                        OutlinedTextField(
                            value = uiState.content,
                            onValueChange = { viewModel.onContentChange(it) },
                            label = { Text("Escribe tu opinión...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp),
                            maxLines = 10,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MyYellow,
                                unfocusedBorderColor = TextDarkGray.copy(alpha = 0.3f),
                                focusedLabelColor = MyYellowDark,
                                cursorColor = MyYellowDark,
                                focusedTextColor = TextBlack,
                                unfocusedTextColor = TextBlack
                            )
                        )

                        if (uiState.contentError != null) {
                            Text(
                                text = uiState.contentError!!,
                                color = DangerColor,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier
                                    .align(Alignment.Start)
                                    .padding(top = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = { viewModel.submit(onSuccess = onCommentSaved) },
                            enabled = !uiState.isSaving,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MyYellow,
                                disabledContainerColor = MyYellow.copy(alpha = 0.5f)
                            )
                        ) {
                            if (uiState.isSaving) {
                                CircularProgressIndicator(
                                    color = TextBlack,
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = if (uiState.isEditMode) "GUARDAR CAMBIOS" else "PUBLICAR RESEÑA",
                                    color = TextBlack,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        if (uiState.isEditMode && uiState.existingCommentId != null) {
                            Spacer(modifier = Modifier.height(16.dp))
                            TextButton(
                                onClick = { showDeleteDialog = true },
                                enabled = !uiState.isSaving
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = DangerColor,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Eliminar reseña",
                                    color = DangerColor,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                uiState.saveError?.let { error ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DangerColor.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = error,
                            color = DangerColor,
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }

            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .padding(top = 16.dp, start = 16.dp)
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

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                containerColor = LightSurface,
                title = {
                    Text(
                        "Eliminar reseña",
                        fontWeight = FontWeight.Bold,
                        color = TextBlack
                    )
                },
                text = {
                    Text(
                        "¿Estás seguro? Esta acción no se puede deshacer.",
                        color = TextDarkGray
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showDeleteDialog = false
                            viewModel.deleteComment(onSuccess = onCommentSaved)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DangerColor)
                    ) {
                        Text("Sí, eliminar", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancelar", color = TextBlack)
                    }
                }
            )
        }
    }
}