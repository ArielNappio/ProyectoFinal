package com.example.proyectofinal.core.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.auth.data.tokenmanager.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ThemeViewModel(
    val tokenManager: TokenManager
) : ViewModel() {
    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    fun logout() {
        viewModelScope.launch {
            tokenManager.clearToken()
            println("MainViewModel: Logout called, token cleared, isLoggedIn set to false") // Add this log
        }
    }
}