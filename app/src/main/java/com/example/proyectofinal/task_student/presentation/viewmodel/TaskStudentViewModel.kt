package com.example.proyectofinal.task_student.presentation.viewmodel


import android.util.Log
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.task_student.presentation.tts.TextToSpeechManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskStudentViewModel(
    private val ttsManager: TextToSpeechManager
): ViewModel() {

    val FONTSIZE_CHANGER_VALUE = 5
    val fontsize_max = 32.sp
    val fontsize_min = 14.sp

    // Texto que se muestra y se lee - hay que cambiar y que traiga texto de la api
    private val rawText = """
        Esta es la página uno. Waaauu bichon.

        Página dos aparece justo después de esta.

        Finalmente, llegamos a la página tres.
        
        Y pero si hay una página número cuatro?
        
        Y ni te digo si hay página número cinco. por el culo te la hinco
    """.trimIndent()

    // Manejo de texto y páginas

    private val _pages = rawText.split("\n\n")
    private val _currentPageIndex = MutableStateFlow(0)
    val currentPageIndex = _currentPageIndex.asStateFlow()

    val totalPages = _pages.size

    val texto = currentPageIndex.map { index -> _pages[index] }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = _pages[0]
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

    // Estado del fontsize

    private val _fontSize = MutableStateFlow(14.sp)
    val fontSize = _fontSize.asStateFlow()

    init {
        viewModelScope.launch {
            ttsManager.isStoped.collect { stopped ->
                _isStopped.value = stopped
                if(_isStopped.value == true){
                    _showExtraButton.value = false
                    _isSpeaking.value = false
                    ttsManager.resetStoppedFlag()
                    if(_currentPageIndex.value < totalPages-1){
                        nextPage()
                        speakCurrentPage()
                    }
                }
            }
        }
    }

    fun fontSizeIncrease(){
        if (_fontSize.value < fontsize_max) _fontSize.value = (_fontSize.value.value + FONTSIZE_CHANGER_VALUE).sp
    }

    fun fontSizeDecrease(){
        if (_fontSize.value > fontsize_min) _fontSize.value = (_fontSize.value.value - FONTSIZE_CHANGER_VALUE).sp
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
        ttsManager.speak(texto.value)
        print("Start speech activado")
        _isSpeaking.value = true
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
        if (_currentPageIndex.value == _pages.lastIndex-1){
            _isLastPage.value = true
            Log.d("Status variable","lastpage true")
        }
        if (_currentPageIndex.value < _pages.lastIndex) {
            _currentPageIndex.value++
            _isFirstPage.value = false
            Log.d("Status variable", "first page false")
        }
    }

    fun previousPage() {
        if(_currentPageIndex.value == 1){
            _isFirstPage.value = true
            Log.d("Status variable","first true")
        }
        if (_currentPageIndex.value > 0) {
            _currentPageIndex.value--
            _isLastPage.value = false
            Log.d("Status variable", "last page false")
        }
    }

    fun speakCurrentPage() {
        stopSpeech()
        val currentText = _pages[_currentPageIndex.value]
        ttsManager.speak(currentText)
        _isSpeaking.value = true
        _isStopped.value = false
        _isPaused.value = false
        _showExtraButton.value = true
    }

    fun startRecording(){
        TODO()
    }

    fun stopRecording(){
        TODO()
    }

}