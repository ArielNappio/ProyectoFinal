package com.example.proyectofinal.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.data.remoteData.model.Item
import com.example.proyectofinal.data.remoteData.repository.RemoteRepository
import com.example.proyectofinal.util.NetworkResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TestViewModel(
    private val repository: RemoteRepository): ViewModel() {

        private val _getData = MutableStateFlow<NetworkResponse<List<Item>>?>(null)
        val getData = _getData.asStateFlow()

    val gettinData = fun() {

        viewModelScope.launch {
            repository.getItem().collect { res ->
                _getData.update {
                    res }
            }
        }

    }

}