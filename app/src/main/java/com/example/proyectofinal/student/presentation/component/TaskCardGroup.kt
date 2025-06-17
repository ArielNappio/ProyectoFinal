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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    // Estado para saber si está expandido o no
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .fillMaxWidth()
            .clickable {
                expanded = !expanded
                Log.d("TaskGroupCard", "Click en grupo: ${taskGroup.title}, expanded: $expanded")
            },
        border = BorderStroke(1.dp, Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            AppText(
                text = taskGroup.title,
                isTitle = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = if (expanded) 8.dp else 0.dp)
            )

            if (expanded) {
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
}