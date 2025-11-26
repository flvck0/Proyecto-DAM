package com.example.eventoslocales.model

import com.google.gson.annotations.SerializedName

data class Event(
    val id: Int,

    // Postman dice "title", así que usamos ese
    @SerializedName("title")
    val title: String = "Sin título",

    // Postman dice "description", así que usamos ese
    @SerializedName("description")
    val description: String = "",

    // Postman dice "latitude", así que usamos ese
    @SerializedName("latitude")
    val latitude: Double = 0.0,

    // Postman dice "longitude", así que usamos ese
    @SerializedName("longitude")
    val longitude: Double = 0.0,

    // Postman dice "category", así que usamos ese
    @SerializedName("category")
    val category: String = "General",

    // Este sí coincidía, pero lo dejamos igual
    @SerializedName("is_featured")
    val isFeatured: Boolean = false
)