package com.example.proyectofinal.navigation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.proyectofinal.R
import com.example.proyectofinal.core.theme.LocalTheme
import com.example.proyectofinal.navigation.ScreensRoute
import com.example.proyectofinal.navigation.util.showBackButton

@Composable
fun TopBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 30.dp)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (navBackStackEntry?.showBackButton() == true) {
            BackTextButton(navController)
            Image(
                painter = painterResource(
                    id = if (LocalTheme.current.isDark) R.drawable.wirin_logo_dark else R.drawable.wirin_logo_light
                ),
                contentDescription = "Logo de Wirin",
                modifier = Modifier.size(56.dp)
            )
        } else {
            Image(
                painter = painterResource(
                    id = if (LocalTheme.current.isDark) R.drawable.wirin_logo_dark else R.drawable.wirin_logo_light
                ),
                contentDescription = "Logo de Wirin",
                modifier = Modifier.size(56.dp)
            )
            Text(
                text = "¡Bienvenido!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.semantics { contentDescription = "Bienvenido" }
            )
            ChatButton(onClick = {
                navController.navigate(ScreensRoute.Mail.route)
            })
        }
    }
}

@Composable
fun BackTextButton(navController: NavController) {
    TextButton(onClick = { navController.popBackStack() }) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color(0xFFFFA500))
        Text("Volver", color = Color(0xFFFFA500))
    }
}

@Composable
fun ChatButton(onClick: () -> Unit) {
    TextButton(onClick = onClick) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Icon(
                Icons.Outlined.Email,
                contentDescription = "Correo",
                tint = Color(0xFF0084FF)
            )
            Text("Correo", color = Color(0xFF0084FF))
        }
    }
}