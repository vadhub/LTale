package com.vad.ltale.presentation.adapter

interface AccountClickListener {
    fun onClick(id: Long)

    class EmptyAccountClickListener : AccountClickListener {
        override fun onClick(id: Long) {}
    }
}
