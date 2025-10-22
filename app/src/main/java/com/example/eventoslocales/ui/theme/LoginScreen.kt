package com.example.eventoslocales.ui.theme

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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.eventoslocales.ui.theme.viewmodel.AuthViewModel

/**
 * Pantalla de Login de la aplicación.
 *
 * Decisiones clave:
 * - Compose maneja el estado de los campos (email, contraseña, errores) directamente desde el ViewModel.
 * - Se usa collectAsStateWithLifecycle() para suscribirse de forma segura a Flows del ViewModel.
 * - Incluye validación básica de contraseña y feedback visual inmediato en el campo.
 */
@Composable
fun LoginScreen(
    viewModel: AuthViewModel,        // ViewModel que maneja la lógica de autenticación
    onLoginSuccess: () -> Unit       // Callback que navega al mapa cuando el login es exitoso
) {
    // Estado reactivo del email controlado por el ViewModel
    val email by viewModel.email.collectAsStateWithLifecycle()
    // Error general proveniente del ViewModel (por ejemplo, email inválido o credenciales erróneas)
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    // Estados locales (solo de UI)
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    // Validación mínima de contraseña
    fun validatePassword(pwd: String): String? =
        if (pwd.length < 8) "La contraseña debe tener al menos 8 caracteres." else null

    // Lógica de login: primero valido localmente, luego delego al ViewModel
    fun handleLogin() {
        passwordError = validatePassword(password)
        if (passwordError == null) {
            viewModel.login(onLoginSuccess)
        }
    }

    // Estructura general de la pantalla
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Título principal de la pantalla
            Text(
                text = "Descubre increíbles eventos!!",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 40.dp)
            )

            // Campo de correo electrónico
            OutlinedTextField(
                value = email,
                onValueChange = viewModel::onEmailChange, // Actualiza directamente el estado del ViewModel
                label = { Text("Correo Electrónico") },
                leadingIcon = { Icon(Icons.Default.MailOutline, contentDescription = "Email") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de contraseña con validación y opción de mostrar/ocultar texto
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = validatePassword(it)
                },
                label = { Text("Contraseña") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Contraseña") },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (passwordVisible) "Ocultar" else "Mostrar"
                        )
                    }
                },
                // Si el ícono está activo, muestro el texto plano; si no, lo oculto con puntos
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { handleLogin() }),
                singleLine = true,
                isError = passwordError != null,
                supportingText = {
                    // Feedback de error dinámico debajo del campo
                    passwordError?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Mensaje de error proveniente del ViewModel (por ejemplo, email mal formado)
            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botón principal de login
            Button(
                onClick = { handleLogin() },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = email.isNotBlank() && password.isNotBlank() && passwordError == null
            ) {
                Text("Ingresar", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
