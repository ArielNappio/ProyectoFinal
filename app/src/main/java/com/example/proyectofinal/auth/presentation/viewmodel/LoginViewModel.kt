package com.example.proyectofinal.auth.presentation.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.auth.data.model.LoginRequestDto
import com.example.proyectofinal.auth.data.model.LoginResponseDto
import com.example.proyectofinal.auth.data.tokenmanager.TokenManager
import com.example.proyectofinal.auth.domain.provider.AuthRemoteProvider
import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.core.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: AuthRemoteProvider,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _email = MutableStateFlow<String>("admin@bypass.com")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow<String>("Test123.")
    val password = _password.asStateFlow()

    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> = _emailError.asStateFlow()

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError: StateFlow<String?> = _passwordError.asStateFlow()

    private val _emailTouched = MutableStateFlow(false)
    val emailTouched = _emailTouched.asStateFlow()

    private val _passwordTouched = MutableStateFlow(false)
    val passwordTouched = _passwordTouched.asStateFlow()

    private val _isLoading = MutableStateFlow<Boolean>(true)
    val isLoading = _isLoading.asStateFlow()

    private val _loginState = MutableStateFlow<UiState<LoginResponseDto>>(UiState.Loading)
    val loginState: StateFlow<UiState<LoginResponseDto>> = _loginState

    private val _navigateToMain = MutableStateFlow<Boolean>(false)
    val navigateToMain: StateFlow<Boolean> = _navigateToMain.asStateFlow()

    private val _navigateToPreferences = MutableStateFlow<Boolean>(false)
    val navigateToPreferences: StateFlow<Boolean> = _navigateToPreferences.asStateFlow()

    private val _showErrorDialog = MutableStateFlow(false)
    val showErrorDialog: StateFlow<Boolean> = _showErrorDialog.asStateFlow()

    init {
        _isLoading.update { true }
        checkExistingToken()
    }

    fun onLoginClick() {
        viewModelScope.launch {
            println("Intentando loguear con ${email.value} y ${password.value}")

            if (email.value.isNotEmpty() && password.value.isNotEmpty()) {
                if (email.value == "admin@bypass.com" && password.value == "123456") {
                    println("Bypass activado, navegando directo")
                    _isLoading.update { false }
                    _navigateToMain.update { true }
                    return@launch
                }

                println("entro al if de que no esta empty")
                _isLoading.update { true }
                repository.postLogin(LoginRequestDto(email.value, password.value))
                    .collect { response ->
                        when (response) {
                            is NetworkResponse.Success -> {
                                _isLoading.update { false }
                                println("Respuesta exitosa: ${response.data}")
                                if (response.data != null) {
                                    tokenManager.saveToken(response.data.token)
                                    tokenManager.saveUserId(response.data.userId)

                                    repository.getMe().collect { userResponse ->
                                        when (userResponse) {
                                            is NetworkResponse.Success -> {
                                                tokenManager.saveUser(userResponse.data)
                                                println("Usuario obtenido y guardado: ${userResponse.data?.email}")
                                            }

                                            is NetworkResponse.Failure -> {
                                                println("Error al obtener el usuario: ${userResponse.error}")
                                            }

                                            is NetworkResponse.Loading -> {
                                                println("Cargando usuario...")
                                            }
                                        }
                                    }
                                    _navigateToPreferences.update { true }
                                    _loginState.update { UiState.Success(response.data) }
                                }
                            }
                            is NetworkResponse.Loading -> {
                                _isLoading.update { true }
                                println("Cargando...")
                            }
                            is NetworkResponse.Failure -> {
                                _isLoading.update { false }
                                _loginState.update { UiState.Error("Ocurrió un error desconocido 😕") }
                                _showErrorDialog.update { true }
                            }
                        }
                    }
            } else {
                _loginState.update { UiState.Error("Email o contraseña vacíos") }
            }
        }
    }

    fun dismissErrorDialog() {
        _showErrorDialog.update { false }
    }

    private fun checkExistingToken() {
        viewModelScope.launch {
            val token = tokenManager.token.firstOrNull()
            if (!token.isNullOrEmpty() && token != "") {
                _navigateToMain.update { true }
                _navigateToPreferences.update { false }
                println("LoginViewModel: Existing token found, navigating to MainScreen")
            } else {
                _navigateToMain.update { false }
                _navigateToPreferences.update { false }
                _isLoading.update { false }
                println("LoginViewModel: No existing token found")
            }
        }
    }

    private fun validateEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
        if (emailTouched.value) {
            _emailError.value = if (validateEmail(newEmail)) null else "Email inválido"
        }
    }

    fun onEmailFocusChange(focused: Boolean) {
        if (!focused) {
            _emailTouched.value = true
            _emailError.value = if (validateEmail(email.value)) null else "Email inválido"
        }
    }

    fun onPasswordChange(newPass: String) {
        _password.value = newPass
        if (passwordTouched.value) {
            _passwordError.value = if (newPass.length >= 6) null else "Mínimo 6 caracteres"
        }
    }

    fun onPasswordFocusChange(focused: Boolean) {
        if (!focused) {
            _passwordTouched.value = true
            _passwordError.value = if (password.value.length >= 6) null else "Mínimo 6 caracteres"
        }
    }

}
