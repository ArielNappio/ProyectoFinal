package com.example.proyectofinal

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.proyectofinal.auth.data.tokenmanager.TokenManager
import com.example.proyectofinal.core.presentation.theme.CurrentTheme
import com.example.proyectofinal.core.presentation.theme.LocalTheme
import com.example.proyectofinal.core.presentation.theme.ProyectoFinalTheme
import com.example.proyectofinal.core.presentation.viewmodel.ThemeViewModel
import com.example.proyectofinal.navigation.presentation.view.Main
import com.example.proyectofinal.notifications.scheduleOneTimeWorker
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    private lateinit var tokenManager: TokenManager

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkNotificationPermission()
        }

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

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(this, "Notifications enabled", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkNotificationPermission() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED -> {
                Log.d("MainActivity", "Notifications are enabled")
            }
            shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
            else -> {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}