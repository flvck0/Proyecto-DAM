package com.example.eventoslocales.ui.theme.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventoslocales.model.Event
import com.example.eventoslocales.repository.EventsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class UserLocation(val lat: Double, val lon: Double)

class EventsViewModel(
    private val repository: EventsRepository = EventsRepository()
) : ViewModel() {

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events

    private val _userLocation = MutableStateFlow(UserLocation(-33.4402, -70.6482)) // Santiago Centro
    val userLocation: StateFlow<UserLocation> = _userLocation

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadEvents()
    }

    fun loadEvents() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val loadedEvents = repository.getNearbyEvents()
                _events.value = loadedEvents.sortedByDescending { it.isFeatured }
            } catch (e: Exception) {
                println("Error al cargar eventos :( : ${e.message}")
                _events.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getEventById(id: Int): Event? {
        return events.value.firstOrNull { it.id == id }
    }
}
