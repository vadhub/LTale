package com.vad.ltale.domain

import android.content.Context
import android.content.SharedPreferences

class SaveDataPref(private val context: Context) {
    private lateinit var preferences: SharedPreferences

    fun saveId(id: Int) {
        preferences = context.getSharedPreferences("save_id_user_ltale", Context.MODE_PRIVATE)
        val ed: SharedPreferences.Editor = preferences.edit()
        ed.putInt("id_user", id)
        ed.apply()
    }

    fun getId(): Int {
        preferences = context.getSharedPreferences("save_id_user_ltale", Context.MODE_PRIVATE)
        return preferences.getInt("id_user", -1)
    }
}