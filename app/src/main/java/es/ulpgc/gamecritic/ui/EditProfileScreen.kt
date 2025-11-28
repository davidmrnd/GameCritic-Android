package es.ulpgc.gamecritic.ui

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import es.ulpgc.gamecritic.util.ImageUtils
import es.ulpgc.gamecritic.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch

@Composable
fun EditProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    onSave: () -> Unit = {},
    onCancel: () -> Unit = {}
) {
    val user = viewModel.user
    var name by remember { mutableStateOf(TextFieldValue(user?.name ?: "")) }
    var username by remember { mutableStateOf(TextFieldValue(user?.username ?: "")) }
    var description by remember { mutableStateOf(TextFieldValue(user?.description ?: "")) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
                selectedImageUri = it
                val base64 = ImageUtils.uriToBase64(context, it)
                viewModel.setEditingProfileImage(base64)
            }
        }
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
        onResult = { bitmap ->
            if (bitmap != null) {
                val base64 = ImageUtils.bitmapToBase64(bitmap)
                viewModel.setEditingProfileImage(base64)
            }
        }
    )

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                cameraLauncher.launch(null)
            } else {
                scope.launch { snackbarHostState.showSnackbar("Permiso de cámara denegado") }
            }
        }
    )

    LaunchedEffect(user) {
        name = TextFieldValue(user?.name ?: "")
        username = TextFieldValue(user?.username ?: "")
        description = TextFieldValue(user?.description ?: "")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0ECE3)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .width(375.dp)
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Editar Perfil", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))

            val currentImageBase64 = viewModel.editingProfileImageBase64 ?: user?.profileIcon

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
                    .clickable { },
                contentAlignment = Alignment.Center
            ) {
                val imageBitmap = remember(currentImageBase64) {
                    ImageUtils.decodeToImageBitmapOrNull(currentImageBase64)
                }

                when {
                    imageBitmap != null -> {
                        androidx.compose.foundation.Image(
                            bitmap = imageBitmap,
                            contentDescription = "Foto de perfil",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    !currentImageBase64.isNullOrBlank() -> {
                        AsyncImage(
                            model = currentImageBase64,
                            contentDescription = "Foto de perfil",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    else -> {
                        Text("Sin foto")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(onClick = {
                    galleryLauncher.launch("image/*")
                }) {
                    Text("Galería")
                }
                OutlinedButton(onClick = {
                    val permission = Manifest.permission.CAMERA
                    val hasPermission = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
                    if (hasPermission) {
                        cameraLauncher.launch(null)
                    } else {
                        cameraPermissionLauncher.launch(permission)
                    }
                }) {
                    Text("Cámara")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        viewModel.editProfile(
                            name.text,
                            username.text,
                            description.text
                        ) { success ->
                            if (success) {
                                onSave()
                            } else {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Error al guardar")
                                }
                            }
                        }
                    },
                    enabled = !viewModel.isSavingProfile,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF4D73E))
                ) {
                    if (viewModel.isSavingProfile) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.Black,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text("Guardar", fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = onCancel,
                    enabled = !viewModel.isSavingProfile,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
                ) {
                    Text("Cancelar")
                }
            }
        }
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            SnackbarHost(hostState = snackbarHostState)
        }
    }
}
