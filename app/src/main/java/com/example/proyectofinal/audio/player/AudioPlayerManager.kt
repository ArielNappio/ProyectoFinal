package com.example.proyectofinal.audio.player

import android.media.MediaPlayer

class AudioPlayerManager {

    private var mediaPlayer: MediaPlayer? = null
    private var currentPath: String? = null

    fun play(filePath: String, onComplete: (() -> Unit)? = null) {
        // Si ya está reproduciendo ese mismo audio, lo frenamos
        if (mediaPlayer?.isPlaying == true && currentPath == filePath) {
            stop()
            return
        }

        // Si suena otro, lo paramos
        stop()

        mediaPlayer = MediaPlayer().apply {
            currentPath = filePath
            setDataSource(filePath)
            setOnPreparedListener { it.start() }
            setOnCompletionListener {
                stop()
                onComplete?.invoke()
            }
            setOnErrorListener { _, _, _ ->
                stop()
                true
            }
            prepareAsync()
        }
    }

    fun stop() {
        mediaPlayer?.apply {
            if (isPlaying) stop()
            release()
        }
        mediaPlayer = null
        currentPath = null
    }

    fun isPlaying(path: String? = null): Boolean {
        return mediaPlayer?.isPlaying == true && (path == null || currentPath == path)
    }

    fun pause() {
        mediaPlayer?.takeIf { it.isPlaying }?.pause()
    }

    fun resume() {
        mediaPlayer?.takeIf { !it.isPlaying }?.start()
    }

    fun getCurrentPath(): String? = currentPath

    fun getCurrentPosition(): Long {
        return mediaPlayer?.currentPosition?.toLong() ?: 0L
    }

    fun getDuration(): Long {
        return mediaPlayer?.duration?.toLong() ?: 1L
    }

}