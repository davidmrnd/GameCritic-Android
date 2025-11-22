package es.ulpgc.gamecritic.ui

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.PasswordVisualTransformation
import es.ulpgc.gamecritic.viewmodel.RegisterState
import es.ulpgc.gamecritic.viewmodel.RegisterViewModel
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.draw.shadow

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
    viewModel: RegisterViewModel = viewModel()
) {
    val registerState by viewModel.registerState.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }
    var showErrors by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }

    val emailError = showErrors && (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches())
    val passwordError = showErrors && password.isBlank()
    val repeatPasswordError = showErrors && (repeatPassword.isBlank() || password != repeatPassword)
    val nameError = showErrors && name.isBlank()
    val usernameError = showErrors && username.isBlank()

    Scaffold(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF0ECE3)),
        containerColor = Color(0xFFF0ECE3)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .width(375.dp)
                    .shadow(8.dp, RoundedCornerShape(20.dp))
                    .background(Color(0xFFE0E0E0), RoundedCornerShape(20.dp))
                    .padding(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Registrarse",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text("Nombre completo") },
                    singleLine = true,
                    isError = nameError,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (nameError) Color.Red else Color.Transparent,
                        unfocusedBorderColor = if (nameError) Color.Red else Color.Transparent,
                        errorBorderColor = Color.Red,
                        focusedLabelColor = Color.Black,
                        unfocusedLabelColor = Color.Black,
                        errorLabelColor = Color.Red,
                        cursorColor = Color.Black,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        errorTextColor = Color.Red,
                        focusedContainerColor = Color(0xFFB3B3B3),
                        unfocusedContainerColor = Color(0xFFB3B3B3),
                        errorContainerColor = Color(0xFFB3B3B3)
                    )
                )
                if (nameError) {
                    Text(
                        text = "El nombre es obligatorio.",
                        color = Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.Start)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    placeholder = { Text("Nombre de usuario") },
                    singleLine = true,
                    isError = usernameError,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (usernameError) Color.Red else Color.Transparent,
                        unfocusedBorderColor = if (usernameError) Color.Red else Color.Transparent,
                        errorBorderColor = Color.Red,
                        focusedLabelColor = Color.Black,
                        unfocusedLabelColor = Color.Black,
                        errorLabelColor = Color.Red,
                        cursorColor = Color.Black,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        errorTextColor = Color.Red,
                        focusedContainerColor = Color(0xFFB3B3B3),
                        unfocusedContainerColor = Color(0xFFB3B3B3),
                        errorContainerColor = Color(0xFFB3B3B3)
                    )
                )
                if (usernameError) {
                    Text(
                        text = "El nombre de usuario es obligatorio.",
                        color = Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.Start)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("Correo") },
                    singleLine = true,
                    isError = emailError,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (emailError) Color.Red else Color.Transparent,
                        unfocusedBorderColor = if (emailError) Color.Red else Color.Transparent,
                        errorBorderColor = Color.Red,
                        focusedLabelColor = Color.Black,
                        unfocusedLabelColor = Color.Black,
                        errorLabelColor = Color.Red,
                        cursorColor = Color.Black,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        errorTextColor = Color.Red,
                        focusedContainerColor = Color(0xFFB3B3B3),
                        unfocusedContainerColor = Color(0xFFB3B3B3),
                        errorContainerColor = Color(0xFFB3B3B3)
                    )
                )
                if (emailError) {
                    Text(
                        text = if (email.isBlank()) "El correo es obligatorio." else "El formato del correo no es válido.",
                        color = Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.Start)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("Contraseña") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    isError = passwordError,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (passwordError) Color.Red else Color.Transparent,
                        unfocusedBorderColor = if (passwordError) Color.Red else Color.Transparent,
                        errorBorderColor = Color.Red,
                        focusedLabelColor = Color.Black,
                        unfocusedLabelColor = Color.Black,
                        errorLabelColor = Color.Red,
                        cursorColor = Color.Black,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        errorTextColor = Color.Red,
                        focusedContainerColor = Color(0xFFB3B3B3),
                        unfocusedContainerColor = Color(0xFFB3B3B3),
                        errorContainerColor = Color(0xFFB3B3B3)
                    )
                )
                if (passwordError) {
                    Text(
                        text = "La contraseña es obligatoria.",
                        color = Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.Start)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = repeatPassword,
                    onValueChange = { repeatPassword = it },
                    placeholder = { Text("Repite la contraseña") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    isError = repeatPasswordError,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (repeatPasswordError) Color.Red else Color.Transparent,
                        unfocusedBorderColor = if (repeatPasswordError) Color.Red else Color.Transparent,
                        errorBorderColor = Color.Red,
                        focusedLabelColor = Color.Black,
                        unfocusedLabelColor = Color.Black,
                        errorLabelColor = Color.Red,
                        cursorColor = Color.Black,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        errorTextColor = Color.Red,
                        focusedContainerColor = Color(0xFFB3B3B3),
                        unfocusedContainerColor = Color(0xFFB3B3B3),
                        errorContainerColor = Color(0xFFB3B3B3)
                    )
                )
                if (repeatPasswordError) {
                    Text(
                        text = "Las contraseñas no coinciden.",
                        color = Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.Start)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        showErrors = true
                        if (!nameError && !usernameError && !emailError && !passwordError && !repeatPasswordError) {
                            viewModel.register(name, username, email, password, repeatPassword)
                        }
                    },
                    enabled = !nameError && !usernameError && !emailError && !passwordError && !repeatPasswordError && registerState != RegisterState.Loading,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF4D73E)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp, bottom = 10.dp)
                ) {
                    Text(
                        "Registrarse",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
                if (registerState is RegisterState.Error) {
                    Text(
                        text = (registerState as RegisterState.Error).message,
                        color = Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                if (registerState is RegisterState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.padding(top = 8.dp))
                }
                if (registerState is RegisterState.Success) {
                    LaunchedEffect(Unit) { onRegisterSuccess() }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("¿Ya tienes cuenta?", fontWeight = FontWeight.Bold)
                    TextButton(
                        onClick = { onNavigateToLogin() },
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            "Iniciar sesión",
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                shadow = Shadow(
                                    color = Color.Black,
                                    offset = Offset(2f, 2f),
                                    blurRadius = 4f
                                )
                            ),
                            modifier = Modifier.padding(5.dp)
                        )
                    }
                }
            }
        }
    }
}
