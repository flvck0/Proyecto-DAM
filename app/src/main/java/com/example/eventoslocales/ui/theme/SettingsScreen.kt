package com.example.eventoslocales.ui.theme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Pantalla de ajustes de la aplicación.
 *
 * Responsabilidades:
 * - Permitir cambiar entre modo claro y oscuro.
 * - Mostrar información básica de la app.
 * - Ofrecer opción para cerrar sesión.
 *
 * Decisión: toda la lógica de estado (tema oscuro o login) se maneja
 * desde el nivel superior (MainActivity), así esta pantalla se mantiene
 * completamente desacoplada de ViewModels y solo dispara callbacks.
 */
@Composable
fun SettingsScreen(
    darkTheme: Boolean,              // Estado actual del tema (true = oscuro)
    onToggleTheme: (Boolean) -> Unit, // Callback que cambia el tema global
    onLogout: () -> Unit              // Callback que cierra sesión y vuelve al Login
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Título principal de la pantalla de ajustes
        Text(
            text = "Ajustes de la Aplicación",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        // 🔦 Switch para cambiar entre tema claro y oscuro
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Tema oscuro")
            Switch(
                checked = darkTheme,
                onCheckedChange = { onToggleTheme(it) } // Llama al callback de MainActivity
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tarjeta informativa con detalles de la aplicación
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    // En el futuro se podría mostrar un diálogo con más info
                },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            ListItem(
                headlineContent = { Text("Acerca de la App") },
                supportingContent = {
                    Text("Versión 1.0 - Desarrollado con Kotlin y Compose.")
                },
                leadingContent = {
                    Icon(Icons.Default.Info, contentDescription = "Información")
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para cerrar sesión
        Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.Logout,
                contentDescription = "Cerrar Sesión",
                modifier = Modifier.padding(end = 8.dp)
            )
            Text("Cerrar Sesión")
        }
    }
}
