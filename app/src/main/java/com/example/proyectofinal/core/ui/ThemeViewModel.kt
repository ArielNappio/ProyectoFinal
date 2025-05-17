package com.example.proyectofinal.core.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ThemeViewModel : ViewModel() {
    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme

    private val _fontFamilySelected = MutableStateFlow(false) // TODO: Change to enum with different font families
    val fontFamilySelected: StateFlow<Boolean> = _fontFamilySelected

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    fun setFontFamilySelected(isSelected: Boolean) {
        _fontFamilySelected.value = isSelected
    }
}