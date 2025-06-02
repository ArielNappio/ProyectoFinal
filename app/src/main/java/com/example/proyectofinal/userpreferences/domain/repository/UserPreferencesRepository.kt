package com.example.proyectofinal.userpreferences.domain.repository

import com.example.proyectofinal.userpreferences.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    fun getUserPreferences(): Flow<UserPreferences>
    suspend fun saveFontSize(size: Float)
    suspend fun saveFontFamily(family: String)
}
