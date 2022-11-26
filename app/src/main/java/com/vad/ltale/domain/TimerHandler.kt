package com.vad.ltale.domain

interface TimerHandler {
    fun showTime(time: Long)
    fun finishTime()
}