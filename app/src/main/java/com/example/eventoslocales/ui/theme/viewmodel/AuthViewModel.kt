package com.example.eventoslocales.ui.theme.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel encargado de manejar la lógica de autenticación del usuario.
 *
 * Responsabilidades:
 * - Mantener el estado del email ingresado.
 * - Validar el formato del correo.
 * - Emitir mensajes de error si el login no es válido.
 * - Notificar a la UI cuando la autenticación es exitosa.
 *
 * Decisión: uso StateFlow para mantener la reactividad entre la UI (Compose)
 * y el ViewModel sin necesidad de LiveData, ya que Compose reacciona de forma natural a Flows.
 */
class AuthViewModel : ViewModel() {

    // Estado interno del correo electrónico.
    // MutableStateFlow permite actualizarlo, mientras que la UI solo accede a su versión inmutable.
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    // Estado del mensaje de error que se muestra debajo del campo de login si algo falla.
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    /**
     * Se ejecuta cada vez que el usuario cambia el texto del correo electrónico.
     * Actualiza el flujo y limpia errores previos para que la interfaz se refresque en tiempo real.
     */
    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
        _errorMessage.value = null // limpio error anterior si el usuario sigue escribiendo
    }

    /**
     * Valida el correo ingresado y, si es correcto, dispara la acción de éxito.
     *
     * Decisión: al ser una validación simple, la hago localmente sin backend.
     * En un escenario real, acá se llamaría a una API o repositorio de autenticación.
     */
    fun login(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _errorMessage.value = null

            // Validación básica de email: evita cadenas vacías o sin formato válido.
            if (_email.value.isBlank() || !_email.value.contains("@") || _email.value.length < 5) {
                _errorMessage.value = "No es un email válido amigx."
            } else {
                // Si pasa la validación, notifico a la UI para que navegue al mapa.
                onSuccess()
            }
        }
    }
}
