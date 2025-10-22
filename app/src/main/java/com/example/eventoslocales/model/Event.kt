package com.example.eventoslocales.model

/**
 * Modelo de datos base para representar un evento local.
 *
 * Este modelo contiene la información necesaria para mostrar
 * los eventos tanto en el mapa como en la vista de detalle.
 *
 * Decisión: uso un data class porque quiero inmutabilidad,
 * copia simple (copy()) y comparación estructural automática,
 * lo cual funciona excelente con Jetpack Compose y Flow.
 */
data class Event(
    val id: Int,               // Identificador único del evento
    val title: String,         // Título o nombre del evento
    val description: String,   // Descripción breve que se muestra en detalle
    val latitude: Double,      // Latitud del evento (para posicionarlo en el mapa)
    val longitude: Double,     // Longitud del evento (para posicionarlo en el mapa)
    val category: String,      // Categoría del evento (ej. música, deportes, arte)
    val isFeatured: Boolean = false // Marca si el evento es destacado o no (usado en el ordenamiento)
)
