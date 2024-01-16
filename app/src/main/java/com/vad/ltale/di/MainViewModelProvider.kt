package com.vad.ltale.di

import com.vad.ltale.presentation.MainViewModel

interface MainViewModelProvider {
    fun get(): MainViewModel
}