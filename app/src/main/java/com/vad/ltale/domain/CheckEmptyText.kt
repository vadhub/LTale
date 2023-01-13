package com.vad.ltale.domain

import android.widget.EditText
import androidx.core.widget.doOnTextChanged

class CheckEmptyText {
    companion object {
        fun check(vararg editText: EditText, runnable: Runnable) {
            editText.forEach {
                if (it.text.isEmpty()) {
                    it.error = "empty text"
                } else {
                    runnable.run()
                }
            }
        }
    }
}