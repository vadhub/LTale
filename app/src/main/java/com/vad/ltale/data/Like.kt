package com.vad.ltale.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Like(
    @SerializedName("userId")
    @Expose
    val idUser: Long,

    @SerializedName("postId")
    @Expose
    val idPost: Long
    )