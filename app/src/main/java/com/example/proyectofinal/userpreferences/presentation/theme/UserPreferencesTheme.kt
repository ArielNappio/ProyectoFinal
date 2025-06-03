package com.example.proyectofinal.userpreferences.presentation.theme

import androidx.compose.runtime.compositionLocalOf
import com.example.proyectofinal.userpreferences.domain.model.UserPreferences

val LocalUserPreferences = compositionLocalOf { UserPreferences(16f, "Default") }