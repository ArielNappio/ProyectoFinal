package com.example.proyectofinal.text_editor.presentation.view

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.proyectofinal.text_editor.presentation.component.PdfViewer
import com.example.proyectofinal.text_editor.presentation.component.TextEditor
import com.example.proyectofinal.text_editor.presentation.viewmodel.TextEditorViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun TextEditorScreen(navController: NavHostController, id: Int) {
    val viewModel = koinViewModel<TextEditorViewModel>()

    val topWeight by viewModel.topViewWeight.collectAsState()
    val bottomWeight by viewModel.bottomViewWeight.collectAsState()

    val context = LocalContext.current

    LaunchedEffect(id) {
        Log.d("TextEditorScreen", "Loading task with ID: $id")
        viewModel.getProcessedTask(context, id)
    }

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
                .weight(topWeight)
                .border(1.dp, Color.Blue)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextEditor()
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .background(Color.Gray)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        val newTopWeight = (topWeight + dragAmount.y / 1000f).coerceIn(0.1f, 0.9f)
                        viewModel.updateViewsWeights(newTopWeight, 1f - newTopWeight)
                    }
                }
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(30.dp)
                    .background(Color.LightGray, shape = androidx.compose.foundation.shape.CircleShape)
                    .clickable {
                        viewModel.updateViewsWeights()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text("⇅", color = Color.Black)
            }
        }

        Row(
            modifier = Modifier
                .weight(bottomWeight)
                .border(1.dp, Color.Blue)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PdfViewer()
        }
    }
}