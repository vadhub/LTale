package com.vad.ltale

import android.app.Application
import com.vad.ltale.data.local.AppDatabase

class App : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
}