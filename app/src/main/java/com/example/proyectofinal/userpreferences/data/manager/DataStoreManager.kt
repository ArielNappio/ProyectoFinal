package com.example.proyectofinal.userpreferences.data.manager


import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.proyectofinal.userpreferences.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_preferences")

class DataStoreManager(private val context: Context) {

    companion object {
        val FONT_SIZE_KEY = floatPreferencesKey("font_size")
        val FONT_FAMILY_KEY = stringPreferencesKey("font_family")
    }

    val preferencesFlow: Flow<UserPreferences> = context.dataStore.data.map { prefs ->
        UserPreferences(
            fontSize = prefs[FONT_SIZE_KEY] ?: 16f,
            fontFamily = prefs[FONT_FAMILY_KEY] ?: "Default"
        )
    }

    suspend fun saveFontSize(size: Float) {
        context.dataStore.edit { prefs ->
            prefs[FONT_SIZE_KEY] = size
        }
    }

    suspend fun saveFontFamily(family: String) {
        context.dataStore.edit { prefs ->
            prefs[FONT_FAMILY_KEY] = family
        }
    }
}
