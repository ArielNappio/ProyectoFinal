package com.example.proyectofinal.student.data.repository

import com.example.proyectofinal.student.data.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class TaskRepository {

    private val tasks = MutableStateFlow(
        listOf(
            Task(1, "Parcial de Seguridad en Applicaciones Web", "Preguntas modelo del primer parcial de vulnerabilidades y Protocolo TCP/IP.", true, "2025-05-18", 5, true),
            Task(2, "Apunte de Redes", "Resumen de los modelos OSI y TCP/IP con ejemplos.", false, "2025-05-17", 12, false),
            Task(3, "Práctica de Estadística", "Ejercicios de media, mediana, moda y desvío estándar.", true, "2025-05-10", 8, false),
            Task(4, "Trabajo Práctico de UI/UX", "Prototipo y diseño de app para gestión de turnos médicos.", false, "2025-05-15", 16, true),
            Task(5, "Resumen de Historia Económica", "Principales crisis económicas del siglo XX.", true, "2025-05-12", 20, true),
            Task(6, "Apunte de Arquitectura de Computadoras", "Resumen de tipos de memoria y ciclos de instrucción.", false, "2025-05-08", 10, true),
            Task(7, "Filosofía: clase 3", "Ideas principales de Platón y su teoría de las Ideas.", false, "2025-05-01", 6, true),
        )
    )

    fun getAllTasks() = tasks.asStateFlow()

    fun getTaskById(taskId: Int): Task? = tasks.value.find { it.id == taskId }

    fun toggleFavorite(taskId: Int) {
        tasks.value = tasks.value.map {
            if (it.id == taskId) it.copy(isFavorite = !it.isFavorite) else it
        }
    }
}
