package com.example.proyectofinal.userpreferences.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectofinal.core.theme.BlueDark
import com.example.proyectofinal.core.theme.BlueGray
import com.example.proyectofinal.core.theme.CustomBlue
import com.example.proyectofinal.core.theme.CustomGreen
import com.example.proyectofinal.userpreferences.presentation.viewmodel.PreferencesViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun FontPreferencesScreen(onDone: () -> Unit) {
    val viewModel: PreferencesViewModel = koinViewModel()
    val prefs by viewModel.preferences.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Configuración de fuente",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        HorizontalDivider(thickness = 1.dp, color = Color.DarkGray)

        Text(
            text = "Tamaño de fuente",
            color = Color.White,
            fontSize = prefs.fontSize.sp,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Slider(
                value = prefs.fontSize,
                onValueChange = { viewModel.updateFontSize(it) },
                valueRange = 12f..40f,
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

        Text(
            text = "Fuente actual",
            color = Color.White,
            fontSize = prefs.fontSize.sp,
            fontWeight = FontWeight.SemiBold
        )

        val fontOptions = listOf("Default", "Sans", "Serif", "Monospace")

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            fontOptions.forEach { font ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (prefs.fontFamily == font) CustomBlue else BlueGray
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = "Botón fuente $font" },
                    onClick = { viewModel.updateFontFamily(font) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = font,
                            color = Color.White,
                            fontSize = prefs.fontSize.sp
                        )
                        if (prefs.fontFamily == font) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Fuente seleccionada",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = onDone,
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "Botón continuar" },
            colors = ButtonDefaults.buttonColors(containerColor = CustomGreen)
        ) {
            Text(
                text = "Continuar",
                fontSize = prefs.fontSize.sp,
                color = Color.White
            )
        }
    }
}

