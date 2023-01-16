package com.vad.ltale.data.remote

import com.vad.ltale.data.FileResponse

data class Post(val date: String, val pathImage: String, val audios: List<FileResponse>)