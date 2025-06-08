package com.example.proyectofinal.mail.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.proyectofinal.mail.data.entity.MessageEntity

@Dao
interface MessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMessage(message: MessageEntity)

    // Último mensaje enviado (que no sea borrador)
    @Query("SELECT * FROM message WHERE isDraft = 0 ORDER BY date DESC LIMIT 1")
    fun getLastMessage(): MessageEntity?

    // Mensajes en bandeja de entrada
    @Query("SELECT * FROM message WHERE userToID = :currentUserId ORDER BY date DESC")
    fun getInboxMessages(currentUserId: Int): List<MessageEntity>

    // Mensajes en bandeja de salida
    @Query("SELECT * FROM message WHERE userFromId = :currentUserId ORDER BY date DESC")
    fun getOutboxMessages(currentUserId: Int): List<MessageEntity>

    // Guardar borrador
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveDraft(draft: MessageEntity)

    //Actualizamos el borrador
    @Update
    suspend fun updateDraft(draft: MessageEntity)

    // Traer todos los borradores
    @Query("SELECT * FROM message WHERE isDraft = 1 ORDER BY date DESC")
    fun getDrafts(): List<MessageEntity>

    //Traer borrador por ID
    @Query("SELECT * FROM message WHERE isDraft = 1 AND id = :idMessage")
    fun getDraftById(idMessage: Int): MessageEntity

    // Eliminar borrador por ID
    @Query("DELETE FROM message WHERE isDraft = 1 AND id = :idMessage")
    fun deleteDraftById(idMessage: Int)
}
