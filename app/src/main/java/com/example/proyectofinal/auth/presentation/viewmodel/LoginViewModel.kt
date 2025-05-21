package com.example.proyectofinal.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.auth.data.local.TokenManager
import com.example.proyectofinal.auth.data.remoteData.model.LoginRequest
import com.example.proyectofinal.auth.data.remoteData.model.LoginResponse
import com.example.proyectofinal.auth.data.remoteData.repository.AuthRemoteRepository
import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.core.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: AuthRemoteRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _email = MutableStateFlow<String>("admin@biblioteca.com")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow<String>("Test123.")
    val password = _password.asStateFlow()

    private val _isLoading = MutableStateFlow<Boolean>(true)
    val isLoading = _isLoading.asStateFlow()

    private val _loginState =
        MutableStateFlow<UiState<LoginResponse>>(UiState.Loading)
    val loginState: StateFlow<UiState<LoginResponse>> = _loginState

    private val _navigateToMain = MutableStateFlow<Boolean>(false)
    val navigateToMain: StateFlow<Boolean> = _navigateToMain.asStateFlow()

    init {
        _isLoading.update { true }
        checkExistingToken()
    }


    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    fun onLoginClick() {
        viewModelScope.launch {
            println("Intentando loguear con ${email.value} y ${password.value}")

            if(email.value.isNotEmpty() && password.value.isNotEmpty())
            {
                println("entro al if de que no esta empty")
                _isLoading.update { true }
                repository.postLogin(LoginRequest(email.value, password.value)).collect { response ->
                    when (response) {
                        is NetworkResponse.Success -> {
                            _isLoading.update { false }
                            println("Respuesta exitosa: ${response.data}")
                            if (response.data != null) {
                                tokenManager.saveToken(response.data.token)
                                _loginState.update { UiState.Success(response.data) }
                                println("token guardado exitosamente: ${response.data.token}")
                            } else {
                                println("Datos nulos en la respuesta")
                            }
                        }
                        is NetworkResponse.Loading -> {
                            _isLoading.update { true }
                            println("Cargando...")
                        }
                        is NetworkResponse.Failure<*> -> {
                            _isLoading.update { false }
                            println("Falló")
                        }
                    }
                }
            }
            else {
                _loginState.update { UiState.Error("Email o contraseña vacíos") }
                print("ta vacio")
            }
        }
    }

    private fun checkExistingToken() {
        viewModelScope.launch {
            val token = tokenManager.token.firstOrNull()
            if (!token.isNullOrEmpty()) {
                _navigateToMain.update { true }
                println("LoginViewModel: Existing token found, navigating to MainScreen")
            } else {
                _navigateToMain.update { false }
                _isLoading.update { false }
                println("LoginViewModel: No existing token found")
            }
        }
    }

}
