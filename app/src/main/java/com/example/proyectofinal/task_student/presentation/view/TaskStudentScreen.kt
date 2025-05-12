package com.example.proyectofinal.task_student.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.proyectofinal.task_student.presentation.component.AccessibleIconButton

@Composable
fun TaskStudent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // ----- HEADER FIJO -----
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = { /* volver */ }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color(0xFFFFA500))
                Text("Volver", color = Color(0xFFFFA500))
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AccessibleIconButton(
                    icon = Icons.Default.FileDownload,
                    label = "Descargar",
                    onClick = { /* Descargar */ }
                )
                AccessibleIconButton(
                    icon = Icons.Default.Add,
                    label = "Aumentar",
                    onClick = { /* Aumentar */ }
                )
                AccessibleIconButton(
                    icon = Icons.Default.Remove,
                    label = "Disminuir",
                    onClick = { /* Disminuir */ }
                )
            }
        }

        // ----- CUERPO CON BOTÓN FLOTANTE -----
        Box(modifier = Modifier.weight(1f)) {
            // Botón "Escuchar" flotante
            IconButton(
                onClick = { /* Escuchar */ },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 24.dp, end = 16.dp) // debajo del header
                    .background(Color(0xFFFFA500), shape = RoundedCornerShape(50))
                    .size(48.dp)
                    .zIndex(1f)
            ) {
                Icon(Icons.Default.VolumeUp, contentDescription = "Escuchar", tint = Color.White)
            }

            // Contenido scrolleable
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Las ecuaciones cuadráticas son una herramienta fundamental en Matemática utilizadas para resolver problemas en áreas como la física la economía y la ingeniería. Su estructura estándar es de la forma ax² + bx + c = 0 donde a b y c son coeficientes que determinan el comportamiento de la función cuadrática.\n\n" +
                            "Este documento está diseñado para reforzar la comprensión y aplicación de los diferentes métodos de resolución de ecuaciones cuadráticas proporcionando ejercicios de dificultad progresiva que ayudarán a desarrollar habilidades analíticas y de razonamiento matemático:\n\n" +
                            "• Conceptos básicos\n" +
                            "• Identificación de coeficientes en ecuaciones cuadráticas\n" +
                            "• Interpretación gráfica de la función cuadrática y su representación como parábola\n" +
                            "• Relación entre las raíces de la ecuación y los puntos de intersección con el eje x\n" +
                            "• Métodos de resolución\n" +
                            "• Factorización: descomposición de expresiones cuadráticas en factores más simples para encontrar soluciones",
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }

        // ----- FOOTER FIJO -----
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { /* página anterior */ },
                modifier = Modifier
                    .background(Color(0xFFFFA500), shape = RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Anterior", tint = Color.White)
            }

            Text(
                text = "1",
                color = Color.Black,
                fontSize = 24.sp,
                modifier = Modifier
                    .background(Color.White, shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                textAlign = TextAlign.Center
            )

            IconButton(
                onClick = { /* página siguiente */ },
                modifier = Modifier
                    .background(Color(0xFFFFA500), shape = RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Siguiente", tint = Color.White)
            }
        }
    }
}
