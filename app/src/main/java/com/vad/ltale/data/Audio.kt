package com.vad.ltale.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Audio(

    @SerializedName("href")
    @Expose
    val href: String
)
