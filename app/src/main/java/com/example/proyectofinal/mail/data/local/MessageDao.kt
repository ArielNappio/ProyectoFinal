package com.example.proyectofinal.mail.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.proyectofinal.mail.data.entity.MessageEntity

@Dao
interface MessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    // Último mensaje enviado (que no sea borrador)
    @Query("SELECT * FROM message WHERE isDraft = 0 ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastMessage(): MessageEntity?

    // Mensajes en bandeja de entrada
    @Query("SELECT * FROM message WHERE userToID = :currentUserId ORDER BY timestamp DESC")
    suspend fun getInboxMessages(currentUserId: Int): List<MessageEntity>

    // Mensajes en bandeja de salida
    @Query("SELECT * FROM message WHERE userFromId = :currentUserId ORDER BY timestamp DESC")
    suspend fun getOutboxMessages(currentUserId: Int): List<MessageEntity>

    // Guardar borrador
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveDraft(draft: MessageEntity): Long

    // Traer todos los borradores
    @Query("SELECT * FROM message WHERE isDraft = 1 ORDER BY timestamp DESC")
    suspend fun getDrafts(): List<MessageEntity>

    // Eliminar todos los borradores
    @Query("DELETE FROM message WHERE isDraft = 1")
    suspend fun deleteDrafts()
}
