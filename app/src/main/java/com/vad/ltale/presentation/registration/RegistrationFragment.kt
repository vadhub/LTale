package com.vad.ltale.presentation.registration

import android.os.Bundle
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
import com.vad.ltale.presentation.BaseFragment
import com.vad.ltale.presentation.UserViewModel
import com.vad.ltale.presentation.UserViewModelFactory


class RegistrationFragment : BaseFragment(), HandleResponse {

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

        val factory = UserViewModelFactory(UserRepository(mainViewModel.getRetrofit()), this)
        val userViewModel = ViewModelProvider(this, factory).get(UserViewModel::class.java)

        //todo check on empty
        buttonRegistration.setOnClickListener {
                userViewModel.createUser(
                    User(0,
                        username.text.toString(),
                        email.text.toString(),
                        password.text.toString()
                    )
                )
        }

        userViewModel.userDetails.observe(viewLifecycleOwner) {
            mainViewModel.setUserDetails(User(it.userId, it.username, it.email, password.text.toString()))
            configuration.saveLogin(it.username)
            configuration.savePass(password.text.toString())
            configuration.saveFirstStart(true)
        }

        buttonLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registrationFragment_to_loginFragment)
        }
    }

    override fun error() {
        Toast.makeText(context, "Illegal registration", Toast.LENGTH_SHORT).show()
    }

    override fun success() {
        findNavController().navigate(R.id.action_registrationFragment_to_accountFragment)
    }
}