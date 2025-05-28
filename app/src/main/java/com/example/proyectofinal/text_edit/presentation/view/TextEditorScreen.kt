package com.example.proyectofinal.text_edit.presentation.view

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.proyectofinal.text_edit.presentation.component.PdfViewer
import com.example.proyectofinal.text_edit.presentation.component.TextEditor

@Composable
fun TextEditorScreen(navController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier
                .padding(top = 30.dp, bottom = 16.dp)
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                modifier = Modifier.clickable { navController.popBackStack() },
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
            )
            Text("Titulo del apunte")
            Spacer(modifier = Modifier.width(30.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Chat,
                contentDescription = "Agregar comentario",
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Enviar a revisión",
            )
        }
        Row(
            modifier = Modifier
                .weight(1f)
                .border(1.dp, Color.Blue)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextEditor()
        }
        Row(
            modifier = Modifier
                .weight(1f)
                .border(1.dp, Color.Blue)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PdfViewer()
        }
    }
}