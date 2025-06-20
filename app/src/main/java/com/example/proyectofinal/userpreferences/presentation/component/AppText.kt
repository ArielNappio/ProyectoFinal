package com.example.proyectofinal.userpreferences.presentation.component

import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.example.proyectofinal.userpreferences.presentation.viewmodel.PreferencesViewModel
import com.example.proyectofinal.userpreferences.util.getFontFamilyFromString
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppText(
    text: String,
    modifier: Modifier = Modifier,
    isTitle: Boolean = false,
    color: Color = colorScheme.onBackground,
    fontWeight: FontWeight = FontWeight.Normal,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    lineHeight: Float? = null
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
        overflow = overflow,
        lineHeight = lineHeight?.sp ?: (fontSize * 1.2).sp
    )
}
