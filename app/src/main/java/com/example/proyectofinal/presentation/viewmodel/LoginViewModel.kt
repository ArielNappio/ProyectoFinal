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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: RemoteRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _email = MutableStateFlow<String>("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow<String>("")
    val password = _password.asStateFlow()

    private val _isLoading = MutableStateFlow<String>("")
    val isLoading = _isLoading.asStateFlow()

    private val _loginState = MutableStateFlow<NetworkResponse<LoginResponse>>(NetworkResponse.Loading())
    val loginState: StateFlow<NetworkResponse<LoginResponse>> = _loginState


    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    fun onLoginClick() {
        viewModelScope.launch {
            if(email.value.isNotEmpty() && password.value.isNotEmpty())
            {
                repository.postLogin(LoginRequest(email.value, password.value)).collect { response ->
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
            else print("ta vacio")
        }
    }


}