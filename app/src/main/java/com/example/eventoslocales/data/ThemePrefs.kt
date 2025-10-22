package com.example.eventoslocales.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Módulo que administra la preferencia de tema (claro u oscuro) del usuario.
 *
 * Decisiones clave:
 * - Usa Jetpack DataStore para guardar el estado del tema de forma persistente.
 * - Devuelve Flows reactivos, permitiendo que la UI se actualice automáticamente
 *   cuando el usuario cambie entre modo claro y oscuro.
 * - Se maneja como un objeto Singleton (`object`) para asegurar acceso único
 *   y simplificar el uso en toda la aplicación.
 */

// Inicializo un DataStore de tipo Preferences llamado "settings"
private val Context.dataStore by preferencesDataStore(name = "settings")

object ThemePrefs {

    // Clave que identifica la preferencia de tema oscuro (true = activado)
    private val KEY_DARK = booleanPreferencesKey("dark_theme")

    /**
     * Devuelve un Flow<Boolean> con el estado actual del tema.
     *
     * Si el usuario nunca cambió el tema, retorna false (tema claro por defecto).
     *
     * Decisión: este flujo se usa en MainActivity para que Compose reactive
     * el color scheme automáticamente cuando el usuario cambia la preferencia.
     */
    fun darkThemeFlow(context: Context): Flow<Boolean> =
        context.dataStore.data.map { prefs -> prefs[KEY_DARK] ?: false }

    /**
     * Guarda la preferencia del tema en DataStore.
     *
     * Este método es `suspend` porque `edit()` realiza la operación de escritura
     * de forma asíncrona, sin bloquear el hilo principal.
     *
     * Se llama normalmente desde SettingsScreen, al activar o desactivar el switch de tema oscuro.
     */
    suspend fun setDarkTheme(context: Context, value: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_DARK] = value
        }
    }
}
