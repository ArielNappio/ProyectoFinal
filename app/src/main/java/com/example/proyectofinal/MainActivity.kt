package com.example.proyectofinal

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeViewModel: ThemeViewModel = koinViewModel() // TODO: Move scope to Main or NavigationComponent composables
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()
            val isCustomFontFamilySelected by themeViewModel.fontFamilySelected.collectAsState()

            val darkTheme = if (isDarkTheme) CurrentTheme(false, isCustomFontFamilySelected)
                else CurrentTheme(true, isCustomFontFamilySelected)

            ProyectoFinalTheme(
                darkTheme = darkTheme.isDark,
                customTypographySelected = isCustomFontFamilySelected
            ) {
                CompositionLocalProvider(LocalTheme provides darkTheme) {
                    Log.d("MainActivity", "isDarkTheme: ${LocalTheme.current.colorScheme.background}")
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
//                            .background(LocalTheme.current.colorScheme.background)
                    ) {
                        val navController = rememberNavController()
                        Main(themeViewModel, navController)
                    }
                }
            }
        }
    }
}