package com.vad.ltale.domain

import android.media.AudioAttributes
import android.media.MediaPlayer


class PlayAudioHandle {

    private var mediaPlayer = MediaPlayer().apply {
        setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()
        )
    }

    fun setAudioSource(audioSource: String): Int {
        //mediaPlayer.setDataSource(audioSource)
        return 9//mediaPlayer.duration
    }

    fun playAudio() {
        mediaPlayer.prepare()
        mediaPlayer.start()
    }

    fun stopPlaying() {
        mediaPlayer.release()
    }
}