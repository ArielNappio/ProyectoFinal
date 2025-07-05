package com.example.proyectofinal.userpreferences.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectofinal.core.presentation.theme.ATKINSON_HYPERLEGIBLE_FAMILY_NAME
import com.example.proyectofinal.core.presentation.theme.AtkinsonHyperlegibleFamily
import com.example.proyectofinal.core.presentation.theme.BlueDark
import com.example.proyectofinal.core.presentation.theme.BlueGray
import com.example.proyectofinal.core.presentation.theme.CustomBlue
import com.example.proyectofinal.core.presentation.theme.CustomGreen
import com.example.proyectofinal.core.presentation.theme.OPEN_DYSLEXIC_FAMILY_NAME
import com.example.proyectofinal.core.presentation.theme.OpenDyslexicFamily
import com.example.proyectofinal.userpreferences.presentation.viewmodel.PreferencesViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun FontPreferencesScreen(onDone: () -> Unit) {
    val viewModel: PreferencesViewModel = koinViewModel()
    val prefs by viewModel.preferences.collectAsState()

    val fontOptions = mapOf<String, FontFamily>(
        ATKINSON_HYPERLEGIBLE_FAMILY_NAME to AtkinsonHyperlegibleFamily,
        OPEN_DYSLEXIC_FAMILY_NAME to OpenDyslexicFamily,
        "Default Serif" to FontFamily.Serif,
        "Monospace" to FontFamily.Monospace
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(top = 50.dp, bottom = 36.dp)
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Configuración de fuente",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        HorizontalDivider(thickness = 1.dp, color = Color.DarkGray)

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Text(
                    text = "Tamaño:",
                    color = Color.White,
                    fontSize = prefs.fontSize.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Slider(
                        value = prefs.fontSize,
                        onValueChange = { viewModel.updateFontSize(it) },
                        valueRange = 26f..50f,
                        steps = 5,
                        modifier = Modifier
                            .weight(1f)
                            .semantics {
                                contentDescription = "Selector de tamaño de fuente"
                            },
                        colors = SliderDefaults.colors(
                            thumbColor = BlueDark,
                            activeTrackColor = CustomBlue,
                            inactiveTrackColor = Color.DarkGray,
                            activeTickColor = Color.White,
                            inactiveTickColor = BlueGray
                        )
                    )
                    Text(
                        text = "${prefs.fontSize.toInt()} sp",
                        color = Color.White,
                        fontSize = 18.sp
                    )
                }

            }
            item {
                Text(
                    text = "Tipo:",
                    color = Color.White,
                    fontSize = prefs.fontSize.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    fontOptions.map { font ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (prefs.fontFamily == font.key) CustomBlue else BlueGray
                            ),
                            modifier = Modifier
                                .wrapContentSize()
                                .fillMaxWidth()
                                .semantics { contentDescription = "Botón fuente $font" },
                            onClick = { viewModel.updateFontFamily(font.key) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    modifier = Modifier.weight(1f).padding(end = 16.dp),
                                    text = font.key,
                                    fontFamily = font.value,
                                    color = Color.White,
                                    fontSize = prefs.fontSize.sp,
                                    lineHeight = (prefs.fontSize * 1.2).sp
                                )
                                if (prefs.fontFamily == font.key) {
                                    Icon(
                                        modifier = Modifier.size(prefs.iconSize.dp),
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Fuente seleccionada",
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = onDone,
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "Botón continuar" },
            colors = ButtonDefaults.buttonColors(containerColor = CustomGreen)
        ) {
            Text(
                text = "Aceptar",
                fontSize = prefs.fontSize.sp,
                color = Color.White
            )
        }
    }
}
