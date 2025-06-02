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

    private val _preferences = MutableStateFlow(UserPreferences(16f, "Default"))
    val preferences: StateFlow<UserPreferences> = _preferences.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getUserPreferences().collect { _preferences.value = it }
        }
    }

    fun updateFontSize(size: Float) {
        viewModelScope.launch {
            repository.saveFontSize(size)
        }
    }

    fun updateFontFamily(family: String) {
        viewModelScope.launch {
            repository.saveFontFamily(family)
        }
    }
}
