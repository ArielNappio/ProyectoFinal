package com.example.proyectofinal.core.network

object ApiUrls {
    const val BASE_URL =  "http://192.168.71.3:5072/"
    const val BASE_URL_LOCAL = "http://localhost:5072/"
    const val AUTH_ME = "${BASE_URL}api/User/me"
    const val LOGIN = "${BASE_URL}api/Auth/login"
    const val REGISTER = "${BASE_URL}api/Auth/register"
    const val ORDERS = "${BASE_URL}api/Order"
    const val ORDER = "${BASE_URL}api/Order/{id}"
}