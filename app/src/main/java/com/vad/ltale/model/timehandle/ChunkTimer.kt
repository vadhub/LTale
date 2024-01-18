package com.vad.ltale.model.timehandle

import android.os.CountDownTimer
import android.util.Log

class ChunkTimer(time: Long) {

    private lateinit var handler: TimerHandler
    private var timer: CountDownTimer? = null
    private var timeStartFrom = time
    private var timeLast = 0L
    private val interval = 1000L

    // set time listener
    fun setTimerHandler(handler: TimerHandler) {
        this.handler = handler
    }

    fun setTimeStartFrom(time: Long) {
        timeLast = 0
        timeStartFrom = time
    }

    fun startTimer() {
        timeLast = 0
        timer = object: CountDownTimer(timeStartFrom, interval) {
            override fun onTick(millisUntilFinished: Long) {
                timeStartFrom = millisUntilFinished
                handler.showTime(millisUntilFinished)
                timeLast += interval
                Log.d("#ChunkTimer", "timeStartFrom: $timeStartFrom, timeLast: $timeLast")
            }

            override fun onFinish() {
                handler.finishTime()

            }
        }.start()
    }

    // cancel timer
    // timeStartFrom - 1000, fix delay from touch
    fun cancelTimer(): Long {
        timeStartFrom -= 1000
        timer?.cancel()
        timer = null
        return timeLast
    }

}