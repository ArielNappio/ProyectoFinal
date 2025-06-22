package com.example.proyectofinal.userpreferences.domain.repository

import com.example.proyectofinal.userpreferences.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    fun getUserPreferences(): Flow<UserPreferences>
    suspend fun saveFontSize(size: Float)
    suspend fun saveFontFamily(family: String)
    suspend fun saveIconSize(size: Float)
    suspend fun saveProfileImageUri(email: String, uri: String)
    suspend fun getProfileImageUri(email: String): String?
}
