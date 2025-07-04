package com.example.proyectofinal.task_student.presentation.viewmodel


import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.sp
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.audio.domain.model.RecordedAudio
import com.example.proyectofinal.audio.domain.repository.AudioRepository
import com.example.proyectofinal.audio.player.AudioPlayerManager
import com.example.proyectofinal.audio.recorder.AudioRecorderManager
import com.example.proyectofinal.auth.data.tokenmanager.TokenManager
import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.orderFeedback.domain.model.OrderFeedback
import com.example.proyectofinal.orderFeedback.domain.usecase.GetFeedbackUseCase
import com.example.proyectofinal.orderFeedback.domain.usecase.SaveFeedbackUseCase
import com.example.proyectofinal.orderFeedback.domain.usecase.SendFeedbackUseCase
import com.example.proyectofinal.orderManagement.data.repository.LastReadRepository
import com.example.proyectofinal.orderManagement.domain.model.OrderDelivered
import com.example.proyectofinal.orderManagement.domain.model.OrderParagraph
import com.example.proyectofinal.orderManagement.domain.provider.OrderManagementProvider
import com.example.proyectofinal.orderManagement.domain.repository.OrderRepository
import com.example.proyectofinal.orderManagement.domain.usecase.GetTaskGroupByStudentUseCase
import com.example.proyectofinal.task_student.domain.model.DownloadType
import com.example.proyectofinal.task_student.domain.usecase.DownloadAsMp3UseCase
import com.example.proyectofinal.task_student.domain.usecase.DownloadAsPdfUseCase
import com.example.proyectofinal.task_student.domain.usecase.DownloadAsTxtUseCase
import com.example.proyectofinal.task_student.presentation.tts.TextToSpeechManager
import com.example.proyectofinal.task_student.util.htmlToAnnotatedStringFormatted
import com.example.proyectofinal.userpreferences.data.manager.DataStoreManager
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TaskStudentViewModel(
    private val ttsManager: TextToSpeechManager,
    private val audioRecorderManager: AudioRecorderManager,
    private val audioPlayerManager: AudioPlayerManager,
    private val audioRepositoryImpl: AudioRepository,
    private val downloadAsPdfUseCase: DownloadAsPdfUseCase,
    private val downloadAsMp3UseCase: DownloadAsMp3UseCase,
    private val downloadAsTxtUseCase: DownloadAsTxtUseCase,
    private val getOrders: GetTaskGroupByStudentUseCase,
    private val tokenManager: TokenManager,
    private val orderRepository: OrderRepository,
    private val userPreferences: DataStoreManager,
    private val lastReadRepository: LastReadRepository,
    private val sendFeedbackUseCase: SendFeedbackUseCase,
    private val saveFeedbackUseCase: SaveFeedbackUseCase,
    private val getFeedbackUseCase: GetFeedbackUseCase,
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

    private val _currentTaskId = MutableStateFlow<Int?>(null)
    val currentTaskId = _currentTaskId.asStateFlow()

    private val _isFirstPage = MutableStateFlow(true)
    val isFirstPage = _isFirstPage.asStateFlow()

    private val _isLastPage = MutableStateFlow(false)
    val isLastPage = _isLastPage.asStateFlow()

    private val _annotatedParagraphs = MutableStateFlow<List<Pair<Int, AnnotatedString>>>(emptyList())
    val annotatedParagraphs: StateFlow<List<Pair<Int, AnnotatedString>>> = _annotatedParagraphs.asStateFlow()

    // Estado de reproducción
    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking = _isSpeaking.asStateFlow()

    private val _isPaused = MutableStateFlow(false)
    val isPaused = _isPaused.asStateFlow()

    private val _isStopped = MutableStateFlow(true)
    val isStopped = _isStopped.asStateFlow()

    private val _speechRate = MutableStateFlow(1.0f)
    val speechRate: StateFlow<Float> = _speechRate.asStateFlow()

    private val _pitch = MutableStateFlow(1.0f)
    val pitch: StateFlow<Float> = _pitch.asStateFlow()

    private val _editingPath = MutableStateFlow("")
    val editingPath: StateFlow<String> = _editingPath

    private val _editingName = MutableStateFlow("")
    val editingName: StateFlow<String> = _editingName

    private val _showEditDialog = MutableStateFlow(false)
    val showEditDialog: StateFlow<Boolean> = _showEditDialog

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

    private val _showFontsMenu = MutableStateFlow(false)
    val showFontsMenu: StateFlow<Boolean> = _showFontsMenu

    private val _showSpeechSettingsMenu = MutableStateFlow(false)
    val showSpeechSettingsMenu = _showSpeechSettingsMenu.asStateFlow()

    // Estado del font

    private val _fontSize = MutableStateFlow(MIN_FONT_SIZE)
    val fontSize = _fontSize.asStateFlow()

    private val _selectedFontFamily = MutableStateFlow<String?>(null)
    val selectedFontFamily: StateFlow<String?> = _selectedFontFamily

    private var minFontSize = 0f

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

    // Feedback State

    private val _feedbackState = MutableStateFlow<Boolean?>(null)
    val feedbackState: StateFlow<Boolean?> = _feedbackState

    private val _rating = MutableStateFlow(0)
    val rating: StateFlow<Int> = _rating.asStateFlow()

    private val _feedbackText = MutableStateFlow("")
    val feedbackText: StateFlow<String> = _feedbackText.asStateFlow()

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _recordedFilePath = MutableStateFlow<String?>(null)
    val recordedFilePath: StateFlow<String?> = _recordedFilePath.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme

    init {
        viewModelScope.launch {
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

            userPreferences.preferencesFlow.collect { prefs ->
                _selectedFontFamily.value = prefs.fontFamily
                minFontSize = prefs.fontSize
                _fontSize.value = minFontSize.sp
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

    fun startSpeech() {
        val currentPage = _currentPageIndex.value
        val currentTextHtml = _paragraphs.value
            .filter { it.pageNumber == currentPage + 1 }
            .joinToString("\n") { it.paragraphText }

        println("currentText del StartSpeech: $currentTextHtml")

        val currentTextPlain = htmlToPlainText(currentTextHtml)
        ttsManager.speak(currentTextPlain)
        _isSpeaking.value = true
        println("Start speech activado")
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
        val currentTextHtml = _paragraphs.value
            .filter { it.pageNumber == currentPage }
            .joinToString("\n") { it.paragraphText }

        println("currentText del SpeakCurrentPage: $currentTextHtml")

        val currentTextPlain = htmlToPlainText(currentTextHtml)
        ttsManager.speak(currentTextPlain)

        _isSpeaking.value = true
        _isStopped.value = false
        _isPaused.value = false
        _showExtraButton.value = true
    }

    fun startRecording(){
        currentAudioFile = audioRecorderManager.startRecording()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun stopRecording(){

        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currentDate = formatter.format(Date()) // "30/06/2025"

        val recordedFile = audioRecorderManager.stopRecording()
        recordedFile?.takeIf { it.exists() && it.length() > 0 }?.let { file ->
            viewModelScope.launch {
                audioRepositoryImpl.saveAudio(
                    path = file.absolutePath,
                    taskId = currentTaskId.value.toString(),
                    title = "Wirin_audio_task_${currentTaskId.value.toString()}_page${currentPageIndex.value+1}_${System.currentTimeMillis()}",
                    page = currentPageIndex.value,
                    date = currentDate
                )
                val studentId = tokenManager.userId.first() ?: return@launch
                val taskId = currentTaskId.value?.toString() ?: return@launch

                orderRepository.markOrderAsCommented(studentId, taskId)

                loadCommentsByTaskId(currentTaskId.value?: 0)
            }
        }
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
                        _projectState.value.data!!.title,
                        _paragraphs.value.map { htmlToPlainText(it.paragraphText) },
                        totalPages.value,
                        _fontSize.value.value
                    )
                    DownloadType.MP3 -> downloadAsMp3UseCase(
                        context,
                        _projectState.value.data!!.title,
                        htmlToPlainText(textoPorPagina.value)
                    )
                    DownloadType.TXT -> downloadAsTxtUseCase(
                        context,
                        _projectState.value.data!!.title,
                        htmlToPlainText(textoPorPagina.value)
                    )
                }
                resultMessage = "${type.friendlyName} downloaded successfully"
            } catch (e: Exception) {
                e.printStackTrace()
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

    fun saveLastPage(orderId: Int, page: Int) {
        viewModelScope.launch {
            lastReadRepository.saveLastReadPage(orderId, page)
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

                                    val parsed = matchingParagraphs.map { it.pageNumber to htmlToAnnotatedStringFormatted(it.paragraphText) }
                                    _annotatedParagraphs.value = parsed
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

    fun saveTaskId(taskId: Int){
        _currentTaskId.value = taskId
    }

    fun loadCommentsByTaskId(taskId: Int) {
        viewModelScope.launch {
            val comments = audioRepositoryImpl.getAudiosForTask(taskId.toString())
            _comments.value = comments
        }
    }

    fun htmlToPlainText(html: String): String {
            val cleaned = html
                .replace("<br>", "\n")
                .replace("<br/>", "\n")
                .replace("</p>", "\n")
                .replace("<p>", "")
                .replace("&nbsp;", " ")

            // 2. Convertir a texto plano
            return HtmlCompat.fromHtml(cleaned, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
    }

    fun setFontFamily(font: String) {
        _selectedFontFamily.value = font
        viewModelScope.launch {
            userPreferences.saveFontFamily(font)
        }
    }

    fun toggleFontMenu() {
        _showFontsMenu.value = !_showFontsMenu.value
    }

    fun closeFontMenu() {
        _showFontsMenu.value = false
    }

    fun loadFeedback(orderId: Int) {
        viewModelScope.launch {
            _feedbackState.value = getFeedbackUseCase(orderId)?.wasSent ?: false
        }
    }

    fun sendFeedbackToApi(orderId: Int, feedbackText: String, stars: Int) {
        viewModelScope.launch {
            val feedbackLocal = getFeedbackUseCase(orderId)
            if (feedbackLocal?.wasSent == true) {
                return@launch
            }

            val studentId = tokenManager.userId.first() ?: return@launch

            try {
                val feedback = OrderFeedback(
                    studentId = studentId,
                    feedbackText = feedbackText,
                    stars = stars,
                    orderId = orderId
                )

                println("Feedback to send: $feedback.studentId, $feedback.feedbackText, $feedback.stars, $feedback.orderId")

                sendFeedbackUseCase(feedback).collect { response ->
                    when(response) {
                        is NetworkResponse.Success -> {
                            saveFeedbackUseCase(orderId, true)
                            _feedbackState.value = true
                        }
                        is NetworkResponse.Failure -> {
                            Log.e("Feedback", "Error al enviar feedback: ${response.error}")
                        }
                        is NetworkResponse.Loading -> {
                            // mostrar loading si querés
                        }
                    }
                }

            } catch (e: Exception) {
                Log.e("Feedback", "Excepción al enviar feedback: ${e.message}")
            }
        }
    }

    fun setRating(value: Int) {
        _rating.value = value
    }

    fun setFeedbackText(text: String) {
        _feedbackText.value = text
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

    fun setSpeechRate(rate: Float) {
        if (rate <= 0f) return
        _speechRate.value = rate
        ttsManager.setSpeechRate(rate)

        when {
            isSpeaking.value -> {
                restartSpeechWithNewParams()
            }

            isPaused.value -> {
                ttsManager.pause()
            }
            else -> {}
        }
    }

    private fun restartSpeechWithNewParams() {
        ttsManager.pause()
        ttsManager.resume()
        _isSpeaking.value = true
        _isPaused.value = false
    }

    fun toggleSpeechSettingsMenu() {
        _showSpeechSettingsMenu.value = !_showSpeechSettingsMenu.value
    }

    fun closeSpeechSettingsMenu() {
        _showSpeechSettingsMenu.value = false
    }

    fun playSpeech(){
        ttsManager.resume()
        _isSpeaking.value = true
        _isPaused.value = false
    }

    fun pauseSpeech(){
        ttsManager.pause()
        _isSpeaking.value = false
        _isPaused.value = true
    }

    fun openEditDialog(filePath: String, currentName: String) {
        _editingPath.value = filePath
        _editingName.value = currentName
        _showEditDialog.value = true
    }

    fun updateEditingName(newName: String) {
        _editingName.value = newName
    }

    fun closeEditDialog() {
        _showEditDialog.value = false
        _editingPath.value = ""
        _editingName.value = ""
    }

    fun confirmEditName() {
        viewModelScope.launch {
            val newName = editingName.value.trim()
            val path = editingPath.value
            val current = _comments.value.find { it.filePath == path } ?: return@launch

            val isDuplicate = _comments.value.any {
                it.title.equals(newName, ignoreCase = true) &&
                        it.filePath != path &&
                        it.associatedTaskId == current.associatedTaskId
            }

            if (newName.isBlank() || isDuplicate) return@launch

            audioRepositoryImpl.updateAudioName(path, newName)

            _comments.value = _comments.value.map {
                if (it.filePath == path) it.copy(title = newName) else it
            }

            closeEditDialog()
        }
    }

}
