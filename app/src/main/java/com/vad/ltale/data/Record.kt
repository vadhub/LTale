package com.vad.ltale.data

import java.net.URI
import java.util.Date

data class Record(
    val id: Long,
    val title: String,
    val uri: URI,
    val path: String,
    val date: Date,
    val duration: Long
    )
