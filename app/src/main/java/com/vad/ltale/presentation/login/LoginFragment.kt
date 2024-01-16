package com.vad.ltale.presentation.login

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
import com.vad.ltale.data.remote.RemoteInstance
import com.vad.ltale.data.repository.UserRepository
import com.vad.ltale.data.remote.HandleResponse
import com.vad.ltale.data.remote.exception.UnauthorizedException
import com.vad.ltale.data.remote.exception.UserNotFoundException
import com.vad.ltale.presentation.AuthViewModel
import com.vad.ltale.presentation.AuthViewModelFactory
import com.vad.ltale.presentation.BaseFragment
import com.vad.ltale.presentation.UserViewModel
import com.vad.ltale.presentation.UserViewModelFactory

class LoginFragment : BaseFragment(), HandleResponse<User> {

    private lateinit var username: TextInputEditText
    private lateinit var password: TextInputEditText

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

        username.setText("anton")
        password.setText("1234")

        val factory = AuthViewModelFactory(UserRepository(mainViewModel.getRetrofit()), this)
        val viewModel: AuthViewModel =
            ViewModelProvider(this, factory).get(AuthViewModel::class.java)

        buttonLogin.setOnClickListener {

            if (username.text.isNullOrBlank()) {
                Toast.makeText(thisContext, "Enter username", Toast.LENGTH_SHORT).show()
            } else if (password.text.isNullOrBlank()) {
                Toast.makeText(thisContext, "Enter password", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.login(username.text.toString())
            }
        }
    }

    override fun success(t: User) {
        configuration.saveLogin(username.text.toString())
        configuration.savePass(password.text.toString())

        mainViewModel.setUserDetails(
            User(
                t.userId,
                username.text.toString().trim(),
                "",
                password.text.toString().trim()
            )
        )
        mainViewModel.setRetrofit(RemoteInstance(mainViewModel.getUserDetails()))
        findNavController().navigate(R.id.accountFragment)
    }

    override fun error(e: Exception) {
        if (e is UserNotFoundException) {
            Toast.makeText(thisContext, "Invalid password or username", Toast.LENGTH_SHORT).show()
        }
    }

}