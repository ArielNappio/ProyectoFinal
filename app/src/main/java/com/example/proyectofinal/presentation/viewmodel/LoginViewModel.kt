package com.example.proyectofinal.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.data.local.TokenManager
import com.example.proyectofinal.data.remoteData.model.LoginRequest
import com.example.proyectofinal.data.remoteData.model.LoginResponse
import com.example.proyectofinal.data.remoteData.repository.RemoteRepository
import com.example.proyectofinal.util.NetworkResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: RemoteRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _loginState = MutableStateFlow<NetworkResponse<LoginResponse>>(NetworkResponse.Loading())
    val loginState: StateFlow<NetworkResponse<LoginResponse>> = _loginState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            repository.postLogin(LoginRequest(email, password)).collect { response ->
                _loginState.value = response

                if (response is NetworkResponse.Success && response.data != null) {
                    tokenManager.saveToken(response.data.token)
                    // aca hay que hacer que vaya al main
                }
                else{
                    print("response.data null ${response.data}")
                }
            }
        }
    }
}