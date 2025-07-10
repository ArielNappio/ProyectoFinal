package com.example.proyectofinal.audio.util

import com.example.proyectofinal.audio.data.model.RecordedAudioEntity
import com.example.proyectofinal.audio.domain.model.RecordedAudio

fun RecordedAudioEntity.toDomain(): RecordedAudio {
    return RecordedAudio(
        id = id,
        filePath = filePath,
        timestamp = timestamp,
        associatedTaskId = associatedTaskId,
        title = title,
        page = page,
        date = date,
        duration = duration
    )
}

fun RecordedAudio.toEntity(): RecordedAudioEntity {
    return RecordedAudioEntity(
        id = id,
        filePath = filePath,
        timestamp = timestamp,
        associatedTaskId = associatedTaskId,
        title = title,
        page = page,
        date = date,
        duration = duration
    )
}