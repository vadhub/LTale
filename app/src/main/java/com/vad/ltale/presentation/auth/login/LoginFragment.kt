package com.vad.ltale.presentation.auth.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.google.android.material.textfield.TextInputEditText
import com.vad.ltale.R
import com.vad.ltale.data.remote.RemoteInstance
import com.vad.ltale.data.repository.UserRepository
import com.vad.ltale.presentation.AuthViewModel
import com.vad.ltale.presentation.AuthViewModelFactory
import com.vad.ltale.presentation.auth.AuthBaseFragment

class LoginFragment : AuthBaseFragment() {

    private lateinit var username: TextInputEditText
    private lateinit var password: TextInputEditText
    private val viewModel: AuthViewModel by activityViewModels {
        AuthViewModelFactory(UserRepository(RemoteInstance), this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val buttonLogin: Button = view.findViewById(R.id.loginButton)
        username = view.findViewById(R.id.usernameLoginEditText) as TextInputEditText
        password = view.findViewById(R.id.passwordLoginEditText) as TextInputEditText

        buttonLogin.setOnClickListener {

            if (username.text.isNullOrBlank()) {
                Toast.makeText(thisContext, getString(R.string.enter_username), Toast.LENGTH_SHORT).show()
            } else if (password.text.isNullOrBlank()) {
                Toast.makeText(thisContext, getString(R.string.password), Toast.LENGTH_SHORT).show()
            } else {
                qwrt = password.text.toString().trim()
                viewModel.login(username.text.toString().trim(), qwrt)
            }
        }
    }

}