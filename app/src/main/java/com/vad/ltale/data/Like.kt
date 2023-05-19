package com.vad.ltale.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Like(
    @SerializedName("userId")
    @Expose
    val idUser: Int,

    @SerializedName("postId")
    @Expose
    val idPost: Int
    )