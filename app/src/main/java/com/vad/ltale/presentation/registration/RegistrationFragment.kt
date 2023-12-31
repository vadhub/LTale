package com.vad.ltale.presentation.registration

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.vad.ltale.R
import com.vad.ltale.model.User
import com.vad.ltale.data.repository.UserRepository
import com.vad.ltale.data.remote.HandleResponse
import com.vad.ltale.presentation.AuthViewModel
import com.vad.ltale.presentation.AuthViewModelFactory
import com.vad.ltale.presentation.BaseFragment


class RegistrationFragment : BaseFragment(), HandleResponse<User> {

    private var qwr = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_registration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val buttonRegistration: Button = view.findViewById(R.id.enterRegistrationButton)
        val buttonLogin: Button = view.findViewById(R.id.loginRegistrationButton)

        val username = view.findViewById(R.id.nikEditText) as TextInputEditText
        val email = view.findViewById(R.id.emailEditText) as TextInputEditText
        val password = view.findViewById(R.id.passwordEditText) as TextInputEditText

        val factory = AuthViewModelFactory(UserRepository(mainViewModel.getRetrofit()), this)
        val authViewModel = ViewModelProvider(this, factory).get(AuthViewModel::class.java)

        //todo check on empty
        buttonRegistration.setOnClickListener {
            authViewModel.createUser(
                User(
                    0,
                    username.text.toString(),
                    email.text.toString(),
                    password.text.toString()
                )
            )
        }

        qwr = password.text.toString()

        buttonLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registrationFragment_to_loginFragment)
        }
    }

    override fun error(e: String) {
        Log.d("Registration", "error: $e")
        Toast.makeText(context, "Illegal registration", Toast.LENGTH_SHORT).show()
    }

    override fun success(t: User) {
        mainViewModel.setUserDetails(
            User(
                t.userId,
                t.username,
                t.email,
                qwr
            )
        )
        configuration.saveLogin(t.username)
        configuration.savePass(qwr)
        configuration.saveFirstStart(true)
        findNavController().navigate(R.id.action_registrationFragment_to_accountFragment)
    }
}