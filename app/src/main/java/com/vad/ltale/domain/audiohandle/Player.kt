package com.vad.ltale.domain.audiohandle

import android.content.Context
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

class Player(context: Context) {

    var mp: ExoPlayer = ExoPlayer.Builder(context).build()

    var isPlay = false

    fun play(uri: String) {

        if (uri.isEmpty()) {
            Log.d("##Player", "uri empty")
            return
        }

        mp.setMediaItem(MediaItem.fromUri(uri))
        mp.prepare()
        mp.play()
        isPlay = mp.isPlaying

        Log.d("##Player", "play $isPlay $uri")
    }

    fun stop() {
        mp.pause()
        isPlay = mp.isPlaying
        Log.d("##Player", "stop"+isPlay)
    }

    fun cancel() {
        if (mp.isPlaying)
            mp.stop()
        Log.d("##Player", "cancel")
    }

}