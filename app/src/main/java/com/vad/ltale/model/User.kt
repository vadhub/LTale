package com.vad.ltale.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class User(

    @SerializedName("id")
    @Expose
    val userId: Long,

    @SerializedName("username")
    @Expose
    val username: String,

    @SerializedName("email")
    @Expose
    val email: String,

    @SerializedName("password")
    @Expose
    val password: String
)