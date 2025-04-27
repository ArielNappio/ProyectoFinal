package com.example.proyectofinal.data.remoteData.repository

object ApiUrls {
    const val BASE_URL = "http://192.168.100.79:5072/"
    const val BASE_URL_LOCAL = "http://localhost:5072/"
    const val AUTH_ME = "${BASE_URL}api/User/me"
    const val TEST = "${BASE_URL}WeatherForecast"
    const val LOGIN = "${BASE_URL}api/Auth/login"
    const val REGISTER = "${BASE_URL}api/Auth/register"
}