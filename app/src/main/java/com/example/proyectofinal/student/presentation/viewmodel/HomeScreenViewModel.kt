package com.example.proyectofinal.student.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.auth.data.model.UserResponseDto
import com.example.proyectofinal.auth.data.tokenmanager.TokenManager
import com.example.proyectofinal.auth.domain.provider.AuthRemoteProvider
import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.core.util.UiState
import com.example.proyectofinal.navigation.presentation.viewmodel.MainScreenUiState
import com.example.proyectofinal.orderManagment.domain.usecase.GetOrdersManagmentUseCase
import com.example.proyectofinal.student.domain.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeScreenViewModel(
    private val getOrdersManagment: GetOrdersManagmentUseCase,
    private val tokenManager: TokenManager,
    private val authRemoteProviderImpl: AuthRemoteProvider
) : ViewModel() {

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _orderManagmentState = MutableStateFlow<NetworkResponse<List<Task>>>(NetworkResponse.Loading())
    val orderManagmentState: StateFlow<NetworkResponse<List<Task>>> = _orderManagmentState.asStateFlow()

    private val _userState = MutableStateFlow<UiState<UserResponseDto>>(UiState.Loading)
    val userState: StateFlow<UiState<UserResponseDto>> = _userState

    private val _mainScreenUiState = MutableStateFlow<MainScreenUiState>(MainScreenUiState.Loading)
    val mainScreenUiState: StateFlow<MainScreenUiState> = _mainScreenUiState

    init {
        getOrdersManagments("b3c8c79e-4173-4a74-b8ef-28b4f5446333") // tu studentId real

        viewModelScope.launch {
            tokenManager.token
                .distinctUntilChanged()
                .collectLatest { token ->
                    println("TokenManager emitted token: $token - hash: ${token.hashCode()} at ${System.currentTimeMillis()}")
                    println("MainViewModel: Token retrieved from TokenManager: $token") // Add this log
                    if (!token.isNullOrEmpty()) {
                        getUserData(token)
                    } else {
                        println("asdasd") // Add this log
                    }
                }
        }
    }

    private fun getOrdersManagments(studentId: String) {
        viewModelScope.launch {
            getOrdersManagment(studentId).collect { response ->
                _orderManagmentState.value = response
            }
        }
    }

    private suspend fun getUserData(token: String) {
        println("MainViewModel: Attempting to fetch user data with token: $token")
        authRemoteProviderImpl.getMe(token)
            .onStart {
                if (_userState.value !is UiState.Loading) {
                    _userState.update { UiState.Loading }
                    println("home: getUserData started, userState set to Loading")
                }
            }
            .catch { e ->
                _userState.update { UiState.Error(e.message.toString()) }
                _mainScreenUiState.update { MainScreenUiState.Unauthenticated }
                println("home: getUserData failed with error: ${e.message}")
            }
            .collect { response ->
                println("home: getUserData response received: $response")
                when (response) {
                    is NetworkResponse.Success -> {
                        _userState.update { UiState.Success(response.data) as UiState<UserResponseDto> }
                        println("home: getUserData success, userState updated")
                    }
                    is NetworkResponse.Failure -> {
                        _userState.update { UiState.Error(response.error.toString()) }
                        println("home: getUserData failure: ${response.error}")
                    }
                    is NetworkResponse.Loading -> {
                        _userState.update { UiState.Error("Respuesta vacía del servidor") }
                        _mainScreenUiState.update { MainScreenUiState.Unauthenticated } // Consider this carefully
                        println("home: getUserData received empty response")
                    }
                }
            }
        println("TokenManager emitted token fun: $token - hash: ${token.hashCode()} at ${System.currentTimeMillis()}")

    }



    fun updateSearchText(newText: String) {
        _searchText.value = newText
    }

    fun toggleFavorite(taskId: Int) {
        // repository.toggleFavorite(taskId)
    }
}
