package com.example.proyectofinal.core.ui

import androidx.lifecycle.ViewModel
import com.example.proyectofinal.auth.data.tokenmanager.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ThemeViewModel(
    val tokenManager: TokenManager
) : ViewModel() {
    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }
}