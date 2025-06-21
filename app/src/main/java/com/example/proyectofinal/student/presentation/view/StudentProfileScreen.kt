package com.example.proyectofinal.student.presentation.view

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyectofinal.R
import com.example.proyectofinal.auth.data.tokenmanager.TokenManager
import com.example.proyectofinal.core.theme.LocalTheme
import com.example.proyectofinal.core.ui.ThemeViewModel
import com.example.proyectofinal.navigation.ScreensRoute
import com.example.proyectofinal.userpreferences.presentation.component.AppText
import com.example.proyectofinal.userpreferences.presentation.viewmodel.PreferencesViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import android.content.Intent
import androidx.compose.ui.layout.ContentScale


@Composable
fun StudentProfileScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    val themeViewModel = koinViewModel<ThemeViewModel>()
    val preferencesViewModel = koinViewModel<PreferencesViewModel>()  // Aquí lo obtenés directamente
    val tokenManager: TokenManager = koinInject()

    val user = tokenManager.user.collectAsState(initial = null).value
    val preferences by preferencesViewModel.preferences.collectAsState()
    val context = LocalContext.current

    val imageUri = remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(user?.email) {
        user?.email?.let { email ->
            preferencesViewModel.getProfileImageUri(email) { uriString ->
                uriString?.let { imageUri.value = Uri.parse(it) }
            }
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            context.contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
            imageUri.value = it
            user?.email?.let { email ->
                preferencesViewModel.updateProfileImageUri(email, it.toString())
            }
        }
    }




    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Image
            Image(
                painter = rememberAsyncImagePainter(
                    model = imageUri.value ?: R.drawable.student_placeholder_profile_photo
                ),
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.DarkGray)
                    .clickable(){
                        launcher.launch(arrayOf("image/*"))
                    },
                        contentScale = ContentScale.Crop //  Ajusta la imagen al recuardo

            )

            Spacer(modifier = Modifier.width(24.dp))
            // Placeholder for user info
            Column {
                AppText(user?.email.toString(), isTitle = true, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                AppText(user?.fullName.toString())
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppText(
                text = "Modo Oscuro",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.semantics { contentDescription = "Toggle de modo oscuro" }
            )
            Switch(
                checked = LocalTheme.current.isDark,
                onCheckedChange = { themeViewModel.toggleTheme() }
            )
        }

        Button(
            onClick = { navController.navigate(ScreensRoute.Preferences.route) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3A66FF),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            AppText(text = "Configuración de fuente")
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                themeViewModel.logout()
                navController.navigate(ScreensRoute.Login.route) {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
                      },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE53935),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            AppText("Cerrar sesión")
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
