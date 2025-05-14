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

     fun loadNotes() {
        _notes.value = listOf(
            Note(1, "Apunte de Redes", "Resumen de los modelos OSI y TCP/IP con ejemplos.", true),
            Note(2, "Práctica de Estadística", "Ejercicios de media, mediana, moda y desvío estándar.", true),
            Note(3, "Parcial de Base de Datos", "Preguntas resueltas del parcial de SQL y modelado.", true),
            Note(4, "Trabajo Práctico de UI/UX", "Prototipo y diseño de app para gestión de turnos médicos.", false),
            Note(5, "Resumen de Historia Económica", "Principales crisis económicas del siglo XX.", true),
            Note(6, "Apunte de Arquitectura de Computadoras", "Resumen de tipos de memoria y ciclos de instrucción.", false),
            Note(7, "Filosofía: clase 3", "Ideas principales de Platón y su teoría de las Ideas.", false),
        )
    }

}