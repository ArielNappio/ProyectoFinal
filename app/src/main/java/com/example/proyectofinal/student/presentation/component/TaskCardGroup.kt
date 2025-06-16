package com.example.proyectofinal.student.presentation.component

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyectofinal.orderManagment.domain.model.OrderDelivered
import com.example.proyectofinal.userpreferences.presentation.component.AppText

@Composable
fun TaskGroupCard(
    taskGroup: OrderDelivered,
    onToggleFavorite: (Int) -> Unit,
    navController: NavController
) {
    Card(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 16.dp) // Padding exterior para separar esta Card de otras
            .fillMaxWidth() // Ocupa todo el ancho disponible
            .clickable {
                Log.d("TaskGroupCard", "Click en grupo: ${taskGroup.title}")
                // Aquí podrías navegar a una pantalla de detalles del grupo o realizar otra acción
            },
        // Estilo de la Card similar a tu TaskCard
        border = BorderStroke(1.dp, Color.White), // Borde blanco como en TaskCard
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp) // Una pequeña elevación
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp) // Padding interno para el contenido de la Card
        ) {
            // Título del grupo de tareas
            AppText( // Usamos AppText para consistencia con tu TaskCard
                text = taskGroup.title,
                isTitle = true, // Asumimos que quieres el título del grupo como un título
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp) // Pequeño padding debajo del título para separar de las tareas
            )

            // Iterar y mostrar cada TaskCard dentro de este grupo
            taskGroup.orders.forEach { task ->
                TaskCard(
                    task = task,
                    onToggleFavorite = onToggleFavorite,
                    navController = navController
                )
            }
        }
    }
}