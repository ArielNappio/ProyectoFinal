package com.example.proyectofinal.core.network

object ApiUrls {
    const val BASE_URL =  "http://10.0.2.2:5072/"
    const val BASE_URL_LOCAL = "http://localhost:5072/"
    const val GET_ME = "${BASE_URL}api/User/{id}"
    const val LOGIN = "${BASE_URL}api/Auth/login"
    const val REGISTER = "${BASE_URL}api/Auth/register"
    const val ORDERS = "${BASE_URL}api/Order"
    const val ORDER = "${BASE_URL}api/Order/{id}"
    const val TASKS = "${BASE_URL}api/Task"
    const val TASK = "${BASE_URL}api/Task/{id}"
    const val USER = "${BASE_URL}api/User"

    const val MESSAGE = "${BASE_URL}api/Message/{id}"
    const val EXAMPLE_PDF_URL = "https://research.cocos.capital/informe%20semanal%2026%20MAYO%201.pdf?lid=59pi4z1jojfp" // TODO replace with actual URL
    const val MESSAGES = "${BASE_URL}api/Message/byUserId/{userId}"
    const val UPDATE_MESSAGE = "${BASE_URL}api/Message/updateMessage"
    const val SEND_MESSAGE = "${BASE_URL}api/Message"
    const val ORDER_MANAGMENT = "${BASE_URL}api/OrderDelivery/WithOrders/{studentId}"
}