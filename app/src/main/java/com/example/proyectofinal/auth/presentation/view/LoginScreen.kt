package com.example.proyectofinal.auth.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.proyectofinal.auth.presentation.viewmodel.LoginViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import com.example.proyectofinal.core.util.UiState
import com.example.proyectofinal.navigation.ScreensRoute

@Composable
fun LoginScreen(
    navController: NavController
) {
    val viewmodel = koinViewModel<LoginViewModel>()
    val email by viewmodel.email.collectAsState()
    val password by viewmodel.password.collectAsState()
    val loginState by viewmodel.loginState.collectAsState()
    val navigateToMain by viewmodel.navigateToMain.collectAsState()
    val isLoading by viewmodel.isLoading.collectAsState()

    LaunchedEffect(navigateToMain) {
        if (navigateToMain) {
            navController.navigate(ScreensRoute.Home.route) {
                popUpTo(ScreensRoute.Login.route) { inclusive = true }
            }
        }
    }

    LaunchedEffect(loginState) {
        if (loginState is UiState.Success) {
            navController.navigate(ScreensRoute.Home.route) {
                popUpTo(ScreensRoute.Login.route) { inclusive = true }
            }
        }
    }

    if(isLoading){
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Cargando...")
            CircularProgressIndicator()
        }
    }

    else {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "BiblioAccess6", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { viewmodel.onEmailChange(it) },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Email
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { viewmodel.onPasswordChange(it) },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Password
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewmodel.onLoginClick() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Iniciar sesión")
            }

            Spacer(modifier = Modifier.height(16.dp))

        }
    }

}
