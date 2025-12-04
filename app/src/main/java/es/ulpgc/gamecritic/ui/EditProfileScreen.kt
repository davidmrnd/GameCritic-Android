package es.ulpgc.gamecritic.ui

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import es.ulpgc.gamecritic.util.ImageUtils
import es.ulpgc.gamecritic.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch

private val LightBackground = Color(0xFFF0ECE3)
private val LightSurface = Color(0xFFFFFFFF)
private val TextBlack = Color(0xFF111827)
private val TextDarkGray = Color(0xFF4B5563)
private val MyYellow = Color(0xFFF4D73E)
private val MyYellowDark = Color(0xFFF4D73E)

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
    var showImageSourceDialog by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
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
            if (isGranted) cameraLauncher.launch(null)
            else scope.launch { snackbarHostState.showSnackbar("Permiso de cámara denegado") }
        }
    )

    LaunchedEffect(user) {
        name = TextFieldValue(user?.name ?: "")
        username = TextFieldValue(user?.username ?: "")
        description = TextFieldValue(user?.description ?: "")
    }

    Scaffold(
        containerColor = LightBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
                    text = "EDITAR PERFIL",
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
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clickable { showImageSourceDialog = true }
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .background(Color(0xFFE5E7EB))
                                    .shadow(4.dp, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                val currentImageBase64 = viewModel.editingProfileImageBase64 ?: user?.profileIcon
                                val imageBitmap = remember(currentImageBase64) {
                                    ImageUtils.decodeToImageBitmapOrNull(currentImageBase64)
                                }

                                when {
                                    imageBitmap != null -> Image(
                                        bitmap = imageBitmap,
                                        contentDescription = "Avatar",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                    !currentImageBase64.isNullOrBlank() -> AsyncImage(
                                        model = currentImageBase64,
                                        contentDescription = "Avatar",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                    else -> Icon(
                                        Icons.Default.Person,
                                        contentDescription = null,
                                        tint = TextDarkGray,
                                        modifier = Modifier.size(40.dp)
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(MyYellow)
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Cambiar foto",
                                    tint = TextBlack,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        StyledTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = "Nombre"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextField(
                            value = username,
                            onValueChange = { username = it },
                            label = "Usuario (@)"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = "Descripción",
                            singleLine = false,
                            maxLines = 4
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            OutlinedButton(
                                onClick = onCancel,
                                enabled = !viewModel.isSavingProfile,
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, TextDarkGray),
                                modifier = Modifier.weight(1f).height(50.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextBlack)
                            ) {
                                Text("Cancelar", fontWeight = FontWeight.SemiBold)
                            }

                            Button(
                                onClick = {
                                    viewModel.editProfile(name.text, username.text, description.text) { success ->
                                        if (success) onSave()
                                        else scope.launch { snackbarHostState.showSnackbar("Error al guardar cambios") }
                                    }
                                },
                                enabled = !viewModel.isSavingProfile,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MyYellow,
                                    disabledContainerColor = MyYellow.copy(alpha = 0.5f)
                                ),
                                modifier = Modifier.weight(1f).height(50.dp)
                            ) {
                                if (viewModel.isSavingProfile) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = TextBlack,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text("Guardar", color = TextBlack, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        if (showImageSourceDialog) {
            AlertDialog(
                onDismissRequest = { showImageSourceDialog = false },
                containerColor = LightSurface,
                title = { Text("Cambiar foto de perfil", fontWeight = FontWeight.Bold, color = TextBlack) },
                text = {
                    Column {
                        ListItem(
                            headlineContent = { Text("Tomar foto", color = TextBlack) },
                            leadingContent = { Icon(Icons.Default.Person, null, tint = MyYellow) },
                            modifier = Modifier.clickable {
                                showImageSourceDialog = false
                                val permission = Manifest.permission.CAMERA
                                if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                                    cameraLauncher.launch(null)
                                } else {
                                    cameraPermissionLauncher.launch(permission)
                                }
                            }
                        )
                        ListItem(
                            headlineContent = { Text("Elegir de galería", color = TextBlack) },
                            leadingContent = { Icon(Icons.Default.List, null, tint = MyYellow) },
                            modifier = Modifier.clickable {
                                showImageSourceDialog = false
                                galleryLauncher.launch("image/*")
                            }
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showImageSourceDialog = false }) {
                        Text("Cancelar", color = TextDarkGray)
                    }
                }
            )
        }
    }
}

@Composable
fun StyledTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    label: String,
    singleLine: Boolean = true,
    maxLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = singleLine,
        maxLines = maxLines,
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
}