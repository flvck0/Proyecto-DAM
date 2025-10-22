package com.example.eventoslocales.repository

import com.example.eventoslocales.model.Event
import kotlinx.coroutines.delay

/**
 * Repositorio responsable de proveer los eventos cercanos.
 *
 * Actualmente trabaja con datos mockeados (simulados),
 * pero su estructura ya está pensada para poder reemplazarse fácilmente
 * por una fuente real, como una API REST o una base de datos local.
 *
 * Decisión: mantengo la capa de datos separada de la UI y del ViewModel
 * para cumplir con el principio de separación de responsabilidades (Clean Architecture).
 */
class EventsRepository {

    // Lista mock de eventos. Sirve como base temporal mientras no hay backend real.
    // Cada evento tiene coordenadas, categoría y un flag "isFeatured" para ordenarlos en la UI.
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

    /**
     * Devuelve la lista de eventos disponibles.
     *
     * - Uso `suspend` porque en el futuro este método podría hacer una llamada real a la red.
     * - Agrego un pequeño `delay()` para simular la latencia de una petición HTTP.
     * - Actualmente retorna la lista mockeada definida arriba.
     *
     * Ventaja: el ViewModel no necesita saber si los datos vienen de una API o de una lista local,
     * solo se suscribe al resultado y la UI reacciona.
     */
    suspend fun getNearbyEvents(): List<Event> {
        delay(500) // Simulo una carga de datos o tiempo de red
        return mockEvents
    }
}
