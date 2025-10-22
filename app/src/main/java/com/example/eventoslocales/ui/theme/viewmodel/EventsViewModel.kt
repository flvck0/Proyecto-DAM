package com.example.eventoslocales.ui.theme.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventoslocales.model.Event
import com.example.eventoslocales.repository.EventsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Clase simple para representar la ubicación actual del usuario.
// Mantengo esto separado del modelo de dominio para no mezclar datos del sistema con los de negocio.
data class UserLocation(val lat: Double, val lon: Double)

/**
 * ViewModel principal encargado de manejar la lógica de eventos cercanos al usuario.
 *
 * Responsabilidades:
 * - Mantener el estado de la lista de eventos.
 * - Controlar la ubicación actual del usuario (inicialmente Santiago Centro).
 * - Exponer un flag de carga para mostrar indicadores de progreso.
 * - Comunicar resultados y errores a la UI mediante StateFlows reactivos.
 *
 * Decisión: uso coroutines + StateFlow porque Compose reacciona automáticamente a los cambios de estado.
 */
class EventsViewModel(
    // Inyección directa (por defecto creo el repo interno, pero idealmente vendría de un DI)
    private val repository: EventsRepository = EventsRepository()
) : ViewModel() {

    // Lista de eventos visibles actualmente en el mapa/pantalla.
    // MutableStateFlow para actualizar desde corrutinas, y StateFlow para exponer solo lectura a la UI.
    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events

    // Ubicación del usuario (por ahora fija, podría actualizarse cuando tenga permisos reales de GPS).
    // La inicial corresponde a Santiago Centro para pruebas.
    private val _userLocation = MutableStateFlow(UserLocation(-33.4402, -70.6482))
    val userLocation: StateFlow<UserLocation> = _userLocation

    // Flag para mostrar un spinner o animación de carga en la interfaz.
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Bloque init: apenas se crea el ViewModel, lanzo la carga de eventos inicial.
    // Esto da una experiencia inmediata al usuario sin tener que presionar nada.
    init {
        loadEvents()
    }

    /**
     * Carga la lista de eventos desde el repositorio.
     *
     * Decisiones clave:
     * - Marco _isLoading en true antes de iniciar y lo reseteo en finally.
     * - Uso un try/catch para capturar cualquier error de red o parsing.
     * - Ordeno los eventos priorizando los destacados (isFeatured = true).
     * - Si algo falla, retorno lista vacía para mantener la app estable.
     */
    fun loadEvents() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Repositorio devuelve eventos (puede venir de API o fuente local)
                val loadedEvents = repository.getNearbyEvents()
                // Ordeno para que los eventos destacados aparezcan primero.
                _events.value = loadedEvents.sortedByDescending { it.isFeatured }
            } catch (e: Exception) {
                // Registro el error en consola, pero no detengo la app.
                println("Error al cargar eventos :( : ${e.message}")
                _events.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Busca un evento específico por su ID.
     *
     * Esto lo uso, por ejemplo, en la pantalla de detalle.
     * Ventaja: no hago otra llamada al backend, reutilizo la lista ya cargada.
     */
    fun getEventById(id: Int): Event? {
        return events.value.firstOrNull { it.id == id }
    }
}
