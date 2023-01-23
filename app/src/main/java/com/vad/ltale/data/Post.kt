package com.vad.ltale.data

import java.sql.Date


data class Post(
    val image: Image, val date: Date, val audio: List<Audio>
    )