package com.example.proyectofinal.task_student.presentation.viewmodel


import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.proyectofinal.task_student.presentation.tts.TextToSpeechManager
import kotlinx.coroutines.launch

class TaskStudentViewModel(
    private val ttsManager: TextToSpeechManager
): ViewModel() {

    // Texto que se muestra y se lee
    private val _texto = MutableStateFlow(
        """ pinga. 
            la concha de la gorra, que esta pasando que esta mierda no lee todo?
            monstruo. chocho
        """.trimIndent()
    )

    val texto = _texto.asStateFlow()

    // Estado de reproducción
    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking = _isSpeaking.asStateFlow()

    private val _isPaused = MutableStateFlow(false)
    val isPaused = _isPaused.asStateFlow()

    private val _showExtraButton = MutableStateFlow(false)
    val showExtraButton = _showExtraButton.asStateFlow()

    private val _showDownloadDialog = MutableStateFlow(false)
    val showDownloadDialog = _showDownloadDialog.asStateFlow()

    private val _fontSize = MutableStateFlow(14.sp)
    val fontSize = _fontSize.asStateFlow()

    private val _isStopped = MutableStateFlow(true)
    val isStopped = _isStopped.asStateFlow()

    init {
        viewModelScope.launch {
            ttsManager.isStoped.collect { stopped ->
                _isStopped.value = stopped
                if(_isStopped.value == true){
                    _showExtraButton.value = false
                    _isSpeaking.value = false
                    ttsManager.resetStoppedFlag()
                }
            }
        }
    }

    fun fontSizeIncrease(){
        if (_fontSize.value < 32.sp) _fontSize.value = (_fontSize.value.value + 2).sp
    }

    fun fontSizeDecrease(){
        if (_fontSize.value > 14.sp) _fontSize.value = (_fontSize.value.value - 2).sp
    }

    fun showDownloadDialog(){
        _showDownloadDialog.value = !showDownloadDialog.value
    }

    fun showExtraButton(){
        _showExtraButton.value = !showExtraButton.value
    }

    fun startSpeech() {
        ttsManager.speak(_texto.value)
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
}