package com.example.eventoslocales.network

import com.example.eventoslocales.model.Event
import retrofit2.http.GET

interface EventsApi {
    @GET("event")
    suspend fun getEvents(): List<Event>
}