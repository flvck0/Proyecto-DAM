package com.example.eventoslocales.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Archivo donde defino la tipografía base de la aplicación.
 *
 * Aquí configuro los estilos principales de texto usados por Material 3,
 * como tamaño, peso de fuente y espaciado.
 *
 * Decisión: utilizo la tipografía por defecto del sistema para mantener
 * una lectura clara y compatible en todos los dispositivos Android.
 * En el futuro, puedo reemplazar `FontFamily.Default` por una fuente personalizada.
 */

// Conjunto inicial de estilos tipográficos de Material Design 3
val Typography = Typography(
    // Estilo general para textos largos o contenido principal (párrafos, descripciones, etc.)
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,   // Fuente del sistema
        fontWeight = FontWeight.Normal,    // Peso medio, ideal para lectura prolongada
        fontSize = 16.sp,                  // Tamaño base estándar
        lineHeight = 24.sp,                // Altura de línea para mantener espacio entre líneas
        letterSpacing = 0.5.sp             // Ligero espaciado para mejorar legibilidad
    )

    /* Otros estilos disponibles que se pueden personalizar más adelante:

    // Títulos grandes: usados en headers o pantallas principales
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),

    // Etiquetas o textos secundarios pequeños (botones, subtítulos)
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)
