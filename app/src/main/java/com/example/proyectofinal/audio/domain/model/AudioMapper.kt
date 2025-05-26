package com.example.proyectofinal.audio.domain.model

import com.example.proyectofinal.audio.data.model.RecordedAudioEntity

fun RecordedAudioEntity.toDomain(): RecordedAudio {
    return RecordedAudio(
        id = id,
        filePath = filePath,
        timestamp = timestamp,
        associatedTaskId = associatedTaskId,
        title = title,
        page = page,
        date = date
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
        date = date
    )
}