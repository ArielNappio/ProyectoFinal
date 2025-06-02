package com.example.proyectofinal.student.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.audio.data.repository.AudioRepositoryImpl
import com.example.proyectofinal.audio.domain.model.RecordedAudio
import com.example.proyectofinal.audio.player.AudioPlayerManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition

    private var positionJob: Job? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    init {
        viewModelScope.launch {
            _comments.value = audioRepositoryImpl.getAllAudios()
        }
    }

    fun playAudio(filePath: String) {
        viewModelScope.launch {
            when {
                // Si es el mismo archivo y está reproduciendo, pausamos
                _currentlyPlayingPath.value == filePath && _isPlaying.value -> {
                    audioPlayerManager.pause()
                    _isPlaying.value = false
                    positionJob?.cancel()
                }

                // Si es el mismo archivo y está pausado, reanudamos
                _currentlyPlayingPath.value == filePath && !_isPlaying.value -> {
                    audioPlayerManager.resume()
                    _isPlaying.value = true
                    startPositionTracking(filePath)
                }

                // Si es otro archivo, reproducimos desde cero
                else -> {
                    stopAudio() // por si había algo sonando
                    audioPlayerManager.play(filePath) {
                        _currentlyPlayingPath.value = ""
                        _currentPosition.value = 0L
                        _isPlaying.value = false
                        positionJob?.cancel()
                    }
                    _currentlyPlayingPath.value = filePath
                    _isPlaying.value = true
                    startPositionTracking(filePath)
                }
            }
        }
    }

    private fun startPositionTracking(filePath: String) {
        positionJob?.cancel()
        positionJob = viewModelScope.launch {
            while (_currentlyPlayingPath.value == filePath && _isPlaying.value) {
                _currentPosition.value = audioPlayerManager.getCurrentPosition()
                delay(200L)
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

    fun seekTo(position: Long, playAfterSeek: Boolean = false, path: String) {
        audioPlayerManager.seekTo(position)
        _currentPosition.value = position
        if (playAfterSeek && !_isPlaying.value) {
            playAudio(path)
        }
    }
}