package com.example.proyectofinal.userpreferences.util

import androidx.compose.ui.text.font.FontFamily
import com.example.proyectofinal.core.presentation.theme.ATKINSON_HYPERLEGIBLE_FAMILY_NAME
import com.example.proyectofinal.core.presentation.theme.AtkinsonHyperlegibleFamily
import com.example.proyectofinal.core.presentation.theme.OPEN_DYSLEXIC_FAMILY_NAME
import com.example.proyectofinal.core.presentation.theme.OpenDyslexicFamily

fun getFontFamilyFromString(fontFamily: String): FontFamily {
    return when (fontFamily) {
        ATKINSON_HYPERLEGIBLE_FAMILY_NAME -> AtkinsonHyperlegibleFamily
        OPEN_DYSLEXIC_FAMILY_NAME -> OpenDyslexicFamily
        "Sans" -> FontFamily.SansSerif
        "Serif" -> FontFamily.Serif
        "Monospace" -> FontFamily.Monospace
        else -> FontFamily.Default
    }
}