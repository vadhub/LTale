package com.vad.ltale.presentation.animation

import android.os.CountDownTimer
import android.util.Log
import android.view.animation.OvershootInterpolator
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.vad.ltale.model.audiohandle.Recorder
import kotlin.math.min

class AnimationButton(private val recorder: Recorder, private val interpolator: OvershootInterpolator) {

    private var countDownTimer: CountDownTimer? = null

    private lateinit var button: FloatingActionButton

    companion object {
        private const val MAX_RECORD_AMPLITUDE = 32768.0
        private const val VOLUME_UPDATE_DURATION = 100L
    }

    fun startCaptureAudioVolume(button: FloatingActionButton) {

        this.button = button

        countDownTimer = object : CountDownTimer(60_000, 100) {
            override fun onTick(p0: Long) {
                val volume = recorder.getVolume()
                Log.d("$@@ddd", "Volume = $volume")
                handleVolume(volume, button)
            }

            override fun onFinish() {
            }
        }.apply {
            start()
        }
    }

    fun stopCapture() {
        countDownTimer?.cancel()
        countDownTimer = null
        button.animate()
            .scaleX(1F)
            .scaleY(1F)

    }

    private fun handleVolume(volume: Int, button: FloatingActionButton) {
        val scale = min(8.0, volume / MAX_RECORD_AMPLITUDE + 1.0).toFloat()
        Log.d("$@ddd", "Scale = $scale")

        button.animate()
            .scaleX(scale)
            .scaleY(scale)
            .setInterpolator(interpolator)
            .duration= VOLUME_UPDATE_DURATION
    }
}