package com.example.proyectofinal.core.network

sealed class
NetworkResponse<T>(
    val data: T? = null,
    val error: String? = null
){

    class Success<T>(data: T?): NetworkResponse<T>(data)

    class Failure<T>(error: String?): NetworkResponse<T>(error = error)

    class Loading<T>: NetworkResponse<T>()
}