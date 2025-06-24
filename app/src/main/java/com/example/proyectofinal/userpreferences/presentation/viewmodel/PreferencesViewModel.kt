package com.example.proyectofinal.userpreferences.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.userpreferences.domain.model.UserPreferences
import com.example.proyectofinal.userpreferences.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PreferencesViewModel(
    private val repository: UserPreferencesRepository
) : ViewModel() {

    private val _preferences =
        MutableStateFlow(UserPreferences(fontSize = 26f, fontFamily = "Default", iconSize = 32f))
    val preferences: StateFlow<UserPreferences> = _preferences.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getUserPreferences().collect { _preferences.value = it }
        }
    }

    fun updateFontSize(size: Float) {
        viewModelScope.launch {
            repository.saveFontSize(size)
            when (size) {
                26f -> repository.saveIconSize(32f)
                30f -> repository.saveIconSize(36f)
                34f -> repository.saveIconSize(40f)
                38f -> repository.saveIconSize(48f)
                42f -> repository.saveIconSize(52f)
                46f -> repository.saveIconSize(56f)
                else -> repository.saveIconSize(80f)
            }
        }
    }

    fun updateFontFamily(family: String) {
        viewModelScope.launch {
            repository.saveFontFamily(family)
        }
    }

    fun updateProfileImageUri(email: String, uri: String) {
        viewModelScope.launch {
            repository.saveProfileImageUri(email, uri)
        }
    }

    fun getProfileImageUri(email: String, onResult: (String?) -> Unit) {
        viewModelScope.launch {
            val uri = repository.getProfileImageUri(email)
            onResult(uri)
        }
    }

}
