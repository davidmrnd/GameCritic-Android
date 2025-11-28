package es.ulpgc.gamecritic.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import es.ulpgc.gamecritic.util.ImageUtils
import es.ulpgc.gamecritic.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import android.util.Base64

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
                    try {
                        if (!currentImageBase64.isNullOrBlank()) {
                            val lower = currentImageBase64.lowercase()
                            if (lower.startsWith("http://") || lower.startsWith("https://") || lower.startsWith("content://")) {
                                null
                            } else {
                                val bytes = Base64.decode(currentImageBase64, Base64.DEFAULT)
                                android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
                            }
                        } else null
                    } catch (e: Exception) {
                        null
                    }
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
                            scope.launch {
                                if (success) {
                                    snackbarHostState.showSnackbar("Perfil actualizado")
                                    onSave()
                                } else {
                                    snackbarHostState.showSnackbar("Error al guardar")
                                }
                            }
                        }
                    },
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF4D73E))
                ) {
                    Text("Guardar", fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = onCancel,
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
