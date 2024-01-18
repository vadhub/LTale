package com.vad.ltale.model.pojo

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

data class Embedded(
    @SerializedName("users")
    @Expose
    val users: List<User>,

    @SerializedName("posts")
    @Expose
    val messages: List<PostResponse>
    )