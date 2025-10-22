package com.example.eventoslocales.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Módulo encargado de guardar y recuperar el estado de sesión del usuario.
 *
 * Decisiones clave:
 * - Se usa Jetpack DataStore en lugar de SharedPreferences, ya que es más seguro,
 *   moderno y soporta Flows reactivos.
 * - El patrón Singleton (object) asegura que el acceso a las preferencias sea único
 *   y consistente en toda la app.
 */

// Extensión del contexto para inicializar el DataStore de tipo "Preferences"
private val Context.sessionStore by preferencesDataStore(name = "session")

object SessionPrefs {

    // Clave única para guardar el estado de login (true = usuario logueado)
    private val KEY_LOGGED_IN = booleanPreferencesKey("logged_in")

    /**
     * Devuelve un Flow<Boolean> que emite el estado actual de la sesión.
     *
     * Si no hay valor guardado, por defecto retorna `false` (usuario deslogueado).
     *
     * Decisión: se usa Flow para que Compose o ViewModels puedan reaccionar
     * automáticamente cuando cambie el valor.
     */
    fun loggedInFlow(context: Context): Flow<Boolean> =
        context.sessionStore.data.map { prefs -> prefs[KEY_LOGGED_IN] ?: false }

    /**
     * Guarda el nuevo valor del estado de sesión en DataStore.
     *
     * - Este método es suspend porque `edit()` escribe de forma asíncrona.
     * - Llamado principalmente desde MainActivity cuando el usuario inicia o cierra sesión.
     */
    suspend fun setLoggedIn(context: Context, value: Boolean) {
        context.sessionStore.edit { prefs ->
            prefs[KEY_LOGGED_IN] = value
        }
    }
}
