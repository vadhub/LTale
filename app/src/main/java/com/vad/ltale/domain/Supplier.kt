package com.vad.ltale.domain

interface Supplier<T> {
    fun get(): T
}