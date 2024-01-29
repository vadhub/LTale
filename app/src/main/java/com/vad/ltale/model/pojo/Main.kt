package com.vad.ltale.model.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Main(
    @SerializedName("_embedded")
    @Expose
    val embedded: Embedded
)