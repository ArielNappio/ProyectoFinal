package com.example.proyectofinal.users.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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


    internal fun fetchUsers() {
        viewModelScope.launch {
            getUserUserCase().collect {
                when (it) {
                    is NetworkResponse.Failure<*> -> _users.value = it.data ?: emptyList()
                    is NetworkResponse.Loading<*> -> Log.e("OrderViewModel", "Error: ${it.error}")
                    is NetworkResponse.Success<*> -> Log.d("OrderViewModel", "Cargando...")
                }
            }
        }
    }



    fun deleteOrder(id: Int) {
        viewModelScope.launch {
            deleteUserUseCase(id).collect {
                fetchUsers()
            }
        }
    }



}


