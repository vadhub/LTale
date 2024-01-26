package com.vad.ltale.presentation.auth.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.vad.ltale.R
import com.vad.ltale.model.pojo.User
import com.vad.ltale.presentation.auth.AuthBaseFragment

class RegistrationFragment : AuthBaseFragment(){

    private lateinit var username: TextInputEditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_registration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val buttonRegistration: Button = view.findViewById(R.id.enterRegistrationButton)
        val buttonLogin: Button = view.findViewById(R.id.loginRegistrationButton)
        username = view.findViewById(R.id.nikEditText) as TextInputEditText
        val email = view.findViewById(R.id.emailEditText) as TextInputEditText
        val password = view.findViewById(R.id.passwordEditText) as TextInputEditText

        buttonRegistration.setOnClickListener {

            if (username.text.isNullOrBlank()) {
                username.error = getString(R.string.field_empty)
                Toast.makeText(thisContext, getString(R.string.enter_username), Toast.LENGTH_SHORT).show()
            } else if (email.text.isNullOrBlank()) {
                email.error = getString(R.string.field_empty)
                Toast.makeText(thisContext, getString(R.string.enter_mail), Toast.LENGTH_SHORT).show()
            } else if (password.text.isNullOrBlank()) {
                password.error = getString(R.string.field_empty)
                Toast.makeText(thisContext, getString(R.string.enter_password), Toast.LENGTH_SHORT).show()
            } else {
                qwrt = password.text?.trim().toString()

                authViewModel.register(
                    User(
                        0,
                        username.text?.trim().toString(),
                        email.text?.trim().toString(),
                        password.text?.trim().toString()
                    )
                )
            }

        }

        buttonLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registrationFragment_to_loginFragment)
        }
    }

}