package com.vad.ltale.domain.audiohandle

import android.media.MediaPlayer
import android.util.Log

class Player() {

    lateinit var mp: MediaPlayer
    var isPlay = false

    fun play(uri: String) {
        mp = MediaPlayer().also {
            it.setDataSource(uri)
            it.start()
            isPlay = it.isPlaying
        }

        Log.d("##Player", "play")
    }

    fun stop() {
        mp.reset()
        isPlay = mp.isPlaying
        Log.d("##Player", "stop")
    }

    fun cancel() {
        if (mp.isPlaying)
            mp.stop()
        Log.d("##Player", "cancel")
    }

}