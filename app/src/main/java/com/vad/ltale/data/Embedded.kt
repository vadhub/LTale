package com.vad.ltale.data

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

data class Embedded(
    @SerializedName("users")
    @Expose
    val employees: List<User>
    )