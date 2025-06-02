package com.example.proyectofinal.userpreferences.data.reporsitory

import com.example.proyectofinal.userpreferences.data.manager.DataStoreManager
import com.example.proyectofinal.userpreferences.domain.repository.UserPreferencesRepository

class UserPreferencesRepoImpl (
    private val dataStoreManager: DataStoreManager
) : UserPreferencesRepository {
    override fun getUserPreferences() = dataStoreManager.preferencesFlow
    override suspend fun saveFontSize(size: Float) = dataStoreManager.saveFontSize(size)
    override suspend fun saveFontFamily(family: String) = dataStoreManager.saveFontFamily(family)
}