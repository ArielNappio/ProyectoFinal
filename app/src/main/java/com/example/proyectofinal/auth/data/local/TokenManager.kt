package com.example.proyectofinal.auth.data.local


import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("auth")

class TokenManager(private val context: Context) {
    private val TOKEN_KEY = stringPreferencesKey("jwt_token")

    val token: Flow<String?> = context.dataStore.data.map {
        it[TOKEN_KEY]
    }

    suspend fun saveToken(token: String) {
        context.dataStore.edit {
            it[TOKEN_KEY] = token
        }
    }

    suspend fun clearToken() {
        context.dataStore.edit {
            it.remove(TOKEN_KEY)
        }
    }
}