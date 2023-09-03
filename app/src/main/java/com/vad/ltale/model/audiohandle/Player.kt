package com.vad.ltale.model.audiohandle

import android.content.Context
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

class Player(context: Context) {

    var mp: ExoPlayer = ExoPlayer.Builder(context).build()


    fun play(uri: String) {

        if (uri.isEmpty()) {
            Log.d("##Player", "uri empty")
            return
        }

        mp.setMediaItem(MediaItem.fromUri(uri))
        mp.prepare()
        mp.playWhenReady = true

        Log.d("##Player", "play $uri")
    }

    fun stop() {
        mp.playWhenReady = false
        Log.d("##Player", "stop")
    }

    fun cancel() {
        if (mp.isPlaying)
            mp.stop()
        Log.d("##Player", "cancel")
    }

}