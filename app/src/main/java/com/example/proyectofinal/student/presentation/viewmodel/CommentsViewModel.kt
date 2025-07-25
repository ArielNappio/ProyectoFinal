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
import java.io.File

class CommentsViewModel(
    private val audioRepositoryImpl: AudioRepositoryImpl,
    private val audioPlayerManager: AudioPlayerManager,
) : ViewModel() {

    private val _comments = MutableStateFlow<List<RecordedAudio>>(emptyList())
    val comments: StateFlow<List<RecordedAudio>> = _comments

    private val _currentlyPlayingPath = MutableStateFlow<String>("")
    val currentlyPlayingPath: StateFlow<String> = _currentlyPlayingPath

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition

    private var positionJob: Job? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _showEditDialog = MutableStateFlow(false)
    val showEditDialog: StateFlow<Boolean> = _showEditDialog

    private val _editingName = MutableStateFlow("")
    val editingName: StateFlow<String> = _editingName

    private val _editingPath = MutableStateFlow("")
    val editingPath: StateFlow<String> = _editingPath

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

    fun stopAudio() {
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

    fun loadCommentsByTaskId(taskId: Int) {
        viewModelScope.launch {
            val comments = audioRepositoryImpl.getAudiosForTask(taskId.toString())
            _comments.value = comments
        }
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
                it.title.equals(
                    newName,
                    ignoreCase = true
                ) && it.filePath != path && it.associatedTaskId == current.associatedTaskId
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