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
import kotlinx.coroutines.launch

class UserViewModel (
    private val GetUserUserCase : GetUserUseCase,
    private val DeleteUserUseCase:DeleteUserUseCase
) : ViewModel() {


    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    init {
        fetchUsers()
    }


    internal fun fetchUsers() {
        viewModelScope.launch {
            GetUserUserCase().collect {
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
            DeleteUserUseCase(id).collect {
                fetchUsers()
            }
        }
    }



}


