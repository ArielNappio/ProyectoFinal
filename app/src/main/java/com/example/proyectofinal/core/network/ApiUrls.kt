package com.example.proyectofinal.core.network

object ApiUrls {
    const val BASE_URL =  "http://192.168.73.2:5072/"
    const val BASE_URL_LOCAL = "http://localhost:5072/"
    const val AUTH_ME = "${BASE_URL}api/User/me"
    const val LOGIN = "${BASE_URL}api/Auth/login"
    const val REGISTER = "${BASE_URL}api/Auth/register"
    const val ORDERS = "${BASE_URL}api/Order"
    const val ORDER = "${BASE_URL}api/Order/{id}"
    const val TASKS = "${BASE_URL}api/Task"
    const val TASK = "${BASE_URL}api/Task/{id}"
    const val MESSAGES = "${BASE_URL}api/Message"
    const val MESSAGE = "${BASE_URL}api/Message/{id}"
}