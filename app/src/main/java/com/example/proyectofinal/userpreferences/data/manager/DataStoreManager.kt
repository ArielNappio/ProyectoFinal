package com.example.proyectofinal.userpreferences.data.manager


import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.proyectofinal.core.theme.ATKINSON_HYPERLEGIBLE_FAMILY_NAME
import com.example.proyectofinal.userpreferences.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_preferences")

class DataStoreManager(private val context: Context) {

    companion object {
        val FONT_SIZE_KEY = floatPreferencesKey("font_size")
        val FONT_FAMILY_KEY = stringPreferencesKey("font_family")
        val PROFILE_IMAGE_URI_KEY = stringPreferencesKey("profile_image_uri")
        fun profileImageKey(email: String) = stringPreferencesKey("profile_image_uri_$email")
        val ICON_SIZE_KEY = floatPreferencesKey("icon_size")
    }

    val preferencesFlow: Flow<UserPreferences> = context.dataStore.data.map { prefs ->
        UserPreferences(
            fontSize = prefs[FONT_SIZE_KEY] ?: 26f,
            fontFamily = prefs[FONT_FAMILY_KEY] ?: ATKINSON_HYPERLEGIBLE_FAMILY_NAME,
            profileImageUri = prefs[PROFILE_IMAGE_URI_KEY],
            iconSize = prefs[ICON_SIZE_KEY] ?: 32f
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

    suspend fun saveIconSize(size: Float) {
        context.dataStore.edit { prefs ->
            prefs[ICON_SIZE_KEY] = size
        }
    }

    suspend fun saveProfileImageUri(email: String, uri: String) {
        context.dataStore.edit { prefs ->
            prefs[profileImageKey(email)] = uri
        }
    }

    suspend fun getProfileImageUri(email: String): String? {
        return context.dataStore.data.first()[profileImageKey(email)]
    }

}
