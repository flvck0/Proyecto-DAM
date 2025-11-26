package com.example.eventoslocales.ui.theme

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll // Nuevo import para scroll lateral
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState // Nuevo import para estado del scroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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

private object Dimens {
    val screenPadding = 16.dp
    val sectionGap = 16.dp
    val cardCorner = 18.dp
    val cardPadding = 16.dp
    val listSpacing = 12.dp
    val iconSize = 40.dp
    val chipGap = 8.dp
}

private fun categoryColor(category: String, colors: ColorScheme): Color =
    when (category) {
        "Música" -> colors.primary
        "Comida" -> colors.tertiary
        "Deporte" -> colors.secondary
        "Arte" -> colors.secondary.copy(alpha = 0.6f)
        "Educación" -> colors.primary.copy(alpha = 0.6f)
        else -> colors.outline
    }

@Composable
fun MapEventsScreen(
    viewModel: EventsViewModel,
    onOpenDetail: (Int) -> Unit
) {
    val events by viewModel.events.collectAsStateWithLifecycle()
    val userLocation by viewModel.userLocation.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    var selectedEvent by remember { mutableStateOf<Event?>(null) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val handleEventClick: (Event) -> Unit = { event ->
        selectedEvent = event
        onOpenDetail(event.id)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Dimens.screenPadding)
    ) {
        // CAMBIO 1: Reduje el espacio superior para ganar más pantalla (de sectionGap a 8.dp)
        Spacer(Modifier.height(8.dp))

        HeroHeader(
            selectedCategory = selectedCategory,
            onSelectCategory = { selectedCategory = it }
        )

        Spacer(Modifier.height(Dimens.sectionGap))

        if (isLandscape) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.sectionGap)
            ) {
                MapCard(
                    userLocation = userLocation,
                    events = events.filterByCategory(selectedCategory),
                    selectedEvent = selectedEvent,
                    onMarkerClick = handleEventClick,
                    modifier = Modifier.weight(1f)
                )
                EventsList(
                    events = events.filterByCategory(selectedCategory),
                    isLoading = isLoading,
                    onEventClick = handleEventClick,
                    modifier = Modifier.weight(1f)
                )
            }
        } else {
            MapCard(
                userLocation = userLocation,
                events = events.filterByCategory(selectedCategory),
                selectedEvent = selectedEvent,
                onMarkerClick = handleEventClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.52f)
            )

            Spacer(Modifier.height(Dimens.sectionGap))

            EventsList(
                events = events.filterByCategory(selectedCategory),
                isLoading = isLoading,
                onEventClick = handleEventClick,
                modifier = Modifier.weight(0.48f)
            )
        }
    }
}

// CAMBIO 2: Versión compacta del encabezado
@Composable
private fun HeroHeader(
    selectedCategory: String?,
    onSelectCategory: (String?) -> Unit
) {
    val cs = MaterialTheme.colorScheme

    Surface(
        shape = RoundedCornerShape(12.dp), // Esquinas un poco más cerradas
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        0f to cs.primary.copy(alpha = 0.15f),
                        1f to cs.surfaceVariant.copy(alpha = 0.15f)
                    )
                )
                // Padding reducido para compactar altura
                .padding(vertical = 10.dp, horizontal = 12.dp)
        ) {
            Column {
                // Título más pequeño (TitleMedium en vez de HeadlineSmall)
                Text(
                    "Explora eventos cerca de ti",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                // Se eliminó el subtítulo para ahorrar espacio

                Spacer(Modifier.height(8.dp))

                // Scroll Horizontal habilitado
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Dimens.chipGap),
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    CategoryChip("Todos", selectedCategory == null) { onSelectCategory(null) }
                    CategoryChip("Música", selectedCategory == "Música") { onSelectCategory("Música") }
                    CategoryChip("Comida", selectedCategory == "Comida") { onSelectCategory("Comida") }
                    CategoryChip("Deporte", selectedCategory == "Deporte") { onSelectCategory("Deporte") }
                    CategoryChip("Arte", selectedCategory == "Arte") { onSelectCategory("Arte") }
                    CategoryChip("Educación", selectedCategory == "Educación") { onSelectCategory("Educación") }
                }
            }
        }
    }
}

@Composable
private fun CategoryChip(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) }
    )
}

private fun List<Event>.filterByCategory(cat: String?): List<Event> =
    if (cat == null) this else this.filter { it.category == cat }

@Composable
private fun MapCard(
    userLocation: UserLocation,
    events: List<Event>,
    selectedEvent: Event?,
    onMarkerClick: (Event) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        // 1. El Mapa (Fondo)
        Card(
            shape = RoundedCornerShape(Dimens.cardCorner),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            ),
            modifier = Modifier.fillMaxSize()
        ) {
            GoogleMapComposable(
                userLocation = userLocation,
                events = events,
                selectedEvent = selectedEvent,
                onMarkerClick = onMarkerClick,
                modifier = Modifier.fillMaxSize()
            )
        }

        // 2. El Botón Flotante (Corregido)
        FloatingActionButton(
            onClick = { /* Acción para recentrar */ },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                // CAMBIO AQUÍ:
                // Usamos 'padding' diferenciado.
                // 'end = 12.dp' lo mantiene pegado a la derecha.
                // 'bottom = 80.dp' lo sube lo suficiente para no tapar el zoom (-).
                .padding(end = 12.dp, bottom = 80.dp)
        ) {
            Icon(Icons.Filled.CenterFocusStrong, contentDescription = "Centrar")
        }
    }
}

@Composable
private fun GoogleMapComposable(
    userLocation: UserLocation,
    events: List<Event>,
    selectedEvent: Event?,
    onMarkerClick: (Event) -> Unit,
    modifier: Modifier = Modifier
) {
    val userLatLng = LatLng(userLocation.lat, userLocation.lon)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userLatLng, 12f)
    }

    LaunchedEffect(selectedEvent) {
        selectedEvent?.let { e ->
            val p = LatLng(e.latitude, e.longitude)
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(p, 15f),
                durationMs = 800
            )
        }
    }

    val mapProperties by remember {
        mutableStateOf(MapProperties(isMyLocationEnabled = true))
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        properties = mapProperties
    ) {
        events.forEach { event ->
            val pos = LatLng(event.latitude, event.longitude)
            Marker(
                state = MarkerState(position = pos),
                title = event.title,
                snippet = event.description,
                onClick = {
                    onMarkerClick(event)
                    true
                }
            )
        }
    }
}

@Composable
private fun EventsList(
    events: List<Event>,
    isLoading: Boolean,
    onEventClick: (Event) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Actividades y Eventos",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(Modifier.height(8.dp))

        when {
            isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            events.isEmpty() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "No se encontraron eventos cercanos.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 72.dp),
                    verticalArrangement = Arrangement.spacedBy(Dimens.listSpacing)
                ) {
                    items(events) { event ->
                        EventTile(
                            event = event,
                            onClick = { onEventClick(event) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EventTile(
    event: Event,
    onClick: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val stripe = categoryColor(event.category, cs)
    val isDark = isSystemInDarkTheme()
    val amber = if (isDark) Color(0xFFFFD54F) else Color(0xFFFFC107)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (event.isFeatured)
                    Modifier.border(
                        BorderStroke(1.dp, amber.copy(alpha = 0.7f)),
                        shape = RoundedCornerShape(Dimens.cardCorner)
                    )
                else Modifier
            )
    ) {
        ElevatedCard(
            shape = RoundedCornerShape(Dimens.cardCorner),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = cs.surface),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.cardPadding)
            ) {
                Box(
                    modifier = Modifier
                        .width(6.dp)
                        .height(56.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(stripe)
                )

                Spacer(Modifier.width(12.dp))

                val icon: ImageVector = when (event.category) {
                    "Música" -> Icons.Default.DateRange
                    "Comida" -> Icons.Filled.Restaurant
                    "Deporte" -> Icons.Default.SportsSoccer
                    "Arte" -> Icons.Default.LocationOn
                    "Educación" -> Icons.Default.LocationOn
                    else -> Icons.Default.LocationOn
                }

                Surface(
                    modifier = Modifier
                        .size(Dimens.iconSize)
                        .clip(CircleShape),
                    color = stripe.copy(alpha = 0.12f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = event.category,
                            tint = stripe,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            event.title,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        if (event.isFeatured) {
                            Spacer(Modifier.width(8.dp))
                            FeaturedGoldChip(amber = amber)
                        }
                    }

                    Spacer(Modifier.height(4.dp))

                    Text(
                        event.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = cs.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun FeaturedGoldChip(amber: Color) {
    Surface(
        color = Color.Transparent,
        shape = RoundedCornerShape(50),
        modifier = Modifier
            .height(28.dp)
            .border(
                BorderStroke(1.dp, amber),
                shape = RoundedCornerShape(50)
            )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = amber,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = "Destacado",
                color = amber,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1
            )
        }
    }
}