package com.example.proyectofinal.userpreferences.presentation.component

import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import org.koin.androidx.compose.koinViewModel
import com.example.proyectofinal.userpreferences.presentation.viewmodel.PreferencesViewModel

@Composable
fun AppText(
    text: String,
    modifier: Modifier = Modifier,
    isTitle: Boolean = false,
    color: Color = Color.White,
    fontWeight: FontWeight = FontWeight.Normal,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip
) {
    val viewModel: PreferencesViewModel = koinViewModel()
    val prefs by viewModel.preferences.collectAsState()

    val fontSize = if (isTitle) prefs.fontSize else (prefs.fontSize - 4).coerceAtLeast(12f)

    val fontFamily = getFontFamilyFromString(prefs.fontFamily)

    Text(
        text = text,
        fontSize = fontSize.sp,
        fontFamily = fontFamily,
        color = color,
        fontWeight = fontWeight,
        modifier = modifier,
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = overflow
    )
}
