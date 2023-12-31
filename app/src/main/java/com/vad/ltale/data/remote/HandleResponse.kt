package com.vad.ltale.data.remote

interface HandleResponse<T> {
    fun error(e: String)
    fun success(t: T)
}