package com.example.proyectofinal.core.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.proyectofinal.R

// Set of Material typography styles to start with
fun getTypography() = Typography(
    bodyLarge = TextStyle(
        fontFamily = AtkinsonHyperlegibleFamily,
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

const val ATKINSON_HYPERLEGIBLE_FAMILY_NAME = "Atkinson Hyperlegible"
const val OPEN_DYSLEXIC_FAMILY_NAME = "Open Dyslexic"

val AtkinsonHyperlegibleFamily = FontFamily(
    Font(R.font.atkinson_hyperlegible_regular, FontWeight.Normal),
    Font(R.font.atkinson_hyperlegible_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.atkinson_hyperlegible_bold, FontWeight.Bold)
)

val OpenDyslexicFamily = FontFamily(
    Font(R.font.open_dyslexic_regular, FontWeight.Normal),
    Font(R.font.open_dyslexic_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.open_dyslexic_bold, FontWeight.Bold),
    Font(R.font.open_dyslexic_bold_italic, FontWeight.Bold, FontStyle.Italic)
)

val AtkinsonHyperlegibleMonoFamily = FontFamily(
    Font(R.font.atkinson_hyperlegible_mono_regular, FontWeight.Normal),
    Font(R.font.atkinson_hyperlegible_mono_italic, FontWeight.Normal, FontStyle.Italic),
)