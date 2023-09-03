package com.vad.ltale.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Image(
    @SerializedName("idImage")
    @Expose
    val id: Long,

    @SerializedName("imageUri")
    @Expose
    val uri: String,

    @SerializedName("dateCreated")
    @Expose
    val dateCreated: String,

    @SerializedName("dateChanged")
    @Expose
    val dateChanged: String
)