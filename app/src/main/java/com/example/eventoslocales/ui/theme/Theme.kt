package com.example.eventoslocales.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Archivo responsable de definir la identidad visual de toda la app.
 *
 * Aquí se configuran los colores principales, secundarios y de fondo
 * tanto para el modo claro como el modo oscuro.
 *
 * Decisión: utilizo el sistema de temas de Material 3 para mantener coherencia
 * visual y compatibilidad con los nuevos componentes (Scaffold, Button, etc.).
 */

// Paleta de colores para el modo oscuro
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF64B5F6),     // Azul suave como color principal
    onPrimary = Color.Black,         // Texto o íconos que se dibujan encima del color primario
    secondary = Color(0xFF4DB6AC),   // Verde agua para acentos
    tertiary = Color(0xFFE57373),    // Rojo suave para acciones secundarias
    background = Color(0xFF121212),  // Fondo general oscuro
    surface = Color(0xFF1E1E1E),     // Superficies como tarjetas o menús
    onSurface = Color.White,         // Texto principal sobre fondo oscuro
    tertiaryContainer = Color(0xFF380000) // Contenedor decorativo para variaciones del rojo
)

// Paleta de colores para el modo claro
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1565C0),     // Azul más intenso para el modo claro
    onPrimary = Color.White,         // Texto sobre el color primario
    secondary = Color(0xFF00897B),   // Verde esmeralda para resaltar secciones o botones
    tertiary = Color(0xFFD32F2F),    // Rojo intenso para acciones secundarias
    background = Color(0xFFFFFFFF),  // Fondo blanco general
    surface = Color(0xFFF5F5F5),     // Superficies ligeramente grises (para contraste suave)
    onSurface = Color.Black,         // Texto principal sobre fondo claro
    tertiaryContainer = Color(0xFFFFCDD2) // Variante rosada para resaltar bloques
)

/**
 * Tema global de la aplicación.
 *
 * Recibe un parámetro [darkTheme] para decidir cuál esquema aplicar,
 * y envuelve el contenido visual dentro del sistema de MaterialTheme.
 *
 * Decisión: mantengo la tipografía por defecto de Material 3,
 * pero el esquema de colores es completamente personalizado.
 *
 * Gracias a esto, toda la UI (botones, textos, barras, etc.)
 * adapta automáticamente sus tonos según el tema seleccionado.
 */
@Composable
fun AppTheme(
    darkTheme: Boolean,                // true = modo oscuro activo
    content: @Composable () -> Unit    // Contenido principal de la UI
) {
    // Selecciono el esquema de color según el estado del tema
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    // Aplico el tema global con Material 3
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(), // Tipografía por defecto de Material 3
        content = content
    )
}


