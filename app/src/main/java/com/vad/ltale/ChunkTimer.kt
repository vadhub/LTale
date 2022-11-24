package com.vad.ltale

import android.os.CountDownTimer

class ChunkTimer(time: Long) {

    private lateinit var handler: TimerHandler
    private lateinit var timer: CountDownTimer
    private var timeStartFrom = time

    fun setTimerHandler(handler: TimerHandler) {
        this.handler = handler
    }

    fun startTimer() {
        timer = object: CountDownTimer(timeStartFrom, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeStartFrom = millisUntilFinished
                handler.showTime(millisUntilFinished)
            }

            override fun onFinish() {
                handler.finishTime()
            }
        }.start()
    }

    fun cancelTimer() {
        timer.cancel()
    }

}