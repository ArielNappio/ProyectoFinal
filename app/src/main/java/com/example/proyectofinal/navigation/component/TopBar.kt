package com.example.proyectofinal.navigation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Drafts
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.rememberAsyncImagePainter
import com.example.proyectofinal.R
import com.example.proyectofinal.core.theme.LocalTheme
import com.example.proyectofinal.navigation.ScreensRoute
import com.example.proyectofinal.navigation.util.showBackButton

@Composable
fun TopBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 30.dp)
            .padding(horizontal = 16.dp)
            .height(56.dp),
        contentAlignment = Alignment.Center
    ) {
        WirinIcon()
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (navBackStackEntry?.showBackButton() == true) {
                BackButton(navController)
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 0.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        navController.navigate(ScreensRoute.Search.route)
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Buscar",
                            tint = if (LocalTheme.current.isDark) Color.White else Color.Black
                        )
                    }
                }

                Image(
                    painter = painterResource(
                        id = R.drawable.wirin_25
                    ),
                    contentDescription = "Logo de Wirin",
                    modifier = Modifier.size(56.dp),
                    colorFilter = if (LocalTheme.current.isDark) ColorFilter.tint(Color.White) else ColorFilter.tint(
                        Color.Black
                    )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 0.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (navController.currentDestination?.route == ScreensRoute.Mail.route) {
                        InboxMenuIcon(navController)
                    } else {
                        ChatButton(navController)
                    }
                }
            }
        }
    }
}

@Composable
fun BackButton(navController: NavController) =
    Column(
        modifier = Modifier
            .clickable { navController.popBackStack() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Volver",
            modifier = Modifier.size(36.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "Volver",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.primary
        )
    }

@Composable
fun ChatButton(navController: NavController) =
    TextButton(onClick = { navController.navigate(ScreensRoute.Mail.route) }) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Outlined.Email,
                contentDescription = "Correo",
                tint = Color(0xFF0084FF)
            )
            Text("Correo", color = Color(0xFF0084FF))
        }
    }

@Composable
fun WirinIcon() =
    Image(
        painter = rememberAsyncImagePainter(model = R.drawable.wirin_25),
        contentDescription = "Logo de Wirin",
        modifier = Modifier.size(56.dp),
        colorFilter = if (LocalTheme.current.isDark) ColorFilter.tint(Color.White) else ColorFilter.tint(
            Color.Black
        )
    )

@Composable
fun InboxMenuIcon(navController: NavController) {
    val menuExpanded = remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { menuExpanded.value = true }) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menú",
                modifier = Modifier.size(26.dp)
            )
        }

        DropdownMenu(
            expanded = menuExpanded.value,
            onDismissRequest = { menuExpanded.value = false }
        ) {
            DropdownMenuItem(
                text = { Text("Nuevo Mensaje", fontSize = 18.sp) },
                onClick = {
                    menuExpanded.value = false
                    navController.navigate(ScreensRoute.Message.route)
                },
                leadingIcon = {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "Nuevo mensaje",
                        modifier = Modifier.size(24.dp)
                    )
                }
            )
            DropdownMenuItem(
                text = { Text("Bandeja de Entrada", fontSize = 18.sp) },
                onClick = {
                    menuExpanded.value = false
                    navController.navigate("mail/inbox")
                },
                leadingIcon = {
                    Icon(
                        Icons.Filled.Inbox,
                        contentDescription = "Bandeja de Entrada",
                        modifier = Modifier.size(24.dp)
                    )
                }
            )
            DropdownMenuItem(
                text = { Text("Bandeja de Salida", fontSize = 18.sp) },
                onClick = {
                    menuExpanded.value = false
                    navController.navigate("mail/outbox")
                },
                leadingIcon = {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Bandeja de Salida",
                        modifier = Modifier.size(24.dp)
                    )
                }
            )
            DropdownMenuItem(
                text = { Text("Borradores", fontSize = 18.sp) },
                onClick = {
                    menuExpanded.value = false
                    navController.navigate("mail/drafts")
                },
                leadingIcon = {
                    Icon(
                        Icons.Filled.Drafts,
                        contentDescription = "Borradores",
                        modifier = Modifier.size(24.dp)
                    )
                }
            )
        }
    }
}
