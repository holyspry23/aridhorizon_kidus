package com.example.aridhorizon

import android.content.Context
import android.media.MediaPlayer

class SoundManager(private val context: Context) {
    companion object {
        private var mediaPlayer: MediaPlayer? = null
        private var isMuted: Boolean = false  // Track if sound is muted
    }

    fun playBackgroundMusic() {
        if (isMuted) return  // If sound is muted, don't play

        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.lomi_music).apply {
                isLooping = true
                start()
            }
        } else if (mediaPlayer?.isPlaying == false) {
            mediaPlayer?.start()
        }
    }

    fun stopBackgroundMusic() {
        mediaPlayer?.apply {
            stop()
            release()  // Release resources completely
        }
        mediaPlayer = null  // Ensure it's properly discarded
    }

    fun pauseBackgroundMusic() {
        mediaPlayer?.apply {
            if (isPlaying) pause()
        }
    }

    fun resumeBackgroundMusic() {
        if (!isMuted) {
            mediaPlayer?.apply {
                if (!isPlaying) start()
            }
        }
    }

    fun toggleSound(): Boolean {
        isMuted = !isMuted  // Toggle mute state

        if (isMuted) {
            pauseBackgroundMusic()  // Pause if muted
        } else {
            playBackgroundMusic()  // Resume if unmuted
        }

        return isMuted
    }

    fun isSoundMuted(): Boolean {
        return isMuted
    }
}
