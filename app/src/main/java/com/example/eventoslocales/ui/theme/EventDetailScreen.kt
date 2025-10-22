package com.example.eventoslocales.ui.theme

import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.eventoslocales.model.Event

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    event: Event,
    onBack: () -> Unit
) {
    val context = LocalContext.current


    fun openMapsForNavigation() {
        val uri = Uri.parse("google.navigation:q=${event.latitude},${event.longitude}")

        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage("com.google.android.apps.maps")
        }

        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            val webUri = Uri.parse("http://maps.google.com/maps?daddr=${event.latitude},${event.longitude}")
            context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
        }
    }

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

    fun addToCalendar() {
        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, event.title)
            putExtra(CalendarContract.Events.DESCRIPTION, event.description)

            putExtra(CalendarContract.Events.EVENT_LOCATION, "${event.latitude}, ${event.longitude}")
        }
        context.startActivity(intent)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de evento") },
                navigationIcon = {
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
            Text(
                event.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                "Categoría: ${event.category}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )

            if (event.description.isNotBlank()) {
                Text(
                    event.description,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Text(
                "Ubicación: ${event.latitude}, ${event.longitude}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(8.dp))

            Button(onClick = { openMapsForNavigation() }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Directions, contentDescription = null)
                Spacer(Modifier.height(0.dp))
                Text("Cómo llegar", modifier = Modifier.padding(start = 8.dp))
            }

            Button(onClick = { share() }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Share, contentDescription = null)
                Spacer(Modifier.height(0.dp))
                Text("Compartir", modifier = Modifier.padding(start = 8.dp))
            }

            Button(onClick = { addToCalendar() }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.CalendarToday, contentDescription = null)
                Spacer(Modifier.height(0.dp))
                Text("Añadir al calendario", modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}