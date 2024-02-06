package com.vad.ltale.presentation.auth.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.vad.ltale.R
import com.vad.ltale.databinding.FragmentLoginBinding
import com.vad.ltale.presentation.auth.AuthBaseFragment

class LoginFragment : AuthBaseFragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val buttonLogin: Button = binding.loginButton
        val username = binding.usernameLoginEditText
        val password = binding.passwordLoginEditText

        buttonLogin.setOnClickListener {

            if (username.text.isNullOrBlank()) {
                username.error = getString(R.string.field_empty)
                Toast.makeText(thisContext, getString(R.string.enter_username), Toast.LENGTH_SHORT).show()
            } else if (password.text.isNullOrBlank()) {
                password.error = getString(R.string.field_empty)
                Toast.makeText(thisContext, getString(R.string.enter_password), Toast.LENGTH_SHORT).show()
            } else {
                qwrt = password.text.toString().trim()
                authViewModel.login(username.text.toString().trim(), qwrt)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}