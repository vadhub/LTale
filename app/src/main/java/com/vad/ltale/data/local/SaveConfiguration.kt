package com.vad.ltale.data.local

import android.content.Context
import android.content.SharedPreferences

class SaveConfiguration(private val context: Context) {
    private lateinit var pref: SharedPreferences

    fun clear() {
        context.getSharedPreferences("lil_tale_pass", Context.MODE_PRIVATE).edit().clear().apply()
        context.getSharedPreferences("lil_tale_login", Context.MODE_PRIVATE).edit().clear().apply()
        context.getSharedPreferences("lil_tale_id", Context.MODE_PRIVATE).edit().clear().apply()
    }

    fun saveIdUser(id: Long) {
        pref = context.getSharedPreferences("lil_tale_id", Context.MODE_PRIVATE)
        val ed: SharedPreferences.Editor = pref.edit()
        ed.putLong("id", id)
        ed.apply()
    }

    fun savePass(pass: String?) {
        pref = context.getSharedPreferences("lil_tale_pass", Context.MODE_PRIVATE)
        val ed: SharedPreferences.Editor = pref.edit()
        ed.putString("pass", pass)
        ed.apply()
    }

    fun saveLogin(login: String) {
        pref = context.getSharedPreferences("lil_tale_login", Context.MODE_PRIVATE)
        val ed: SharedPreferences.Editor = pref.edit()
        ed.putString("login", login)
        ed.apply()
    }

    fun saveFirstStart(isFirst: Boolean) {
        pref = context.getSharedPreferences("lil_tale_first_run", Context.MODE_PRIVATE)
        val ed: SharedPreferences.Editor = pref.edit()
        ed.putBoolean("first_run", isFirst)
        ed.apply()
    }

    fun getIdUser(): Long {
        pref = context.getSharedPreferences("lil_tale_id", Context.MODE_PRIVATE)
        return pref.getLong("id", -1)
    }

    fun getPass(): String {
        pref = context.getSharedPreferences("lil_tale_pass", Context.MODE_PRIVATE)
        return pref.getString("pass", "") ?: ""
    }

    fun getLogin(): String {
        pref = context.getSharedPreferences("lil_tale_login", Context.MODE_PRIVATE)
        return pref.getString("login", "") ?: ""
    }

    fun getFirstStart(): Boolean {
        pref = context.getSharedPreferences("lil_tale_first_run", Context.MODE_PRIVATE)
        return pref.getBoolean("first_run", false)
    }
}