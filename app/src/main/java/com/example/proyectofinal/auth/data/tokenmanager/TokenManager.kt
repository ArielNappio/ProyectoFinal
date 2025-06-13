package com.example.proyectofinal.auth.data.tokenmanager


import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("auth")

class TokenManager(private val context: Context) {

    private val TOKEN_KEY = stringPreferencesKey("jwt_token")
    private val USER_ID_KEY = stringPreferencesKey("user_id")

    val token: Flow<String?> = context.dataStore.data
        .distinctUntilChanged()
        .map {
            Log.d("TokenManager", "Token leído: ${it[TOKEN_KEY]}")
            it[TOKEN_KEY]
        }

    val userId: Flow<String?> = context.dataStore.data
        .distinctUntilChanged()
        .map {
            Log.d("TokenManager", "UserID leído: ${it[USER_ID_KEY]}")
            it[USER_ID_KEY]
        }

    suspend fun saveToken(token: String) {
        context.dataStore.edit {
            it[TOKEN_KEY] = token
        }
    }

    suspend fun saveUserId(userId: String) {
        context.dataStore.edit {
            it[USER_ID_KEY] = userId
        }
    }

    suspend fun clearAuthData() {
        context.dataStore.edit {
            it.remove(TOKEN_KEY)
            it.remove(USER_ID_KEY)
        }
    }
}