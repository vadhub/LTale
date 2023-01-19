package com.vad.ltale.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Links(
    @SerializedName("audio")
    @Expose
    val audio: Audio,

    @SerializedName("image")
    @Expose
    val image: Image
)