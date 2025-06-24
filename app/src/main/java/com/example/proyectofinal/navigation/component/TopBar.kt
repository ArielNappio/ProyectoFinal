package com.example.proyectofinal.navigation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyectofinal.R
import com.example.proyectofinal.core.theme.LocalTheme
import com.example.proyectofinal.navigation.ScreensRoute
import com.example.proyectofinal.userpreferences.domain.model.UserPreferences
import com.example.proyectofinal.userpreferences.domain.repository.UserPreferencesRepository
import org.koin.compose.koinInject

@Composable
fun TopBar(navController: NavController) {
    val prefsRepo = koinInject<UserPreferencesRepository>()
    val prefs by prefsRepo.getUserPreferences()
        .collectAsState(
            initial = UserPreferences(
                fontSize = 16f,
                fontFamily = "Default",
                iconSize = 24f
            )
        )

    val iconSize = prefs.iconSize.dp
    val barHeight = 64.dp
    val sideSlotWidth = (iconSize.value * 2).dp // espacio para los laterales

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (LocalTheme.current.isDark) Color.Black else Color.White)
            .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding(), start = 16.dp, end = 16.dp)
            .requiredHeight(barHeight + WindowInsets.statusBars.asPaddingValues().calculateTopPadding()),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // LADO IZQUIERDO
            Box(
                modifier = Modifier
                    .width(sideSlotWidth)
                    .fillMaxHeight(),
                contentAlignment = Alignment.CenterStart
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Buscar",
                        tint = if (LocalTheme.current.isDark) Color.White else Color.Black,
                        modifier = Modifier
                            .size(iconSize)
                            .clickable { navController.navigate(ScreensRoute.Search.route) }
                    )
                }
            }

            // CENTRO - LOGO
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .size(iconSize * 1.7f),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.wirin_25),
                    contentDescription = "Logo de Wirin",
                    colorFilter = if (LocalTheme.current.isDark) ColorFilter.tint(Color.White)
                    else ColorFilter.tint(Color.Black)
                )
            }

            // LADO DERECHO - BOTÓN DE CORREO
            Box(
                modifier = Modifier
                    .width(sideSlotWidth)
                    .fillMaxHeight(),
                contentAlignment = Alignment.CenterEnd
            ) {
                ChatButton(navController, iconSize)
            }
        }
    }
}

@Composable
fun ChatButton(navController: NavController, iconSize: Dp) {
    Column(
        modifier = Modifier
            .clickable { navController.navigate(ScreensRoute.Mail.route) },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Outlined.Email,
            contentDescription = "Correo",
            tint = if (LocalTheme.current.isDark) Color.White else
                Color.Black,
            modifier = Modifier.size(iconSize)
        )
    }
}
