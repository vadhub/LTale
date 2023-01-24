package com.vad.ltale.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Audio(
    @SerializedName("id")
    @Expose
    val id: Int = -1,

    @SerializedName("uri")
    @Expose
    val uri: String = "",

    @SerializedName("duration")
    @Expose
    val duration: Long
    )
