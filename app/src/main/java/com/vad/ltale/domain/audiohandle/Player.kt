package com.vad.ltale.domain.audiohandle

import android.media.MediaPlayer
import android.util.Log

class Player() {

    lateinit var mp: MediaPlayer
    var isPlay = false

    fun play(uri: String) {

        if (uri.isEmpty()) {
            Log.d("##Player", "uri empty")
            return
        }

        mp = MediaPlayer()
        mp.setDataSource(uri)
        mp.prepare()
        mp.start()
        isPlay = mp.isPlaying

        Log.d("##Player", "play $isPlay $uri")
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