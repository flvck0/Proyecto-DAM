package com.example.eventoslocales.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


private val Context.sessionStore by preferencesDataStore(name = "session")

object SessionPrefs {
    private val KEY_LOGGED_IN = booleanPreferencesKey("logged_in")

    fun loggedInFlow(context: Context): Flow<Boolean> =
        context.sessionStore.data.map { prefs -> prefs[KEY_LOGGED_IN] ?: false }

    suspend fun setLoggedIn(context: Context, value: Boolean) {
        context.sessionStore.edit { prefs -> prefs[KEY_LOGGED_IN] = value }
    }
}
