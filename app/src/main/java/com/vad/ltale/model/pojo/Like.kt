package com.vad.ltale.model.pojo

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