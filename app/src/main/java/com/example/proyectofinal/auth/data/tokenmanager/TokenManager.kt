package com.example.proyectofinal.auth.data.tokenmanager


import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.proyectofinal.auth.data.model.UserResponseDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

private val Context.dataStore by preferencesDataStore("auth")

class TokenManager(private val context: Context) {

    private val TOKEN_KEY = stringPreferencesKey("jwt_token")
    private val USER_ID_KEY = stringPreferencesKey("user_id")
    private val USER_DATA_KEY = stringPreferencesKey("user_data")

    suspend fun saveUser(user: UserResponseDto?) {
        val json = Json.encodeToString(user)
        context.dataStore.edit {
            it[USER_DATA_KEY] = json
        }
    }

    val user: Flow<UserResponseDto?> = context.dataStore.data
        .map { preferences ->
            preferences[USER_DATA_KEY]?.let {
                runCatching { Json.decodeFromString<UserResponseDto>(it) }.getOrNull()
            }
        }.distinctUntilChanged()

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
            it[TOKEN_KEY] = ""
            it[USER_ID_KEY] = ""
        }
    }
}