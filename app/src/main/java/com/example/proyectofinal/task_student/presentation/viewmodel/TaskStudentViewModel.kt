package com.example.proyectofinal.task_student.presentation.viewmodel


import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.audio.domain.model.RecordedAudio
import com.example.proyectofinal.audio.domain.repository.AudioRepository
import com.example.proyectofinal.audio.player.AudioPlayerManager
import com.example.proyectofinal.audio.recorder.AudioRecorderManager
import com.example.proyectofinal.task_student.domain.usecase.DownloadAsMp3UseCase
import com.example.proyectofinal.task_student.domain.usecase.DownloadAsPdfUseCase
import com.example.proyectofinal.task_student.domain.model.DownloadType
import com.example.proyectofinal.task_student.domain.usecase.DownloadAsTxtUseCase
import com.example.proyectofinal.auth.data.tokenmanager.TokenManager
import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.orderManagement.domain.model.OrderDelivered
import com.example.proyectofinal.orderManagement.domain.model.OrderParagraph
import com.example.proyectofinal.orderManagement.domain.provider.OrderManagementProvider
import com.example.proyectofinal.orderManagement.domain.usecase.GetTaskGroupByStudentUseCase
import com.example.proyectofinal.task_student.presentation.tts.TextToSpeechManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class TaskStudentViewModel(
    private val ttsManager: TextToSpeechManager,
    private val audioRecorderManager: AudioRecorderManager,
    private val audioPlayerManager: AudioPlayerManager,
    private val audioRepositoryImpl: AudioRepository,
    private val downloadAsPdfUseCase: DownloadAsPdfUseCase,
    private val downloadAsMp3UseCase: DownloadAsMp3UseCase,
    private val downloadAsTxtUseCase: DownloadAsTxtUseCase,
    private val getOrders: GetTaskGroupByStudentUseCase,
    private val tokenManager: TokenManager
): ViewModel() {

    private val _projectState = MutableStateFlow<NetworkResponse<OrderDelivered>>(NetworkResponse.Loading())
    val projectState: StateFlow<NetworkResponse<OrderDelivered>> = _projectState.asStateFlow()

    private val _comments = MutableStateFlow<List<RecordedAudio>>(emptyList())
    val comments: StateFlow<List<RecordedAudio>> = _comments

    private val _currentlyPlayingPath = MutableStateFlow<String>("")
    val currentlyPlayingPath : StateFlow<String> = _currentlyPlayingPath

    private val _recordedFeedbackFilePath = MutableStateFlow<String?>(null)
    val recordedFeedbackFilePath: StateFlow<String?> = _recordedFeedbackFilePath.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _isDownloadInProgress = MutableStateFlow(false)
    val isDownloadInProgress: StateFlow<Boolean> = _isDownloadInProgress

    private var currentAudioFile: File? = null

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition

    private val _paragraphs = MutableStateFlow<List<OrderParagraph>>(emptyList())
    val paragraphs: StateFlow<List<OrderParagraph>> = _paragraphs

    private val _orderManagment = MutableStateFlow<NetworkResponse<OrderManagementProvider?>>(NetworkResponse.Loading())
    val orderManagment = _orderManagment.asStateFlow()

    private val _pages = MutableStateFlow<List<String>>(emptyList())
    val pages = _pages.asStateFlow()

    private val _currentPageIndex = MutableStateFlow(0)
    val currentPageIndex = _currentPageIndex.asStateFlow()

    val texto = currentPageIndex
        .combine(_pages) { index, pages ->
            pages.getOrNull(index) ?: ""
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )


    private val _isFirstPage = MutableStateFlow(true)
    val isFirstPage = _isFirstPage.asStateFlow()

    private val _isLastPage = MutableStateFlow(false)
    val isLastPage = _isLastPage.asStateFlow()

    // Estado de reproducción
    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking = _isSpeaking.asStateFlow()

    private val _isPaused = MutableStateFlow(false)
    val isPaused = _isPaused.asStateFlow()

    private val _isStopped = MutableStateFlow(true)
    val isStopped = _isStopped.asStateFlow()

    // Estados del Dialog

    private val _showExtraButton = MutableStateFlow(false)
    val showExtraButton = _showExtraButton.asStateFlow()

    private val _showDownloadDialog = MutableStateFlow(false)
    val showDownloadDialog = _showDownloadDialog.asStateFlow()

    private val _showFeedback = MutableStateFlow(false)
    val showFeedback = _showFeedback.asStateFlow()

    private val _showFont = MutableStateFlow(false)
    val showFont = _showFont.asStateFlow()

    private val _showAnnotations = MutableStateFlow(false)
    val showAnnotations = _showAnnotations.asStateFlow()

    // Estado del fontsize

    private val _fontSize = MutableStateFlow(MIN_FONT_SIZE)
    val fontSize = _fontSize.asStateFlow()

    private val _totalPages = _paragraphs.map { paragraphs ->
        paragraphs.map { it.pageNumber }
            .distinct()
            .count()
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val totalPages: StateFlow<Int> = _totalPages

    val textoPorPagina = combine(currentPageIndex, _paragraphs) { index, paragraphs ->
        paragraphs
            .filter { it.pageNumber == index + 1 }
            .joinToString(separator = "\n\n") { it.paragraphText }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ""
    )

    init {
        viewModelScope.launch {
            _comments.value = audioRepositoryImpl.getAllAudios()
            ttsManager.isStoped.collect { stopped ->
                _isStopped.value = stopped
                if(_isStopped.value == true){
                    _showExtraButton.value = false
                    _isSpeaking.value = false
                    ttsManager.resetStoppedFlag()
                    if(_currentPageIndex.value < totalPages.value){
                        nextPage()
                        speakCurrentPage()
                    }
                }
            }
        }
    }

    // audio

    private var positionJob: Job? = null

    fun playAudio(filePath: String){
        if (audioPlayerManager.isPlaying(filePath)) {
            stopAudio()
        } else {
            audioPlayerManager.play(filePath = filePath) {
                _currentlyPlayingPath.value = ""
                _currentPosition.value = 0L
                positionJob?.cancel()
            }
            _currentlyPlayingPath.value = filePath

            positionJob?.cancel()
            positionJob = viewModelScope.launch {
                while (_currentlyPlayingPath.value == filePath) {
                    _currentPosition.value = audioPlayerManager.getCurrentPosition()
                    delay(200L)
                }
            }
        }
    }

    fun stopAudio(){
        audioPlayerManager.stop()
        _currentlyPlayingPath.value = ""
        _currentPosition.value = 0L
        positionJob?.cancel()
    }

    fun deleteComment(filePath: String) {
        viewModelScope.launch {
            // 1. Eliminar archivo físicamente (si existe)
            val file = File(filePath)
            if (file.exists()) {
                file.delete()
            }

            // 2. Eliminar de Room
            audioRepositoryImpl.deleteAudio(filePath)

            // 3. Actualizar lista en memoria
            _comments.value = _comments.value.filterNot { it.filePath == filePath }
        }
    }

    fun seekTo(position: Long, playAfterSeek: Boolean = false, path: String) {
        audioPlayerManager.seekTo(position)
        _currentPosition.value = position
        if (playAfterSeek && !_isPlaying.value) {
            playAudio(path)
        }
    }

    fun fontSizeIncrease(){
        if (_fontSize.value < MAX_FONT_SIZE) _fontSize.value = (_fontSize.value.value + FONT_SIZE_CHANGER_VALUE).sp
    }

    fun fontSizeDecrease(){
        if (_fontSize.value > MIN_FONT_SIZE) _fontSize.value = (_fontSize.value.value - FONT_SIZE_CHANGER_VALUE).sp
    }

    fun showFont(){
        _showFont.value = !_showFont.value
    }

    fun showAnnotations(){
        _showAnnotations.value = !_showAnnotations.value
    }


    fun showDownloadDialog(){
        _showDownloadDialog.value = !showDownloadDialog.value
    }

    fun showFeedback(){
        Log.d("DEBUG", "showFeedback llamado")
        _showFeedback.value = !_showFeedback.value
    }

    fun showExtraButton(){
        _showExtraButton.value = !showExtraButton.value
    }

    fun startSpeech() {

        val currentPage = _currentPageIndex.value
        val currentText = _paragraphs.value
            .filter { it.pageNumber == currentPage + 1 }
            .joinToString("\n") { it.paragraphText }

        println("currentText del StartSpeech: $currentText")

        ttsManager.speak(currentText)
        _isSpeaking.value = true
        println("Start speech activado")
    }

    fun pauseSpeech() {
        print("Start speech pausado")
        ttsManager.pause()
        _isPaused.value = true
    }

    fun resumeSpeech() {
        print("Start speech resumido")
        ttsManager.resume()
        _isPaused.value = false
    }

    fun stopSpeech() {
        print("Start speech apagado")
        ttsManager.shutdown()
        _isSpeaking.value = false
        _isPaused.value = false
    }

    override fun onCleared() {
        super.onCleared()
        stopSpeech()
    }

    fun nextPage() {
        val total = _totalPages.value
        val currentIndex = _currentPageIndex.value
        if (currentIndex < total - 1) {
            _currentPageIndex.value = currentIndex + 1
            _isLastPage.value = (currentIndex + 1) == total - 1
            _isFirstPage.value = false
        } else {
            _isLastPage.value = true
        }
    }

    fun previousPage() {
        val currentIndex = _currentPageIndex.value
        if (currentIndex > 0) {
            _currentPageIndex.value = currentIndex - 1
            _isFirstPage.value = (currentIndex - 1) == 0
            _isLastPage.value = false
        } else {
            _isFirstPage.value = true
        }
    }

    fun speakCurrentPage() {
        stopSpeech()

        val currentPage = _currentPageIndex.value
        val currentText = _paragraphs.value
            .filter { it.pageNumber == currentPage }
            .joinToString("\n") { it.paragraphText }

        println("currentText del SpeakCurrentPage: $currentText")
        ttsManager.speak(currentText)

        _isSpeaking.value = true
        _isStopped.value = false
        _isPaused.value = false
        _showExtraButton.value = true
    }

    fun startRecording(){
        currentAudioFile = audioRecorderManager.startRecording()
    }

    fun stopRecording(){
        val recordedFile = audioRecorderManager.stopRecording()
        recordedFile?.takeIf { it.exists() && it.length() > 0 }?.let { file ->
            viewModelScope.launch {
                audioRepositoryImpl.saveAudio(
                    path = file.absolutePath,
                    taskId = "0",
                    title = "audio_${currentPageIndex.value}_${System.currentTimeMillis()}",
                    page = currentPageIndex.value,
                    date = "hoy"
                )
                _comments.value = audioRepositoryImpl.getAllAudios()
            }
        }
    }

    fun startFeedbackRecording() {
        currentAudioFile = audioRecorderManager.startRecording()
    }

    fun stopFeedbackRecording() {
        val recordedFile = audioRecorderManager.stopRecording()
        currentAudioFile = recordedFile
        _recordedFeedbackFilePath.value = recordedFile?.absolutePath
    }

    fun deleteFeedbackRecording() {
        currentAudioFile?.let { file ->
            if (file.exists()) {
                file.delete()
            }
        }
        currentAudioFile = null
        _recordedFeedbackFilePath.value = null
    }

    private fun manageFileDownload(
        context: Context,
        type: DownloadType
    ) {
        var resultMessage = ""
        _isDownloadInProgress.value = true
        viewModelScope.launch(context = Dispatchers.IO) {
            try {
                when (type) {
                    DownloadType.PDF -> downloadAsPdfUseCase(
                        context,
                        "Parcial de Seguridad en Aplicaciones Web", // TODO: Cambiar por el título del documento
                        _pages,
                        totalPages,
                        _fontSize.value.value
                    )
                    DownloadType.MP3 -> downloadAsMp3UseCase(
                        context,
                        "Parcial de Seguridad en Aplicaciones Web", // TODO: Cambiar por el título del documento
                        rawText
                    )
                    DownloadType.TXT -> downloadAsTxtUseCase(
                        context,
                        "Parcial de Seguridad en Aplicaciones Web", // TODO: Cambiar por el título del documento
                        rawText
                    )
                }
                resultMessage = "${type.friendlyName} downloaded successfully"
            } catch (e: Exception) {
                Log.e("TaskStudentViewModel", "Error generating and saving ${type.friendlyName}: ${e.message}")
                resultMessage = "Error downloading ${type.friendlyName}"
            } finally {
                withContext(Dispatchers.Main) {
                    _isDownloadInProgress.value = false
                    Toast.makeText(context, resultMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun downloadTextAsPdfFile(context: Context) = manageFileDownload(context, DownloadType.PDF)

    fun downloadTextAsTxtFile(context: Context) = manageFileDownload(context, DownloadType.TXT)

    fun downloadTextAsMp3File(context: Context) = manageFileDownload(context, DownloadType.MP3)

    companion object {
        private const val FONT_SIZE_CHANGER_VALUE = 6
        private val MAX_FONT_SIZE = 52.sp
        private val MIN_FONT_SIZE = 28.sp
    }

    fun loadProject(taskId: Int) {
        viewModelScope.launch {
            try {
                val userId = tokenManager.userId.first()
                if (userId != null) {
                    getOrders(userId).collect { response ->
                        when (response) {
                            is NetworkResponse.Success -> {
                                val allParagraphs = response.data?.flatMap { it.orderParagraphs }.orEmpty()
                                val matchingParagraphs = allParagraphs.filter { it.orderId == taskId }

                                val matchingOrderDelivered = response.data?.find { delivered ->
                                    delivered.orderParagraphs.any { it.orderId == taskId }
                                }

                                if (matchingOrderDelivered != null) {
                                    // Podés guardar también matchingParagraphs si querés exponerlo en otro StateFlow
                                    _projectState.value = NetworkResponse.Success(matchingOrderDelivered)
                                    _paragraphs.value = matchingParagraphs // <- si querés mostrar solo esos párrafos
                                } else {
                                    _projectState.value = NetworkResponse.Failure("Proyecto no encontrado")
                                }
                            }
                            is NetworkResponse.Failure -> {
                                println("Error al cargar los paragrafos")
                            }

                            is NetworkResponse.Loading -> {
                                println("Loading los paragrafos")
                        }
                        }
                    }
                } else {
                    _projectState.value = NetworkResponse.Failure("Usuario no autenticado")
                }
            } catch (e: Exception) {
                _projectState.value = NetworkResponse.Failure(e.message ?: "Error desconocido")
            }
        }
    }

}
