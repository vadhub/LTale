package com.vad.ltale.data

import java.util.concurrent.TimeUnit

class TimeFormatter {
    companion object {
        fun format(time: Long) =
            String.format(
                "%02d : %02d",
                TimeUnit.MILLISECONDS.toMinutes(time),
                TimeUnit.MILLISECONDS.toSeconds(time) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time))
            )
    }
}