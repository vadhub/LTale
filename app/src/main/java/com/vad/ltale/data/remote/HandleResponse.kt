package com.vad.ltale.data.remote

interface HandleResponse {
    fun error(e: String)
    fun success()
}