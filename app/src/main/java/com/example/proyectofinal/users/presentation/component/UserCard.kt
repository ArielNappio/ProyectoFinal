package com.example.proyectofinal.users.presentation.component

import android.R
import android.R.attr.onClick
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.proyectofinal.users.data.model.User


@Preview(showBackground = true)
@Composable
fun PreviewUserCard() {
    val userAriel = User(
        userName = "ArielN",
        email = "edunappio@gmail.com",
        fullName = "Ariel Nappio",
        roles = listOf("Administrativo"),
        id = "1",
        password = "a",
        phoneNumber = "1"
    )
    UserCard(
        user = userAriel,
        navController = TODO(),
        onClickEliminar = {},
        onClickModificar = {}
    )
}


@Composable
fun UserCard(
    user: User,
    navController: NavController,
    onClickEliminar: () -> Unit,
    onClickModificar: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { expanded = !expanded }
            .border(
                width = 2.dp,
                color = Color(0xFF1976D2),
                shape = RoundedCornerShape(8.dp)
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFB2CCFF)) // Fondo azul claro
    ) {
        Row(
            modifier = Modifier.padding(12.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_menu_add),
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(text = user.fullName, fontWeight = FontWeight.Bold)

                AnimatedVisibility(visible = expanded) {
                    Column {
                        Text(text = user.email)
                        Text(text = "Teléfono: ${user.phoneNumber}")
                    }
                }

                Text(text = "Rol: ${user.roles.joinToString()}")
            }

            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.End
            ) {
                IconButton(onClick = onClickModificar) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = Color(0xFF1976D2),
                        modifier = Modifier
                            .size(24.dp)
                            .padding(bottom = 8.dp)
                    )
                }
                IconButton(onClick = onClickEliminar) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = Color(0xFFFF5722),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
