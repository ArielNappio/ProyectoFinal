package com.example.proyectofinal.student.data.repository

import com.example.proyectofinal.student.data.model.AudioComment

class CommentsRepository {
    fun getCommentsByTaskId(taskId: Int): List<AudioComment> {
        return listOf(
            AudioComment(1, "Comentario 1", "https://example.com/audio1.mp3", page = 3, date = "2025-05-18"),
            AudioComment(2, "Comentario 2", "https://example.com/audio2.mp3", page = 7, date = "2025-05-19")
        )
    }
}