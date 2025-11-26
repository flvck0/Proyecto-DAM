package com.example.eventoslocales.repository

import android.util.Log
import com.example.eventoslocales.model.Event
import com.example.eventoslocales.network.RetrofitClient

class EventsRepository {

    suspend fun getNearbyEvents(): List<Event> {
        return try {
            Log.d("API_XANO", "Intentando conectar a: ${RetrofitClient.BASE_URL}")

            val response = RetrofitClient.api.getEvents()

            Log.d("API_XANO", "¡Éxito! Se encontraron ${response.size} eventos")
            // Filtramos eventos con coordenadas 0.0 para evitar errores en el mapa
            response.filter { it.latitude != 0.0 && it.longitude != 0.0 }

        } catch (e: Exception) {
            // AQUÍ ESTÁ LA CLAVE: Imprimimos el error exacto
            Log.e("API_XANO_ERROR", "Error grave cargando datos: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }
}