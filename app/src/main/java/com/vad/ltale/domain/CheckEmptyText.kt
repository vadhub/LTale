package com.vad.ltale.domain

import android.widget.EditText
import androidx.core.widget.doOnTextChanged

class CheckEmptyText {
    companion object {
        fun check(vararg editText: EditText) {
            editText.forEach {
                it.doOnTextChanged { text, start, before, count ->
                    if (text.toString().isEmpty()) {
                        it.error = "must not be empty"
                    }
                }
            }
        }
    }
}