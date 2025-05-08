package com.example.proyectofinal.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.auth.data.local.TokenManager
import com.example.proyectofinal.auth.data.remoteData.model.LoginRequest
import com.example.proyectofinal.auth.data.remoteData.model.LoginResponse
import com.example.proyectofinal.auth.data.remoteData.repository.AuthRemoteRepository
import com.example.proyectofinal.core.network.NetworkResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading = _isLoading.asStateFlow()

    private val _loginState =
        MutableStateFlow<NetworkResponse<LoginResponse>>(NetworkResponse.Loading())
    val loginState: StateFlow<NetworkResponse<LoginResponse>> = _loginState




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
                repository.postLogin(LoginRequest(email.value, password.value)).collect { response ->
                    when (response) {
                        is NetworkResponse.Success -> {
                            println("Respuesta exitosa: ${response.data}")
                            if (response.data != null) {
                                tokenManager.saveToken(response.data.token)
                                _loginState.update { NetworkResponse.Success(response.data) }
                                println("token guardado exitosamente: ${response.data.token}")
                            } else {
                                println("Datos nulos en la respuesta")
                            }
                        }
                        is NetworkResponse.Loading -> {
                            println("Cargando...")
                        }
                        is NetworkResponse.Failure<*> -> println("Falló")
                    }
                }
            }
            else print("ta vacio")
        }
    }


}