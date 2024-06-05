package com.vad.ltale.presentation.auth

import android.util.Patterns

class Validator {
    companion object {
        fun emailValidator(emailText: String): Boolean {
            return emailText.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(emailText).matches()
        }
    }
}