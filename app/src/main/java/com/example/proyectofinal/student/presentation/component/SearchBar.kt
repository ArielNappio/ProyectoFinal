package com.example.proyectofinal.student.presentation.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.proyectofinal.userpreferences.presentation.component.AppText

@Composable
fun SearchBar(
    searchText: String,
    onTextChange: (String) -> Unit,
    onVoiceClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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
        shape = RoundedCornerShape(50),
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
    )
}