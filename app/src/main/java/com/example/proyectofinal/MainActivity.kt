package com.example.proyectofinal

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.proyectofinal.librarian.view.CreateTaskScreen
import com.example.proyectofinal.librarian.view.UploadfileScreen
import com.example.proyectofinal.navigation.NavigationComponent

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = "createOrder") {
                composable("createOrder") {
                    CreateTaskScreen(navController)
                }
                composable("uploadfile") {
                    UploadfileScreen(navController)
                }
            }
        }
    }
}
