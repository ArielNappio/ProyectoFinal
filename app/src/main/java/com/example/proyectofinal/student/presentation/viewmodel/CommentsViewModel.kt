package com.example.proyectofinal.student.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.proyectofinal.student.domain.model.AudioComment
import com.example.proyectofinal.student.data.repository.CommentsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CommentsViewModel(
    private val commentsRepository: CommentsRepository
) : ViewModel() {
    private val _comments = MutableStateFlow<List<AudioComment>>(emptyList())
    val comments: StateFlow<List<AudioComment>> = _comments

    fun getCommentsByTaskId(taskId: Int) {
        val result = commentsRepository.getCommentsByTaskId(taskId)
        _comments.value = result
    }

}