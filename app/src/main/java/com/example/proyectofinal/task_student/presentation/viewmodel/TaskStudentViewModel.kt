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

    // Texto que se muestra y se lee - hay que cambiar y que traiga texto de la api
    private val rawText = """
        1- ¿A que ataque del OWASP Top-Ten se refiere la siguiente definición: "El atacante puede ejecutar secuencias de
        comandos en el navegador de la víctima..."?
        A. Secuencia de Comandos en Sitios Cruzados (XSS)
        B. Ausencia de Control de Acceso a Funciones
        C. Falsificación de Peticiones en sitios Cruzados (CSRF)
        D. Referencia Directa Insegura a Objetos

        2- ¿Cuál de estas tecnologías es considerada generadora de riesgo por ser ejecutada en el cliente?
        A. Java Applet
        B. Active X
        C. JavaScript
        D. Todas son correctas

        3- ¿Cuál de los siguientes puntos NO corresponde a un tipo de vulnerabilidad?
        A. Debidas al uso
        B. Debidas al diseño
        C. Debidas a la implementación
        D. Ninguna de las anteriores
        
        4- ¿Cuál de estas afirmaciones es verdadera con relación a los Firewalls?
        A. No protege de ataques internos
        B. No protege de ataques internos
        C. No protege de todos los ataques dañinos
        D. Todas las anteriores
        
        5- ¿Cuál de los siguientes puntos no es un atributo del protocolo TCP?
        A. No es orientado a conexión
        B. Corre sobre IP
        C. Cada paquete tiene un numero de secuencia y un flag
        D. Un paquete tiene un numero de puerto origen y destino
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

    private val _fontSize = MutableStateFlow(MIN_FONT_SIZE)
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
        if (_fontSize.value < MAX_FONT_SIZE) _fontSize.value = (_fontSize.value.value + FONT_SIZE_CHANGER_VALUE).sp
    }

    fun fontSizeDecrease(){
        if (_fontSize.value > MIN_FONT_SIZE) _fontSize.value = (_fontSize.value.value - FONT_SIZE_CHANGER_VALUE).sp
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

    companion object {
        private const val FONT_SIZE_CHANGER_VALUE = 6
        private val MAX_FONT_SIZE = 52.sp
        private val MIN_FONT_SIZE = 28.sp
    }

}