package com.vad.ltale.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Message(

    @SerializedName("titleMessage")
    @Expose
    val title: String,

    @SerializedName("uri")
    @Expose
    val uri: String,

    @SerializedName("user")
    @Expose
    val id: Int
    )