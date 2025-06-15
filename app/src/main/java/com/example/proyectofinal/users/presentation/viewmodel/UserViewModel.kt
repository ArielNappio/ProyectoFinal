package com.example.proyectofinal.users.presentation.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.auth.di.repositoryModule
import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.users.data.model.User
import com.example.proyectofinal.users.domain.provider.usecase.DeleteUserUseCase
import com.example.proyectofinal.users.domain.provider.usecase.GetUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel (
    private val getUserUserCase : GetUserUseCase,
    private val deleteUserUseCase:DeleteUserUseCase
) : ViewModel() {


    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users


    private val _id = MutableStateFlow("")
    val id: StateFlow<String> = _id.asStateFlow()


    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName.asStateFlow()


    private val _fullName = MutableStateFlow("")
    val fullName: StateFlow<String> = _fullName.asStateFlow()


    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()


    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()


    private val _phoneNumber= MutableStateFlow("")
    val phoneNumber: StateFlow<String> = _phoneNumber.asStateFlow()

    private val _dni= MutableStateFlow("")
    val dni: StateFlow<String> = _dni.asStateFlow()



    private val _roles = MutableStateFlow<List<String>>(emptyList())
    val roles: MutableStateFlow<List<String>> = _roles





    private val _selectedUser = MutableStateFlow (User(
        userName = "",
        id = "",
        fullName = "",
        email = "",
        password = "",
        phoneNumber = "",
        roles = mutableListOf("")
    ))
    val selectedUser: StateFlow<User> = _selectedUser.asStateFlow()



    fun selectUser(user: User) {
        updateId(user.id)
        updateUserName(user.userName)
        updateFullName(user.fullName)
        updateEmail(user.email)
        updatePhoneNumber(user.phoneNumber)
        updateRoles(user.roles)

        User(
            id = id.value,
            userName = userName.value,
            fullName = fullName.value,
            email = email.value,
            password = password.value,
            phoneNumber = phoneNumber.value,
            roles = roles.value
        )

       }



    fun updateUsers(newUsers: List<User>) {
        _users.value = newUsers
    }

    fun updateId(newId: String) {
        _id.value = newId
    }

    fun updateUserName(newUserName: String) {
        _userName.value = newUserName
    }

    fun updateFullName(newFullName: String) {
        _fullName.value = newFullName
    }

    fun updateEmail(newEmail: String) {
        _email.value = newEmail
    }

    fun updatePassword(newPassword: String) {
        _password.value = newPassword
    }

    fun updatePhoneNumber(newPhoneNumber: String) {
        _phoneNumber.value = newPhoneNumber
    }

    fun updateDni(newDni: String) {
        _dni.value = newDni
    }

    fun updateRoles(newRoles: List<String>) {
        _roles.value = newRoles
    }



    fun changedUser(){
    }




    init {
        fetchUsers()
}


    fun fetchUsers() {
        viewModelScope.launch {
            getUserUserCase().collect { response ->
                when (response) {
                    is NetworkResponse.Loading -> {
                        Log.d("UserViewModel", "Cargando usuarios...")
                        // Opcional: podrías manejar un estado loading para la UI
                    }
                    is NetworkResponse.Success -> {
                        val usersList = response.data ?: emptyList()
                        _users.value = usersList
                        Log.d("UserViewModel", "Usuarios cargados: ${usersList.size}")
                    }
                    is NetworkResponse.Failure -> {
                        Log.e("UserViewModel", "Error al cargar usuarios: ${response.error}")
                        _users.value = emptyList() // Opcional: limpiar lista si hubo error
                        // También podrías manejar un estado de error para mostrar mensaje en UI
                    }
                }
            }
        }
    }




    fun deleteUser(id: String) {
        viewModelScope.launch {
            deleteUserUseCase(id).collect { response ->
                when (response) {
                    is NetworkResponse.Loading -> {
                        Log.d("UserViewModel", "Eliminando usuario...")
                        // Aquí podrías manejar un estado de loading si querés mostrarlo en la UI
                    }
                    is NetworkResponse.Success -> {
                        Log.d("UserViewModel", "Usuario eliminado correctamente")
                        fetchUsers() // Recargar la lista después de eliminar
                    }
                    is NetworkResponse.Failure -> {
                        Log.e("UserViewModel", "Error al eliminar usuario: ${response.error}")
                        // Manejo de error: mostrar mensaje, etc.
                    }
                }
            }
        }
    }

}


