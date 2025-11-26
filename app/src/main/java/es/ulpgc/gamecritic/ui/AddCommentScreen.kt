package es.ulpgc.gamecritic.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import es.ulpgc.gamecritic.viewmodel.AddCommentViewModel

private val DangerColorLocal = Color(0xFFB91D1D)

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
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.isEditMode) "Editar comentario" else "Añadir comentario") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFE0E0E0)
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .background(Color(0xFFF0ECE3)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Valora este videojuego", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                (1..5).forEach { star ->
                    IconButton(onClick = { viewModel.onRatingChange(star) }) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Estrella $star",
                            tint = if (star <= uiState.rating) Color(0xFFF4D73E) else Color.Gray,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
            if (uiState.ratingError != null) {
                Text(uiState.ratingError!!, color = Color.Red, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.content,
                onValueChange = { viewModel.onContentChange(it) },
                label = { Text("Escribe tu comentario") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                maxLines = 5
            )
            if (uiState.contentError != null) {
                Text(uiState.contentError!!, color = Color.Red, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.submit(onSuccess = onCommentSaved) },
                enabled = !uiState.isSaving,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF4D73E)),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        color = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(if (uiState.isEditMode) "Guardar cambios" else "Publicar comentario")
                }
            }

            if (uiState.isEditMode && uiState.existingCommentId != null) {
                Spacer(modifier = Modifier.height(12.dp))
                TextButton(
                    onClick = { showDeleteDialog = true },
                    enabled = !uiState.isSaving
                ) {
                    Text("Eliminar comentario", color = MaterialTheme.colorScheme.error)
                }
            }

            uiState.saveError?.let { error ->
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = error, color = Color.Red)
            }
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = {
                    Text(
                        text = "Eliminar comentario",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.Black
                    )
                },
                text = {
                    Text(
                        text = "¿Seguro que deseas eliminar este comentario?",
                        style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
                        color = Color.Black
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        showDeleteDialog = false
                        viewModel.deleteComment(onSuccess = onCommentSaved)
                    }) {
                        Text("Sí, eliminar comentario", color = DangerColorLocal, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancelar", color = Color.Black)
                    }
                },
                containerColor = Color(0xFFE0E0E0)
            )
        }
    }
}
