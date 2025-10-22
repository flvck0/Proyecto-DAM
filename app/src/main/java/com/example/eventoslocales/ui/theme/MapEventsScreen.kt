package com.example.eventoslocales.ui.theme

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.eventoslocales.model.Event
import com.example.eventoslocales.ui.theme.viewmodel.EventsViewModel
import com.example.eventoslocales.ui.theme.viewmodel.UserLocation

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapEventsScreen(
    viewModel: EventsViewModel,
    onOpenDetail: (Int) -> Unit
) {
    val events by viewModel.events.collectAsStateWithLifecycle()
    val userLocation by viewModel.userLocation.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    var selectedEvent by remember { mutableStateOf<Event?>(null) }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE


    val handleEventClick: (Event) -> Unit = { event ->
        selectedEvent = event
        onOpenDetail(event.id)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (isLandscape) {
            Row(modifier = Modifier.fillMaxSize()) {
                GoogleMapComposable(
                    userLocation = userLocation,
                    events = events,
                    selectedEvent = selectedEvent,
                    onMarkerClick = handleEventClick,
                    modifier = Modifier.weight(1f)
                )
                EventsList(
                    events = events,
                    isLoading = isLoading,
                    onEventClick = { event ->
                        selectedEvent = event
                        onOpenDetail(event.id)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                Card(
                    modifier = Modifier
                        .weight(0.7f)
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    GoogleMapComposable(
                        userLocation = userLocation,
                        events = events,
                        selectedEvent = selectedEvent,
                        onMarkerClick = handleEventClick,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                EventsList(
                    events = events,
                    isLoading = isLoading,
                    onEventClick = { event ->
                        selectedEvent = event
                        onOpenDetail(event.id)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}


@Composable
fun GoogleMapComposable(
    userLocation: UserLocation,
    events: List<Event>,
    selectedEvent: Event?,
    onMarkerClick: (Event) -> Unit,
    modifier: Modifier = Modifier
) {
    val userLatLng = LatLng(userLocation.lat, userLocation.lon)

    val centerLatLng = if (events.isNotEmpty()) {
        userLatLng
    } else {
        userLatLng
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(centerLatLng, 12f)
    }

    LaunchedEffect(selectedEvent) {
        selectedEvent?.let { event ->
            val eventLatLng = LatLng(event.latitude, event.longitude)
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(eventLatLng, 15f), // Zoom más cercano
                durationMs = 800
            )
        }
    }

    val isSystemInDarkTheme = isSystemInDarkTheme()
    val mapProperties by remember {
        mutableStateOf(MapProperties(
            isMyLocationEnabled = true,

        ))
    }

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = mapProperties
    ) {
        Marker(
            state = MarkerState(position = userLatLng),
            title = "Tu Ubicación",
            snippet = "(${userLocation.lat}, ${userLocation.lon})"
        )

        events.forEach { event ->
            val eventLatLng = LatLng(event.latitude, event.longitude)

            Marker(
                state = MarkerState(position = eventLatLng),
                title = event.title,
                snippet = event.description,
                onInfoWindowClick = {
                    onMarkerClick(event)
                    true
                }
            )
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

        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            events.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No se encontraron eventos cercanos.", textAlign = TextAlign.Center)
                }
            }
            else -> {
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
            containerColor = if (event.isFeatured)
                MaterialTheme.colorScheme.tertiaryContainer
            else
                MaterialTheme.colorScheme.surface
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
                modifier = Modifier
                    .size(36.dp)
                    .padding(end = 8.dp)
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