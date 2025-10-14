package com.example.eventoslocales.repository
import com.example.eventoslocales.model.Event
import kotlinx.coroutines.delay

class EventsRepository {
    private val mockEvents = listOf(
        Event(
            id = 1,
            title = "Noche de Cumbia Chilena",
            description = "Tributo a grandes de la cumbia en el Teatro Caupolicán. ¡Destacado!",
            latitude = -33.4503,
            longitude = -70.6558,
            category = "Música",
            isFeatured = true
        ),
        Event(
            id = 2,
            title = "Feria de Diseño y Artesanía",
            description = "Exposición de artistas locales en la Plaza Mulato Gil.",
            latitude = -33.4384,
            longitude = -70.6385,
            category = "Arte"
        ),
        Event(
            id = 3,
            title = "Clase de Running en el San Cristóbal",
            description = "Entrenamiento grupal subiendo el cerro. Punto de encuentro en Pío Nono.",
            latitude = -33.4285,
            longitude = -70.6278,
            category = "Deporte"
        ),
        Event(
            id = 4,
            title = "Festival de Food Trucks",
            description = "Sabores internacionales, cervezas artesanales y música en vivo en el Parque O'Higgins.",
            latitude = -33.4633,
            longitude = -70.6698,
            category = "Comida"
        ),
        Event(
            id = 5,
            title = "Taller de Fotografía Nocturna",
            description = "Aprende a usar la cámara para capturar la noche en la Plaza Ñuñoa.",
            latitude = -33.4474,
            longitude = -70.5976,
            category = "Educación",
            isFeatured = true
        )
    )


    suspend fun getNearbyEvents(): List<Event> {
        delay(500)
        return mockEvents
    }
}