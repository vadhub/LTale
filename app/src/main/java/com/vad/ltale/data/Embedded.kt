package com.vad.ltale.data

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

data class Embedded(
    @SerializedName("employees")
    @Expose
    val employees: List<Employee>
    )