package com.example.mydiary.services

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import com.example.mydiary.R

class AudioService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private val audioTracks = listOf(
        R.raw.brown_noise
    )
    private var currentIndex = -1

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        playRandomTrack()
        return START_STICKY // Mantiene il servizio attivo
    }

    private fun playRandomTrack() {
        currentIndex = (audioTracks.indices - currentIndex).random()
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(this, audioTracks[currentIndex])
        mediaPlayer?.setOnCompletionListener {
            playRandomTrack() // Passa al successivo casuale
        }
        mediaPlayer?.start()
        isRunning = true
    }



    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
        isRunning = false
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        var isRunning = false
    }
}
