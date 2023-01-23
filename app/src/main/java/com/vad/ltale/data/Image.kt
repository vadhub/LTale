package com.vad.ltale.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Image(
    @SerializedName("idImage")
    @Expose
    val id: Int,

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