package com.example.proyectofinal.task_student.presentation.view


import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FontDownload
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicNone
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.TextDecrease
import androidx.compose.material.icons.filled.TextIncrease
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.proyectofinal.audio.speechrecognizer.SpeechRecognizerManager
import com.example.proyectofinal.core.theme.ATKINSON_HYPERLEGIBLE_FAMILY_NAME
import com.example.proyectofinal.core.theme.LocalTheme
import com.example.proyectofinal.core.theme.OPEN_DYSLEXIC_FAMILY_NAME
import com.example.proyectofinal.core.ui.ThemeViewModel
import com.example.proyectofinal.student.presentation.component.CommentAudioCard
import com.example.proyectofinal.task_student.presentation.component.AccessibleIconButton
import com.example.proyectofinal.task_student.presentation.component.DownloadOption
import com.example.proyectofinal.task_student.presentation.component.MicControl
import com.example.proyectofinal.task_student.presentation.component.MicPermissionWrapper
import com.example.proyectofinal.task_student.presentation.viewmodel.TaskStudentViewModel
import com.example.proyectofinal.task_student.util.htmlToAnnotatedStringFormatted
import com.example.proyectofinal.userpreferences.util.getFontFamilyFromString
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import org.koin.androidx.compose.koinViewModel
import kotlin.math.abs

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TaskStudent(taskId: Int, navController: NavHostController) {

    val themeViewModel = koinViewModel<ThemeViewModel>()
    val viewModel = koinViewModel<TaskStudentViewModel>()

    val context = LocalContext.current

    val speechRecognizerManager = remember {
        SpeechRecognizerManager(
            context = context,
            onResult = { result ->
                viewModel.setFeedbackText(result)
            },
            onError = { error ->
                Log.e("SpeechRecognizer", error)
            }
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            speechRecognizerManager.stopListening()
        }
    }


    val text by viewModel.textoPorPagina.collectAsState()
    val paragraphs by viewModel.paragraphs.collectAsState()
    val isSpeaking by viewModel.isSpeaking.collectAsState()
    val isDownloading by viewModel.isDownloadInProgress.collectAsState()
    val currentPage by viewModel.currentPageIndex.collectAsState()
    val totalPages by viewModel.totalPages.collectAsState()

    val showDownloadDialog by viewModel.showDownloadDialog.collectAsState()
    val showFeedback by viewModel.showFeedback.collectAsState()
    val showFont by viewModel.showFont.collectAsState()
    val showAnnotations by viewModel.showAnnotations.collectAsState()
    val showFontsMenu by viewModel.showFontsMenu.collectAsState()
    val showEditDialog by viewModel.showEditDialog.collectAsState()
    val editingName by viewModel.editingName.collectAsState()

    val selectedFontFamily by viewModel.selectedFontFamily.collectAsState()
    val fontSize by viewModel.fontSize.collectAsState()

    val currentPageIndex by viewModel.currentPageIndex.collectAsState()
    val isFirstPage by viewModel.isFirstPage.collectAsState()
    val isLastPage by viewModel.isLastPage.collectAsState()

    val annotatedText: AnnotatedString = remember(paragraphs, currentPageIndex) {
        val currentPage = currentPageIndex + 1
        val currentParagraphs = paragraphs.filter { it.pageNumber == currentPage }
        val fullText = currentParagraphs.joinToString("\n\n") { it.paragraphText }
        htmlToAnnotatedStringFormatted(fullText)
    }

    val comments = viewModel.comments.collectAsState().value
    val filteredComments = comments.filter { it.page == currentPageIndex }
    val currentlyPlayingPath by viewModel.currentlyPlayingPath.collectAsState()
    val currentPosition by viewModel.currentPosition.collectAsState()

    var isRecording by remember { mutableStateOf(false) }
    var hasRecording by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }

    val showSpeechSettingsMenu by viewModel.showSpeechSettingsMenu.collectAsState()
    val speechRate by viewModel.speechRate.collectAsState()
    val pitch by viewModel.pitch.collectAsState()


    val currentContext = LocalContext.current

    val fontFamily = remember(selectedFontFamily) {
        getFontFamilyFromString(selectedFontFamily ?: ATKINSON_HYPERLEGIBLE_FAMILY_NAME)
    }

    val feedbackState by viewModel.feedbackState.collectAsState()
    val rating by viewModel.rating.collectAsState()
    val feedbackText by viewModel.feedbackText.collectAsState()
    val isRecordingFeedback by viewModel.isRecording.collectAsState()
    val recordedFilePath by viewModel.recordedFeedbackFilePath.collectAsState()

    LaunchedEffect(taskId) {
        viewModel.loadProject(taskId)
        viewModel.saveTaskId(taskId)
        viewModel.loadCommentsByTaskId(taskId)
        viewModel.loadFeedback(taskId)
    }

//    LaunchedEffect(paragraphs) {
//        println("🔥 Se actualizaron los párrafos:")
//        paragraphs.forEach {
//            println("📄 Página: ${it.pageNumber}, Texto: ${it.paragraphText}")
//        }
//    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.saveLastPage(taskId, currentPage + 1)
            println("DEBUG: currentTaskId de la screen: $taskId")
            println("DEBUG: feedbackState: $feedbackState")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // ----- HEADER FIJO -----

        val infiniteTransition = rememberInfiniteTransition(label = "colorTransition")
        val animatedColor by infiniteTransition.animateColor(
            initialValue = Color.Red,
            targetValue = Color.White,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 500), // velocidad del parpadeo
                repeatMode = RepeatMode.Reverse
            ), label = "colorAnim"
        )

        val iconTint = if (isSpeaking) animatedColor else MaterialTheme.colorScheme.onSurface

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 30.dp)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AccessibleIconButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    label = "Volver",
                    onClick = {
                        navController.popBackStack()
                        viewModel.saveLastPage(taskId, currentPage + 1)
                    }
                )
                AccessibleIconButton(
                    icon = Icons.Default.FileDownload,
                    label = "Descargar",
                    onClick = { viewModel.showDownloadDialog() }
                )
                AccessibleIconButton(
                    icon = Icons.Default.Create,
                    label = "Anotaciones",
                    onClick = {
                        viewModel.showAnnotations()
                    }
                )
                AccessibleIconButton(
                    icon = Icons.Default.FontDownload,
                    label = "Fuente",
                    onClick = {
                        viewModel.showFont()
                    }
                )
                AccessibleIconButton(
                    icon = if (isSpeaking) Icons.Default.Stop else Icons.AutoMirrored.Filled.VolumeUp,
                    label = if (isSpeaking) "Pausar" else "Escuchar",
                    onClick = {
                        if (!isSpeaking) {
                            viewModel.startSpeech()
                            viewModel.toggleSpeechSettingsMenu()
                        } else {
                            viewModel.stopSpeech()
                        }
                    },
                    tint = iconTint
                )
            }
        }
        // Contenido scrolleable
        Box(modifier = Modifier.weight(1f)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Text(
                    text = annotatedText,
                    fontSize = fontSize,
                    fontFamily = fontFamily,
                    lineHeight = fontSize * 1.5f
                )
            }
        }
        // ----- FOOTER FIJO -----

        val iconButtonSize = 56.dp

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!isFirstPage) {
                IconButton(
                    onClick = { viewModel.previousPage() },
                    enabled = currentPageIndex > 0,
                    modifier = Modifier
                        .size(iconButtonSize)
                        .background(
                            Color(0xFFFFA500), shape = RoundedCornerShape(32.dp)
                        )
                        .padding(8.dp)
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Anterior",
                        tint = Color.White
                    )
                }
            } else {
                Spacer(
                    modifier = Modifier
                        .size(iconButtonSize)
                )
            }
            Text(
                text = "${currentPage + 1} / $totalPages",
                color = Color.Black,
                fontSize = 24.sp,
                modifier = Modifier
                    .background(Color.White, shape = RoundedCornerShape(16.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                textAlign = TextAlign.Center
            )

            IconButton(
                onClick = {
                    if (isLastPage) {
                        if (feedbackState == false) {
                            viewModel.showFeedback()
                        }
                    } else {
                        viewModel.nextPage()
                    }
                },
                modifier = Modifier
                    .size(iconButtonSize)
                    .background(Color(0xFFFFA500), shape = RoundedCornerShape(32.dp))
                    .padding(8.dp)
            ) {
                Icon(
                    if (isLastPage) {
                        if (feedbackState == false) {
                            Icons.Default.Feedback
                        } else {
                            Icons.Default.Android
                        }
                    } else {
                        Icons.Default.ArrowForward
                    },
                    contentDescription = "Siguiente", tint = Color.White
                )
            }
        }
    }

    // dialog show speech

    if (showSpeechSettingsMenu) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable { viewModel.closeSpeechSettingsMenu() }
        ) {
            Card(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .width(280.dp)
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Velocidad de lectura",
                        style = MaterialTheme.typography.titleMedium
                    )

                    val displayText = when {
                        speechRate < 0.75f -> "x0.5"
                        speechRate < 1.25f -> "Normal"
                        speechRate < 1.75f -> "x1.5"
                        else -> "x2"
                    }

                    Text(
                        text = displayText,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Slider(
                        value = speechRate,
                        onValueChange = { viewModel.setSpeechRate(it) },
                        onValueChangeFinished = {
                            val closest = listOf(0.5f, 1.0f, 1.5f, 2.0f).minByOrNull { abs(it - speechRate) } ?: 1.0f
                            viewModel.setSpeechRate(closest)
                        },
                        valueRange = 0.5f..2.0f,
                        steps = 2,
                        modifier = Modifier
                            .fillMaxWidth(),
                    )

                    Button(
                        onClick = {
                            if (isSpeaking) viewModel.pauseSpeech()
                            else viewModel.playSpeech()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = if (isSpeaking) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isSpeaking) "Pausar lectura" else "Iniciar lectura"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isSpeaking) "Pausar" else "Leer")
                    }

                    OutlinedButton(
                        onClick = { viewModel.closeSpeechSettingsMenu() },
                        modifier = Modifier.align(Alignment.End),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Cerrar")
                    }
                }
            }
        }
    }

    // dialog anotaciones

    if (showAnnotations) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    viewModel.showAnnotations()
                },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.85f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, Color.Gray, RoundedCornerShape(16.dp))
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {} // evita cierre si se toca adentro
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    // Título fijo
                    Text(
                        text = "Tus anotaciones",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Anotaciones scrolleables
                    Box(
                        modifier = Modifier
                            .weight(1f) // ocupa todo el espacio disponible verticalmente
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Column {
                            if (filteredComments.isEmpty()) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "No hay anotaciones para esta página",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        text = "😢",
                                        fontSize = 48.sp
                                    )
                                }
                            } else {
                                filteredComments.forEach { comment ->
                                    CommentAudioCard(
                                        comment = comment,
                                        isPlaying = isPlaying && comment.filePath == currentlyPlayingPath,
                                        currentPosition = if (currentlyPlayingPath == comment.filePath) currentPosition else 0L,
                                        onPlayClick = {
                                            viewModel.playAudio(comment.filePath)
                                        },
                                        onPauseClick = { viewModel.playAudio(comment.filePath) },
                                        onSeek = { position, playAfterSeek, path ->
                                            viewModel.seekTo(position, playAfterSeek, path)
                                        },
                                        onDeleteClick = {
                                            viewModel.deleteComment(comment.filePath)
                                        },
                                        onEditClick = { path, title ->
                                            viewModel.openEditDialog(path, title)
                                        }
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Sección fija: grabación y botón cerrar
                    MicPermissionWrapper(
                        content = {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                MicControl(
                                    isRecording = isRecording,
                                    onStartRecording = {
                                        isRecording = true
                                        viewModel.startRecording()
                                    },
                                    onStopRecording = {
                                        isRecording = false
                                        hasRecording = true
                                        viewModel.stopRecording()
                                    }
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                Button(
                                    onClick = { viewModel.showAnnotations() },
                                    modifier = Modifier.align(Alignment.End),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Text("Cerrar", color = MaterialTheme.colorScheme.onPrimary)
                                }
                            }
                        },
                        onPermissionDenied = {}
                    )
                }
            }
        }
    }

    // dialog fuente

    if (showFont) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp)
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(onClick = { viewModel.showFont() })
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .align(Alignment.Center)
                    .padding(16.dp), // Padding interno
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Primera fila con botones
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(88.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AccessibleIconButton(
                        icon = Icons.Default.FontDownload,
                        label = "Fuente",
                        onClick = { viewModel.toggleFontMenu() },
                        iconSize = 56.dp
                    )
                    AccessibleIconButton(
                        icon = Icons.Default.TextIncrease,
                        label = "Aumentar",
                        onClick = { viewModel.fontSizeIncrease() },
                        iconSize = 56.dp
                    )
                    AccessibleIconButton(
                        icon = Icons.Default.TextDecrease,
                        label = "Disminuir",
                        onClick = { viewModel.fontSizeDecrease() },
                        iconSize = 56.dp
                    )
                }

                // Switch de tema
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Tema oscuro")
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = LocalTheme.current.isDark,
                        onCheckedChange = { themeViewModel.toggleTheme() }
                    )
                }
            }
        }
    }

    //Dropdownmenu de la fuente

    if (showFontsMenu) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp, top = 16.dp) // Separación de bordes
                .wrapContentSize(Alignment.TopEnd)
        ) {
            AccessibleIconButton(
                icon = Icons.Default.FontDownload,
                label = "Fuente",
                onClick = { viewModel.toggleFontMenu() },
                iconSize = 56.dp
            )

            DropdownMenu(
                expanded = showFontsMenu,
                onDismissRequest = { viewModel.closeFontMenu() },
                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
            ) {
                val fonts = listOf(
                    "Sans",
                    "Serif",
                    "Monospace",
                    ATKINSON_HYPERLEGIBLE_FAMILY_NAME,
                    OPEN_DYSLEXIC_FAMILY_NAME
                )
                fonts.forEach { fontName ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = fontName,
                                fontWeight = if (fontName == selectedFontFamily) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        onClick = {
                            viewModel.setFontFamily(fontName)
                            viewModel.closeFontMenu()
                        }
                    )
                }
            }
        }
    }

    // DIALOG DE DESCARGAS

    if (showDownloadDialog) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)) // Fondo difuminado
                .clickable(onClick = { viewModel.showDownloadDialog() }) // Toca afuera para cerrar
        ) {
            AnimatedVisibility(
                visible = showDownloadDialog,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut(),
                modifier = Modifier.align(Alignment.Center)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .background(Color(0xFF1C1C1E), RoundedCornerShape(16.dp))
                        .border(1.dp, Color(0xFF4F4F52), RoundedCornerShape(16.dp))
                        .padding(24.dp)
                ) {
                    Text(
                        text = "Descargar como:",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    DownloadOption(".MP3", Icons.AutoMirrored.Filled.VolumeUp) {
                        viewModel.downloadTextAsMp3File(currentContext)
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    DownloadOption(".PDF", Icons.AutoMirrored.Filled.MenuBook) {
                        viewModel.downloadTextAsPdfFile(currentContext)
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    DownloadOption("Texto Plano", Icons.Default.Description) {
                        viewModel.downloadTextAsTxtFile(currentContext)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { viewModel.showDownloadDialog() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B6EF6))
                    ) {
                        Text(
                            text = "Cancelar",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }

    // Dialog de feedback

    if (showFeedback) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(onClick = { viewModel.showFeedback() })
        ) {
            AnimatedVisibility(
                visible = showFeedback,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut(),
                modifier = Modifier.align(Alignment.Center)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFFB6C7D1), RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.background)
                        .padding(24.dp)
                ) {
                    Text(
                        text = "¿Qué tal te pareció el apunte?",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        repeat(5) { index ->
                            IconButton(onClick = {
                                viewModel.setRating(index + 1)
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Estrella ${index + 1}",
                                    tint = if (index < rating) Color(0xFFFF6D00) else Color.LightGray,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Mandanos tu opinión!",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    recordedFilePath?.let { path ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {

                                    IconButton(
                                        onClick = {
                                            if (currentlyPlayingPath == path && isPlaying) {
                                                viewModel.stopAudio()
                                            } else {
                                                viewModel.playAudio(path)
                                            }
                                        },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            imageVector = if (currentlyPlayingPath == path) Icons.Default.Pause else Icons.Default.PlayArrow,
                                            contentDescription = "Play/Pause"
                                        )
                                    }

                                    Text(
                                        text = "Opinión grabada",
                                        modifier = Modifier
                                            .padding(start = 8.dp)
                                            .weight(1f)
                                    )

                                    IconButton(
                                        onClick = {
                                            viewModel.deleteFeedbackRecording()
                                        },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Eliminar grabación"
                                        )
                                    }

                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    var isListening by remember { mutableStateOf(false) }
                    val focusRequester = remember { FocusRequester() }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        BasicTextField(
                            value = feedbackText,
                            onValueChange = { viewModel.setFeedbackText(it) },
                            modifier = Modifier
                                .weight(1f)
                                .focusRequester(focusRequester),
                            textStyle = TextStyle(
                                color = MaterialTheme.colorScheme.onBackground
                            ),
                            decorationBox = { innerTextField ->
                                Box(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .defaultMinSize(minHeight = 48.dp)
                                ) {
                                    if (feedbackText.isBlank()) {
                                        Text(
                                            text = "Escribí tu opinión acá...",
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                    }
                                    innerTextField()
                                }
                            },
                            maxLines = 5,
                            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = {
                                // opcional: enviar texto al apretar Enter
                            })
                        )

                        Spacer(modifier = Modifier.width(8.dp))


                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(0xff2e7d32))
                                .clickable {
                                    if (isListening) {
                                        speechRecognizerManager.stopListening()
                                    } else {
                                        speechRecognizerManager.startListening()
                                    }
                                    isListening = !isListening
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isListening) Icons.Default.Mic else Icons.Default.MicNone,
                                contentDescription = null,
                                tint = Color.White
                            )

                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            viewModel.sendFeedbackToApi(
                                orderId = taskId,
                                feedbackText = feedbackText,
                                stars = rating
                            )
                            viewModel.showFeedback()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xff2e7d32))
                    ) {
                        Text(text = "Enviar opinión", color = Color.White)
                    }
                }
            }
        }
    }

    // dialog edit audio

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.closeEditDialog() },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.confirmEditName() },
                    enabled = editingName.isNotBlank()
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.closeEditDialog() }) {
                    Text("Cancelar")
                }
            },
            title = { Text("Editar nombre") },
            text = {
                OutlinedTextField(
                    value = editingName,
                    onValueChange = { viewModel.updateEditingName(it) },
                    singleLine = true,
                    label = { Text("Nuevo nombre") }
                )
            }
        )
    }


    // Circular progress for download in progress
    if (isDownloading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.width(64.dp)
                )
                Spacer(modifier = Modifier.height(32.dp))
                Text(text = "Descargando documento...")
            }
        }
    }
}