package com.example.proyectofinal.student.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.proyectofinal.student.data.model.Note
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeScreenViewModel : ViewModel() {
    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()

    init {
        loadNotes()
    }

    fun toggleFavorite(noteId: Int) {
        _notes.value = _notes.value.map {
            if (it.id == noteId) {
                it.copy(isFavorite = !it.isFavorite)
            } else {
                it
            }
        }
    }

    private fun loadNotes() {
        _notes.value = listOf(
            Note(1, "Nota 1", "Descripción de la nota 1", false),
            Note(2, "Nota 2", "Descripción de la nota 2", true),
            Note(3, "Nota 3", "Descripción de la nota 3", false),
            Note(4, "Nota 1", "Descripción de la nota 1", false),
            Note(5, "Nota 2", "Descripción de la nota 2", true),
            Note(6, "Nota 3", "Descripción de la nota 3", false),
            Note(6, "Nota 3", "Descripción de la nota 3", false)
        )
    }
}