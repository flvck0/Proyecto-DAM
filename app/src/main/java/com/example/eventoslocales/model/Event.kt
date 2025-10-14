package com.example.eventoslocales.model

data class Event(
    val id: Int,
    val title: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val category: String,
    val isFeatured: Boolean = false
)