package com.example.myapplication2

import android.content.Context
import android.media.MediaPlayer

object AudioPlayer {

    private var mediaPlayer: MediaPlayer? = null

    fun init(context: Context, resourceId: Int) {
        mediaPlayer = MediaPlayer.create(context, resourceId)
        mediaPlayer?.isLooping = true
    }

    fun start() {
        mediaPlayer?.start()
    }

    fun stop() {
        mediaPlayer?.stop()
    }

    fun mute() {
        mediaPlayer?.setVolume(0f, 0f)
    }

    fun unmute() {
        mediaPlayer?.setVolume(1f, 1f)
    }

    fun isInitialized(): Boolean {
        return mediaPlayer != null
    }

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
