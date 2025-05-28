package com.example.proyectofinal.text_editor.presentation.component

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.proyectofinal.text_editor.presentation.viewmodel.TextEditorViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun PdfViewer(
    modifier: Modifier = Modifier
) {
    val viewModel = koinViewModel<TextEditorViewModel>()
    val scale by viewModel.pdfViewScale.collectAsState()
    val offsetX by viewModel.pdfViewOffsetX.collectAsState()
    val offsetY by viewModel.pdfViewOffsetY.collectAsState()

    val scaleAnimated by animateFloatAsState(scale)
    val offsetXAnimated by animateFloatAsState(offsetX)
    val offsetYAnimated by animateFloatAsState(offsetY)

    val pdfUri by viewModel.pdfUri.collectAsState()
    val renderedPages by viewModel.renderedPages.collectAsState()

    val zoomModifier = Modifier
        .pointerInput(Unit) {
            detectTransformGestures { _, pan, zoom, _ ->
                viewModel.updatePdfZoom(zoom, pan.x, pan.y)
            }
        }
        .graphicsLayer(
            scaleX = scaleAnimated,
            scaleY = scaleAnimated,
            translationX = offsetXAnimated,
            translationY = offsetYAnimated
        )

    val choosePdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { viewModel.setPdfUri(it) }

    if(pdfUri == null) {
        Box(
            modifier = modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Button(onClick = {
                choosePdfLauncher.launch("application/pdf")
            }) {
                Text(text = "Choose PDF")
            }
        }
    } else {
        Box(
            modifier = modifier
                .clipToBounds()
                .fillMaxSize()
        ) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .then(zoomModifier),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (renderedPages.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.width(64.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Cargando")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        items(renderedPages) { page ->
                            PdfPage(page)
                        }
                    }
                }
            }
            if (scale > 1f) {
                FloatingActionButton(
                    onClick = {
                        viewModel.resetPdfZoom()
                    },
                    modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        text = "Reset Zoom"
                    )
                }
            }
        }
    }
}
