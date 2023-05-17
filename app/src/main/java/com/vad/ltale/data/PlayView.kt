package com.vad.ltale.data

import android.widget.ProgressBar
import android.widget.SeekBar
import com.vad.ltale.presentation.adapter.AudioAdapter

data class PlayView(
    val position: Int,
    val audio: Audio,
    val audioAdapter: AudioAdapter,
    val seekBar: SeekBar,
    val progressBar: ProgressBar
    )
