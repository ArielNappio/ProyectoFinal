package com.example.proyectofinal.student.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.proyectofinal.orderManagement.domain.model.OrderDelivered
import com.example.proyectofinal.userpreferences.presentation.component.AppText


@Composable
fun ProjectCard(
    project: OrderDelivered,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    iconSize: Dp
) {
    val formattedTitle = project.title.replace(Regex(""" (\S+)$"""), "\u00A0$1")
    println("ProjectCard: ${project.isFavorite}")
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .heightIn(min = 100.dp)
        ) {
            // Título + favorito
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppText(
                    text = formattedTitle,
                    isTitle = true,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tareas con ícono
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = "Cantidad de tareas",
                    modifier = Modifier.size(iconSize),
                    tint = Color(0xFFFFC107)
                )
                Spacer(modifier = Modifier.width(6.dp))
                AppText(
                    text = "${project.orders.size}",
                    fontWeight = FontWeight.Medium,
                )

                Spacer(modifier = Modifier.weight(1f))


                IconButton(onClick = { onToggleFavorite() }) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        modifier = Modifier.size(iconSize),
                        contentDescription = "Favorito de ${project.title}",
                        tint = if (project.isFavorite) Color(0xFFFFC107) else Color.Gray
                    )
                }
            }
        }
    }
}
