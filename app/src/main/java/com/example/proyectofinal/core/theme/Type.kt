package com.example.proyectofinal.core.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.proyectofinal.R

// Set of Material typography styles to start with
fun getTypography(isCustomSelected: Boolean) = Typography(
    bodyLarge = TextStyle(
        fontFamily = if (isCustomSelected) atkinsonHyperlegibleFamily else FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)

val atkinsonHyperlegibleFamily = FontFamily(
    Font(R.font.atkinson_hyperlegible_regular, FontWeight.Normal),
    Font(R.font.atkinson_hyperlegible_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.atkinson_hyperlegible_bold, FontWeight.Bold)
)

val atkinsonHyperlegibleMonoFamily = FontFamily(
    Font(R.font.atkinson_hyperlegible_mono_regular, FontWeight.Normal),
    Font(R.font.atkinson_hyperlegible_mono_italic, FontWeight.Normal, FontStyle.Italic),
)