package com.example.proyectofinal.student.presentation.component

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectofinal.userpreferences.presentation.component.AppText
import com.example.proyectofinal.userpreferences.presentation.viewmodel.PreferencesViewModel
import com.example.proyectofinal.userpreferences.util.getFontFamilyFromString
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchBar(
    searchText: String,
    onTextChange: (String) -> Unit,
    onVoiceClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: PreferencesViewModel = koinViewModel()
    val prefs by viewModel.preferences.collectAsState()

    val fontSize = (prefs.fontSize - 4).coerceAtLeast(12f)
    val fontFamily = getFontFamilyFromString(prefs.fontFamily)

    TextField(
        value = searchText,
        onValueChange = onTextChange,
        placeholder = {
            AppText(
                "Buscá palabras claves",
                isTitle = false,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Buscar"
            )
        },
        trailingIcon = {
            IconButton(onClick = onVoiceClick) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Buscar por voz",
                )
            }
        },
//        colors = TextFieldDefaults.colors(
//            focusedContainerColor = Color(0xFF1E1E1E),
//            unfocusedContainerColor = Color(0xFF1E1E1E),
//            disabledContainerColor = Color(0xFF1E1E1E),
//            cursorColor = Color.White,
//            focusedIndicatorColor = Color.Transparent,
//            unfocusedIndicatorColor = Color.Transparent,
//            disabledIndicatorColor = Color.Transparent,
//            focusedTextColor = Color.White,
//            unfocusedTextColor = Color.White,
//            disabledTextColor = Color.White,
//            focusedPlaceholderColor = Color.White.copy(alpha = 0.5f),
//            unfocusedPlaceholderColor = Color.White.copy(alpha = 0.5f)
//        ),
        textStyle = TextStyle(
            fontSize = fontSize.sp,
            fontFamily = fontFamily,
            lineHeight = (fontSize * 1.2).sp
        ),
        shape = RoundedCornerShape(50),
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 56.dp)
    )
}