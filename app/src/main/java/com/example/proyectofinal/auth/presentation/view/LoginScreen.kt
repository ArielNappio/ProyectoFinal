package com.example.proyectofinal.auth.presentation.view

import com.example.proyectofinal.core.presentation.components.LoadingWithImageBar
import android.graphics.BitmapFactory
import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.graphics.scale
import androidx.navigation.NavController
import com.example.proyectofinal.R
import com.example.proyectofinal.auth.presentation.viewmodel.LoginViewModel
import com.example.proyectofinal.core.util.UiState
import com.example.proyectofinal.navigation.ScreensRoute
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(
    navController: NavController
) {
    val viewmodel = koinViewModel<LoginViewModel>()
    val email by viewmodel.email.collectAsState()
    val password by viewmodel.password.collectAsState()
    val emailError by viewmodel.emailError.collectAsState()
    val passwordError by viewmodel.passwordError.collectAsState()
    val loginState by viewmodel.loginState.collectAsState()
    val navigateToMain by viewmodel.navigateToMain.collectAsState()
    val navigateToPreferences by viewmodel.navigateToPreferences.collectAsState()
    val isLoading by viewmodel.isLoading.collectAsState()

    val showErrorDialog by viewmodel.showErrorDialog.collectAsState()

    if (showErrorDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { viewmodel.dismissErrorDialog() },
            confirmButton = {
                Button(onClick = { viewmodel.dismissErrorDialog() }) {
                    Text("Aceptar")
                }
            },
            title = {
                Text("Error de inicio de sesión")
            },
            text = {
                val errorMsg = (loginState as? UiState.Error)?.message
                Text(errorMsg ?: "Ocurrió un error desconocido 😕")
            }
        )
    }

    LaunchedEffect(navigateToMain) {
        if (navigateToMain) {
            navController.navigate(ScreensRoute.Home.route) {
                popUpTo(ScreensRoute.Login.route) { inclusive = true }
            }
            viewmodel.updateNavigateToMain(false) // reset después de navegar
        }
    }

    if (navigateToPreferences) {
        LaunchedEffect(Unit) {
            navController.navigate(ScreensRoute.Preferences.route) {
                popUpTo(ScreensRoute.Login.route) { inclusive = true }
            }
        }
    }

    if (isLoading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LoadingWithImageBar(imageResId = R.drawable.wirin_logo_dark,
                loadingTexts = listOf(
                "Creando libros...",
                "Fabricando historias...",
                "Leyendo mentes...",
                "Preparando aventuras...",
                "Hablando con los personajes...",
                "Están llegando...",
                "Cultivando ideas...",
                "Criando animalitos...",
                "Rompiendo leyes..."
            ))
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedLogo(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { viewmodel.onEmailChange(it) },
                label = { Text("Email") },
                isError = emailError != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged {
                        if (!it.isFocused) viewmodel.onEmailFocusChange(false)
                    },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Email
                )
            )
            if (emailError != null) {
                Text(emailError ?: "", color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { viewmodel.onPasswordChange(it) },
                label = { Text("Contraseña") },
                isError = passwordError != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged {
                        if (!it.isFocused) viewmodel.onPasswordFocusChange(false)
                    },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Password
                )
            )
            if (passwordError != null) {
                Text(passwordError ?: "", color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewmodel.onLoginClick() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Iniciar sesión")
            }
        }
    }
}

@Composable
fun AnimatedLogo(modifier: Modifier = Modifier) {
    val imageOffsetY = remember { Animatable(-350f) }
    var animationPlayed by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!animationPlayed) {
            animationPlayed = true
            delay(500)
            imageOffsetY.animateTo(
                targetValue = 0f,
                animationSpec = tween(
                    durationMillis = 2000,
                    easing = {
                        OvershootInterpolator(2f).getInterpolation(it)
                    }
                )
            )
        }
    }

    val context = LocalContext.current
    val scaledBitmap = remember {
        val originalBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.wirin_logo_dark)
        val aspectRatio = originalBitmap.width.toFloat() / originalBitmap.height
        val targetWidth = 256
        val targetHeight = (targetWidth / aspectRatio).toInt()
        originalBitmap.scale(targetWidth, targetHeight)
    }

    Image(
        painter = BitmapPainter(scaledBitmap.asImageBitmap()),
        contentDescription = "Logo",
        modifier = modifier
            .size(256.dp)
            .offset(y = imageOffsetY.value.dp)
    )
}
