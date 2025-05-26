package com.example.proyectofinal.student.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.audio.data.repository.AudioRepositoryImpl
import com.example.proyectofinal.audio.domain.model.RecordedAudio
import com.example.proyectofinal.audio.player.AudioPlayerManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CommentsViewModel(
    private val audioRepositoryImpl: AudioRepositoryImpl,
    private val audioPlayerManager: AudioPlayerManager
) : ViewModel() {

    private val _comments = MutableStateFlow<List<RecordedAudio>>(emptyList())
    val comments: StateFlow<List<RecordedAudio>> = _comments

    private val _currentlyPlayingPath = MutableStateFlow<String>("")
    val currentlyPlayingPath : StateFlow<String> = _currentlyPlayingPath

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    init {
        viewModelScope.launch {
            _comments.value = audioRepositoryImpl.getAllAudios()
        }
    }

    fun playAudio(filePath: String){
            if (audioPlayerManager.isPlaying(filePath)) {
                stopAudio()
            } else {
                audioPlayerManager.play(filePath = filePath) {
                    _currentlyPlayingPath.value = "" // Cuando termina de sonar
                }
                _currentlyPlayingPath.value = filePath
            }
    }

    fun stopAudio(){
        audioPlayerManager.stop()
        _currentlyPlayingPath.value = ""
    }

    fun deleteComment(filePath: String) {
        viewModelScope.launch {
            // 1. Eliminar archivo físicamente (si existe)
            val file = java.io.File(filePath)
            if (file.exists()) {
                file.delete()
            }

            // 2. Eliminar de Room
            audioRepositoryImpl.deleteAudio(filePath)

            // 3. Actualizar lista en memoria
            _comments.value = _comments.value.filterNot { it.filePath == filePath }
        }
    }

    fun isPlaying(){
        _isPlaying.value = !_isPlaying.value
    }

}