package com.vad.ltale.model

import android.graphics.drawable.Drawable
import android.util.Log

class CacheIcon {

    private val iconMap: MutableMap<Long, Drawable> = HashMap()

    fun getImage(userId: Long): Drawable? {
        return try {
            iconMap.getValue(userId)
        } catch (e: NoSuchElementException) {
            null
        }
    }

    fun setIcon(userId: Long, icon: Drawable) {
        iconMap.set(userId, icon)
    }

    fun printIcons() {
        Log.d("#printIcons", "\n       " )
        iconMap.forEach {
            Log.d("#printIcons", "${it.key} ${it.value}" )
        }
    }

}