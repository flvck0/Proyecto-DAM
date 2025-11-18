package com.example.eventoslocales.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.eventoslocales.R
import com.example.eventoslocales.ui.theme.viewmodel.AuthViewModel

/**
 * LOGIN PROFESIONAL – Con imagen de fondo, overlay degradado y tarjeta flotante
 *
 * Cambios realizados:
 * ✔ Fondo con foto + blur → da un look tipo Netflix / Instagram
 * ✔ Overlay con degradado para lectura perfecta
 * ✔ Tarjeta flotante blanca/negra según tema
 * ✔ Inputs estilizados con sombra ligera
 * ✔ Botón ancho, llamativo y moderno
 */
@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit
) {
    val email by viewModel.email.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    fun validatePassword(pwd: String): String? =
        if (pwd.length < 8) "La contraseña debe tener al menos 8 caracteres." else null

    fun handleLogin() {
        passwordError = validatePassword(password)
        if (passwordError == null) {
            viewModel.login(onLoginSuccess)
        }
    }

    // Fondo completo con imagen + blur + overlay
    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        // (1) Imagen de fondo
        Image(
            painter = painterResource(id = R.drawable.login_bg),
            contentDescription = "Fondo de eventos",
            modifier = Modifier
                .fillMaxSize()
                .blur(8.dp), // blur suave para estética premium
            contentScale = ContentScale.Crop
        )

        // (2) Overlay degradado para hacer legible el contenido
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.6f),
                            Color.Black.copy(alpha = 0.85f)
                        )
                    )
                )
        )

        // (3) Contenido centrado (tarjeta + título)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // Título principal llamativo
            Text(
                "Descubre increíbles eventos!!",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )

            // Pequeño subtítulo que “vende” mejor la app
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Conciertos, talleres, ferias y más, todo cerca de ti.",
                color = Color.White.copy(alpha = 0.85f),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            // Más espacio bajo el subtítulo (separación visual perfecta)
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp), // separación un poco mayor
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.wrapContentWidth()
            ) {
                SuggestionChip("Conciertos", scale = 1.1f)
                SuggestionChip("Gastronomía", scale = 1.1f)
                SuggestionChip("Deporte", scale = 1.1f)
            }

            // Más distancia antes de la tarjeta (equilibrio visual)
            Spacer(modifier = Modifier.height(30.dp))



            // TARJETA DEL FORMULARIO
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                ),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // EMAIL
                    OutlinedTextField(
                        value = email,
                        onValueChange = viewModel::onEmailChange,
                        label = { Text("Correo Electrónico") },
                        leadingIcon = { Icon(Icons.Default.MailOutline, null) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // CONTRASEÑA
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            passwordError = validatePassword(it)
                        },
                        label = { Text("Contraseña") },
                        leadingIcon = { Icon(Icons.Default.Lock, null) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    if (passwordVisible) Icons.Default.VisibilityOff
                                    else Icons.Default.Visibility,
                                    contentDescription = null
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible)
                            VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        isError = passwordError != null,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions { handleLogin() },
                        supportingText = {
                            passwordError?.let {
                                Text(it, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (errorMessage != null) {
                        Text(
                            errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // BOTÓN DE INGRESAR
                    Button(
                        onClick = { handleLogin() },
                        enabled = email.isNotBlank() &&
                                password.isNotBlank() &&
                                passwordError == null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Ingresar")
                    }

                }
            }
        }
    }
}

@Composable
private fun SuggestionChip(text: String, scale: Float = 1f) {
    Surface(
        shape = RoundedCornerShape(50),
        color = Color.Black.copy(alpha = 0.50f),
        tonalElevation = 2.dp,
        modifier = Modifier
    ) {
        Text(
            text = text,
            color = Color.White.copy(alpha = 0.95f),
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier
                .padding(
                    horizontal = (14 * scale).dp,
                    vertical = (6 * scale).dp
                )
        )
    }
}

