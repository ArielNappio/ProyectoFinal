package com.example.proyectofinal.navigation.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.auth.data.local.TokenManager
import com.example.proyectofinal.auth.data.remoteData.model.UserResponse
import com.example.proyectofinal.auth.data.remoteData.repository.AuthRemoteRepository
import com.example.proyectofinal.core.network.NetworkResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch


class MainViewModel(
    private val authRemoteRepository: AuthRemoteRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _userState = MutableStateFlow<UiState<UserResponse>>(UiState.Loading)
    val userState: StateFlow<UiState<UserResponse>> = _userState

    init{
        viewModelScope.launch {
            val token = tokenManager.token.firstOrNull()
            if(!token.isNullOrEmpty()){
                authRemoteRepository.getMe(token)
                    .onStart {
                        _userState.value = UiState.Loading
                    }
                    .catch { e ->
                        _userState.value = UiState.Error(e.message)
                    }
                    .collect { response ->
                        when(response) {
                            is NetworkResponse.Success ->{
                                _userState.value = UiState.Success(response.data)
                            }
                            is NetworkResponse.Failure -> {
                                _userState.value = UiState.Error(response.error)
                            }
                            is NetworkResponse.Loading -> {
                                _userState.value = UiState.Error("Respuesta vacía del servidor")
                            }
                        }
                    }
            }

        }
    }

}

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<out T>(val data: T?) : UiState<T>()
    data class Error(val message: String?) : UiState<Nothing>()
}