package com.example.eventoslocales.ui.theme

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.eventoslocales.model.Event
import com.example.eventoslocales.ui.theme.viewmodel.EventsViewModel

@Composable
fun MapEventsScreen(
    viewModel: EventsViewModel,
    onLogout: () -> Unit
) {
    val events by viewModel.events.collectAsStateWithLifecycle()
    val userLocation by viewModel.userLocation.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    var selectedEvent by remember { mutableStateOf<Event?>(null) }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Column(modifier = Modifier.fillMaxSize()) {

        if (isLandscape) {
            Row(modifier = Modifier.fillMaxSize()) {
                MapSimulation(
                    userLocation = userLocation,
                    events = events,
                    selectedEvent = selectedEvent,
                    modifier = Modifier.weight(1f)
                )
                EventsList(
                    events = events,
                    isLoading = isLoading,
                    onEventClick = { selectedEvent = it },
                    modifier = Modifier.weight(1f)
                )
            }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                MapSimulation(
                    userLocation = userLocation,
                    events = events,
                    selectedEvent = selectedEvent,
                    modifier = Modifier.weight(0.7f)
                )
                EventsList(
                    events = events,
                    isLoading = isLoading,
                    onEventClick = { selectedEvent = it },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun EventsList(
    events: List<Event>,
    isLoading: Boolean,
    onEventClick: (Event) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = "Actividades y Eventos",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        if (isLoading) {
            Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (events.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                Text("No se encontraron eventos cercanos.", textAlign = TextAlign.Center)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(events) { event ->
                    EventCard(
                        event = event,
                        onEventClick = onEventClick
                    )
                }
            }
        }
    }
}


@Composable
fun MapSimulation(
    userLocation: com.example.eventoslocales.ui.theme.viewmodel.UserLocation,
    events: List<Event>,
    selectedEvent: Event?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFB3E5FC)), // Simulación de mapa
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = "Tu Ubicación",
                    tint = Color.Red,
                    modifier = Modifier.size(48.dp)
                )
                Text(
                    text = "Tu Ubicación\n(${userLocation.lat}, ${userLocation.lon})",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            selectedEvent?.let { event ->
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                        .widthIn(max = 250.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = event.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = event.category,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EventCard(event: Event, onEventClick: (Event) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEventClick(event) },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (event.isFeatured) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val icon: ImageVector = when (event.category) {
                "Música" -> Icons.Default.DateRange
                "Comida" -> Icons.Filled.Restaurant
                "Deporte" -> Icons.Default.SportsSoccer
                else -> Icons.Default.LocationOn
            }

            Icon(
                icon,
                contentDescription = event.category,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp).padding(end = 8.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (event.isFeatured) {
                    Text(
                        text = "¡DESTACADO!",
                        color = MaterialTheme.colorScheme.tertiary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}
