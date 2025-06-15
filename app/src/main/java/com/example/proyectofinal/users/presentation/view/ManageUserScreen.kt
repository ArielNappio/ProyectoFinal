package com.example.proyectofinal.users.presentation.view

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Divider
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.ktor.websocket.Frame.Text
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import com.example.proyectofinal.users.data.model.User
import com.example.proyectofinal.users.presentation.component.UserCard
import androidx.compose.material.icons.Icons
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.example.proyectofinal.navigation.ScreensRoute
import com.example.proyectofinal.users.presentation.viewmodel.UserViewModel
import org.koin.androidx.compose.koinViewModel


@Composable
fun ManageUserScreen(navController: NavController) {
    val viewModel: UserViewModel = koinViewModel()
    val usuarios by viewModel.users.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchUsers()
    }

    ManageUserContent(
        usuarios = usuarios,
        navController = navController,
        onClickEliminar = { user ->
            Log.d("ManageUserScreen", "Eliminar usuario: ${user.id}")
            viewModel.deleteUser(id = user.id)
        },
        onClickModificar = { user ->
            viewModel.selectUser(user)
            navController.navigate("update_user/${user.id}")
        }
    )
}

@Composable
fun ManageUserContent(
    usuarios: List<User>,
    navController: NavController,
    onClickEliminar: (User) -> Unit,
    onClickModificar: (User) -> Unit
) {
    var selectedItem by remember { mutableStateOf(1) }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    // Acción para agregar voluntario
                },
                icon = {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Agregar voluntario",
                        modifier = Modifier
                            .width(40.dp)
                            .height(30.dp)
                    )
                },
                text = {
                    Text("Agregar voluntario")
                },
                containerColor = Color(0xFF0D6EFD),
                contentColor = Color.White,
                shape = RoundedCornerShape(50)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.Black) {
                NavigationBarItem(
                    selected = selectedItem == 0,
                    onClick = { selectedItem = 0 },
                    icon = { Icon(Icons.Default.Folder, contentDescription = "Tareas") },
                    label = { Text("Tareas") },
                    colors = navBarItemColors(selectedItem == 0)
                )
                NavigationBarItem(
                    selected = selectedItem == 1,
                    onClick = { selectedItem = 1 },
                    icon = { Icon(Icons.Default.Group, contentDescription = "Usuarios") },
                    label = { Text("Usuarios") },
                    colors = navBarItemColors(selectedItem == 1)
                )
                NavigationBarItem(
                    selected = selectedItem == 2,
                    onClick = { selectedItem = 2 },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
                    label = { Text("Perfil") },
                    colors = navBarItemColors(selectedItem == 2)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Lista de voluntarios",
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                modifier = Modifier.padding(8.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = Color.LightGray, thickness = 1.dp)

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(usuarios) { user ->
                    UserCard(
                        user = user,
                        navController = navController,
                        onClickEliminar = { onClickEliminar(user) },
                        onClickModificar = { onClickModificar(user) }
                    )
                }
            }
        }
    }
}

@Composable
fun navBarItemColors(isSelected: Boolean) = NavigationBarItemDefaults.colors(
    selectedIconColor = Color.White,
    selectedTextColor = Color.White,
    indicatorColor = Color(0xFF003366),
    unselectedIconColor = Color.Gray,
    unselectedTextColor = Color.Gray
)
