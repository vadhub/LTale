package com.vad.ltale.model.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.vad.ltale.model.pojo.Embedded

data class Main(
    @SerializedName("_embedded")
    @Expose
    val embedded: Embedded
)