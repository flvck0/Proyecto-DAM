package com.example.eventoslocales.ui.theme

import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.eventoslocales.model.Event

/**
 * Pantalla de detalle de evento.
 *
 * Responsabilidades:
 * - Mostrar toda la información disponible de un evento seleccionado.
 * - Permitir acciones rápidas como:
 *   → Ver la ubicación en Google Maps.
 *   → Compartir el evento con otros usuarios.
 *   → Agregarlo al calendario del dispositivo.
 *
 * Decisión: todas las acciones se manejan con Intents nativos de Android
 * para mantener la app ligera y aprovechar las integraciones del sistema.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    event: Event,          // Evento seleccionado (se obtiene desde el ViewModel)
    onBack: () -> Unit     // Callback para regresar a la pantalla anterior
) {
    val context = LocalContext.current

    /**
     * Abre la aplicación de Google Maps en modo navegación
     * hacia las coordenadas del evento.
     *
     * Decisión: se intenta primero abrir la app nativa de Maps; si no está instalada,
     * se usa el navegador web como alternativa.
     */
    fun openMapsForNavigation() {
        val uri = Uri.parse("google.navigation:q=${event.latitude},${event.longitude}")
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage("com.google.android.apps.maps")
        }

        // Verifico si hay alguna app que pueda manejar el intent
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            val webUri = Uri.parse("http://maps.google.com/maps?daddr=${event.latitude},${event.longitude}")
            context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
        }
    }

    /**
     * Permite compartir la información del evento con otras aplicaciones.
     *
     * Usa un Intent implícito con ACTION_SEND, que lanza un selector
     * de apps compatibles (WhatsApp, Gmail, Telegram, etc.).
     */
    fun share() {
        val text = buildString {
            appendLine("¡No te pierdas este evento!")
            appendLine("Evento: ${event.title}")
            appendLine("Categoría: ${event.category}")
            if (event.description.isNotBlank()) appendLine("Descripción: ${event.description}")
            appendLine("Ubicación (Lat, Lon): ${event.latitude}, ${event.longitude}")
        }
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(intent, "Compartir evento"))
    }

    /**
     * Agrega el evento al calendario del dispositivo.
     *
     * Decisión: uso CalendarContract para crear una nueva entrada de calendario
     * con los datos básicos del evento (título, descripción, ubicación).
     */
    fun addToCalendar() {
        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, event.title)
            putExtra(CalendarContract.Events.DESCRIPTION, event.description)
            putExtra(
                CalendarContract.Events.EVENT_LOCATION,
                "${event.latitude}, ${event.longitude}"
            )
        }
        context.startActivity(intent)
    }

    // Estructura visual de la pantalla
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de evento") },
                navigationIcon = {
                    // Botón de regreso a la pantalla anterior
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Título del evento
            Text(
                event.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            // Categoría destacada en color primario
            Text(
                "Categoría: ${event.category}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )

            // Descripción del evento (si existe)
            if (event.description.isNotBlank()) {
                Text(
                    event.description,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            // Coordenadas del evento (útil para depuración o ubicación precisa)
            Text(
                "Ubicación: ${event.latitude}, ${event.longitude}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(8.dp))

            // Botón: abrir el evento en Google Maps
            Button(
                onClick = { openMapsForNavigation() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Directions, contentDescription = null)
                Spacer(Modifier.height(0.dp))
                Text("Cómo llegar", modifier = Modifier.padding(start = 8.dp))
            }

            // Botón: compartir evento
            Button(
                onClick = { share() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Share, contentDescription = null)
                Spacer(Modifier.height(0.dp))
                Text("Compartir", modifier = Modifier.padding(start = 8.dp))
            }

            // Botón: añadir evento al calendario del dispositivo
            Button(
                onClick = { addToCalendar() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.CalendarToday, contentDescription = null)
                Spacer(Modifier.height(0.dp))
                Text("Añadir al calendario", modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}
