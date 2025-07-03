package com.example.proyectofinal.navigation.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.auth.data.model.UserResponseDto
import com.example.proyectofinal.auth.data.tokenmanager.TokenManager
import com.example.proyectofinal.auth.domain.usecases.GetMeUseCase
import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.core.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val getMeUseCase: GetMeUseCase,
    val tokenManager: TokenManager
) : ViewModel() {

    private val _userState = MutableStateFlow<UiState<UserResponseDto>>(UiState.Loading)
    val userState: StateFlow<UiState<UserResponseDto>> = _userState

    private val _mainScreenUiState = MutableStateFlow<MainScreenUiState>(MainScreenUiState.Loading)
    val mainScreenUiState: StateFlow<MainScreenUiState> = _mainScreenUiState

    fun refreshSession() {
        viewModelScope.launch {
            val token = tokenManager.token.firstOrNull()
            if (!token.isNullOrEmpty()) {
                _mainScreenUiState.update { MainScreenUiState.Loading }
                getUserData(token)
            } else {
                _mainScreenUiState.update { MainScreenUiState.Unauthenticated }
                println("MainViewModel: No token, user is unauthenticated")
            }
        }
    }

    private suspend fun getUserData(token: String) {
        println("MainViewModel: Attempting to fetch user data with token: $token")
        getMeUseCase()
            .onStart {
                _userState.update { UiState.Loading }
            }
            .catch { e ->
                _userState.update { UiState.Error(e.message ?: "Error desconocido") }
                _mainScreenUiState.update { MainScreenUiState.Unauthenticated }
            }
            .collect { response ->
                when (response) {
                    is NetworkResponse.Success -> {
                        _userState.update { UiState.Success(response.data) as UiState<UserResponseDto> }
                        _mainScreenUiState.update { MainScreenUiState.Authenticated }
                    }
                    is NetworkResponse.Failure -> {
                        _userState.update { UiState.Error(response.error.toString()) }
                        _mainScreenUiState.update { MainScreenUiState.Unauthenticated }
                    }
                    is NetworkResponse.Loading -> {
                        _userState.update { UiState.Loading }
                    }
                }
            }
    }

    fun logout() {
        viewModelScope.launch {
            tokenManager.clearAuthData()
            _mainScreenUiState.update { MainScreenUiState.Unauthenticated }
            println("MainViewModel: Sesión cerrada")
        }
    }
}

sealed class MainScreenUiState {
    object Loading : MainScreenUiState()
    object Authenticated : MainScreenUiState()
    object Unauthenticated : MainScreenUiState()
}