package com.example.proyectofinal.navigation.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.auth.data.model.UserResponseDto
import com.example.proyectofinal.auth.data.tokenmanager.TokenManager
import com.example.proyectofinal.auth.domain.provider.AuthRemoteProvider
import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.core.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    val authRemoteRepository: AuthRemoteProvider,
    val tokenManager: TokenManager
) : ViewModel() {

    private val _userState = MutableStateFlow<UiState<UserResponseDto>>(UiState.Loading)
    val userState: StateFlow<UiState<UserResponseDto>> = _userState

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val _mainScreenUiState = MutableStateFlow<MainScreenUiState>(MainScreenUiState.Loading)
    val mainScreenUiState: StateFlow<MainScreenUiState> = _mainScreenUiState

    init {
        viewModelScope.launch {
            tokenManager.token
                .distinctUntilChanged()
                .collectLatest { token ->
                    println("TokenManager emitted token: $token - hash: ${token.hashCode()} at ${System.currentTimeMillis()}")
                    println("MainViewModel: Token retrieved from TokenManager: $token") // Add this log
                    if (!token.isNullOrEmpty()) {
                        _mainScreenUiState.update { MainScreenUiState.Loading }
                        getUserData(token)
                    } else {
                        _mainScreenUiState.update { MainScreenUiState.Unauthenticated }
                        println("MainViewModel: Token is null or empty, isLoggedIn set to false") // Add this log
                    }
                }
        }
    }

    private suspend fun getUserData(token: String) {
        println("MainViewModel: Attempting to fetch user data with token: $token")
        authRemoteRepository.getMe()
            .onStart {
                if (_userState.value !is UiState.Loading) {
                    _userState.update { UiState.Loading }
                    println("MainViewModel: getUserData started, userState set to Loading")
                }
            }
            .catch { e ->
                _userState.update { UiState.Error(e.message.toString()) }
                _mainScreenUiState.update { MainScreenUiState.Unauthenticated }
                println("MainViewModel: getUserData failed with error: ${e.message}")
            }
            .collect { response ->
                println("MainViewModel: getUserData response received: $response")
                when (response) {
                    is NetworkResponse.Success -> {
                        _userState.update { UiState.Success(response.data) as UiState<UserResponseDto> }
                        _mainScreenUiState.update { MainScreenUiState.Authenticated }
                        println("MainViewModel: getUserData success, userState updated, MainScreenUiState set to Authenticated")
                    }
                    is NetworkResponse.Failure -> {
                        _userState.update { UiState.Error(response.error.toString()) }
                        _mainScreenUiState.update { MainScreenUiState.Unauthenticated }
                        println("MainViewModel: getUserData failure: ${response.error}")
                    }
                    is NetworkResponse.Loading -> {
                        _userState.update { UiState.Error("Respuesta vacía del servidor") }
                        _mainScreenUiState.update { MainScreenUiState.Unauthenticated } // Consider this carefully
                        println("MainViewModel: getUserData received empty response")
                    }
                }
            }
        println("TokenManager emitted token fun: $token - hash: ${token.hashCode()} at ${System.currentTimeMillis()}")

    }


    fun logout() {
        viewModelScope.launch {
            tokenManager.clearAuthData()
            _isLoggedIn.update { false }
            println("MainViewModel: Logout called, token cleared, isLoggedIn set to false") // Add this log
        }
    }
}

sealed class MainScreenUiState {
    object Loading : MainScreenUiState()
    object Authenticated : MainScreenUiState()
    object Unauthenticated : MainScreenUiState()
}