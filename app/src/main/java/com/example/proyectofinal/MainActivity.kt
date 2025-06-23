package com.example.proyectofinal

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.proyectofinal.auth.data.tokenmanager.TokenManager
import com.example.proyectofinal.core.theme.CurrentTheme
import com.example.proyectofinal.core.theme.LocalTheme
import com.example.proyectofinal.core.theme.ProyectoFinalTheme
import com.example.proyectofinal.core.ui.ThemeViewModel
import com.example.proyectofinal.navigation.presentation.view.Main
import com.example.proyectofinal.notifications.scheduleOneTimeWorker
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    private lateinit var tokenManager: TokenManager

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tokenManager = TokenManager(this)

        lifecycleScope.launch {
            tokenManager.userId.collect { userId ->
                userId?.let {
                    scheduleOneTimeWorker(this@MainActivity, it)
                }
            }
        }

        enableEdgeToEdge()

        setContent {
            val themeViewModel: ThemeViewModel = koinViewModel()
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()

            val currentTheme = CurrentTheme(isDarkTheme)

            ProyectoFinalTheme(
                darkTheme = isDarkTheme
            ) {
                CompositionLocalProvider(LocalTheme provides currentTheme) {
                    Surface(
                        modifier = Modifier
                            .fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        val navController = rememberNavController()
                        Main(Modifier.fillMaxSize(), navController)
                    }
                }
            }
        }
    }
}