package com.example.proyectofinal.order.data.model

import android.net.Uri
import kotlinx.serialization.Serializable
import java.io.File
import java.util.Date

data class Order(
    val id: Int,
    val name: String,
    val description: String,
    val status : String,
    val creationdate : String,
    val limitdate : Date,
    val createdbyuserid : String,
    val filePath : String,
    val assigneduserid : String ,
    val isFavorite: Boolean,
    val lastRead: String,
    val pageCount: Int,
    val hasComments: Boolean,
    val file : Uri
)