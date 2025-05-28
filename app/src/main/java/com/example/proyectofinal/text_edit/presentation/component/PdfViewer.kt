package com.example.proyectofinal.text_edit.presentation.component

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.proyectofinal.text_edit.domain.PdfBitmapConverter
import com.example.proyectofinal.text_edit.presentation.viewmodel.TextEditorViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun PdfViewer(
    modifier: Modifier = Modifier
) {
    val viewModel = koinViewModel<TextEditorViewModel>()
    val scale by viewModel.pdfViewScale.collectAsState()
    val offsetX by viewModel.pdfViewOffsetX.collectAsState()
    val offsetY by viewModel.pdfViewOffsetY.collectAsState()

    val zoomModifier = Modifier
        .pointerInput(Unit) {
            detectTransformGestures { _, pan, zoom, _ ->
                viewModel.updatePdfZoom(zoom, pan.x, pan.y)
            }
        }
        .graphicsLayer(
            scaleX = scale,
            scaleY = scale,
            translationX = offsetX,
            translationY = offsetY
        )

    val context = LocalContext.current
    val pdfBitmapConverter = remember {
        PdfBitmapConverter(context)
    }
    var pdfUri by remember {
        mutableStateOf<Uri?>(null)
    }
    var renderedPages by remember {
        mutableStateOf<List<Bitmap>>(emptyList())
    }

    LaunchedEffect(pdfUri) {
        pdfUri?.let { uri ->
            renderedPages = pdfBitmapConverter.pdfToBitmaps(uri)
        }
    }

    val choosePdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { pdfUri = it }

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

@Composable
fun PdfPage(
    page: Bitmap,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = page,
        contentDescription = null,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(page.width.toFloat() / page.height.toFloat())
            .drawWithContent { drawContent() }
    )
}