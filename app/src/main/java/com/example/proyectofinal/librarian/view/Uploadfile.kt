package com.example.proyectofinal.librarian.view


import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun UploadfileScreen() {
    val selectedFileUri = remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) {
        selectedFileUri.value = it
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
             Box(
                modifier = Modifier
                    .height(360.dp)
                    .fillMaxWidth()
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (selectedFileUri.value == null)
                        "Aún no se ha cargado ningún archivo"
                    else
                        "Archivo seleccionado:\n${selectedFileUri.value?.lastPathSegment}",
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

             Button(
                onClick = {
                    launcher.launch(
                        arrayOf(
                            "application/pdf",
                            "application/msword",
                            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                        )
                    )
                },
                modifier = Modifier.width(350.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1976D2)
                )
            ) {
                Text("Seleccionar archivo", color = Color.White)
            }

            Spacer(modifier = Modifier.height(12.dp))

             Text(
                 modifier = Modifier.width(350.dp),
                 text="Solo se permiten archivos PDF\ny Word (DOC, DOCX)",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Black

            )
        }

         Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(end = 16.dp, bottom = 80.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            FloatingActionButton(
                onClick = { /* acción cámara */ },
                containerColor = Color(0xFF1976D2),
                contentColor = Color.White
            ) {
                Icon(imageVector = Icons.Filled.CameraAlt, contentDescription = "Abrir cámara")
            }
        }
    }
}
