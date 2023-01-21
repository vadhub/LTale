package com.vad.ltale.domain

import android.media.MediaPlayer

class PlayAudioHandle {

    private var player: MediaPlayer? = null
    private var play = false

    fun initializePlayer(source: String) {
        player = MediaPlayer().also {
            it.setDataSource(source)
        }
    }

    fun playAudio() {
        try {
            player?.prepare()
            player?.start()
            play = true
        } catch (e: java.lang.Exception) {
            println(e)
        }
    }

    fun stopPlaying() {
        player?.release()
        play = false
    }
}