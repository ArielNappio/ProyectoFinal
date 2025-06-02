package com.example.proyectofinal

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.proyectofinal.core.theme.CurrentTheme
import com.example.proyectofinal.core.theme.LocalTheme
import com.example.proyectofinal.core.theme.ProyectoFinalTheme
import com.example.proyectofinal.core.ui.ThemeViewModel
import com.example.proyectofinal.navigation.presentation.view.Main
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeViewModel: ThemeViewModel = koinViewModel()
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()
            val isCustomFontFamilySelected by themeViewModel.fontFamilySelected.collectAsState()

            val currentTheme = CurrentTheme(isDarkTheme, isCustomFontFamilySelected)

            ProyectoFinalTheme(
                darkTheme = isDarkTheme,
                customTypographySelected = isCustomFontFamilySelected
            ) {
                CompositionLocalProvider(LocalTheme provides currentTheme) {
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        val navController = rememberNavController()
                        Main(Modifier.fillMaxSize(), navController)
                    }
                }
            }
        }
    }
}