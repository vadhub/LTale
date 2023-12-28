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
import com.vad.ltale.presentation.BaseFragment
import com.vad.ltale.presentation.UserViewModel
import com.vad.ltale.presentation.UserViewModelFactory

class LoginFragment : BaseFragment(), HandleResponse {

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

        val factory = UserViewModelFactory(UserRepository(mainViewModel.getRetrofit()), this)
        val viewModel: UserViewModel =
            ViewModelProvider(this, factory).get(UserViewModel::class.java)

        buttonLogin.setOnClickListener {
            viewModel.getUserByUsername(username.text.toString())
            viewModel.userDetails.observe(viewLifecycleOwner) {
                mainViewModel.setUserDetails(
                    User(
                        it.userId,
                        username.text.toString().trim(),
                        "",
                        password.text.toString().trim()
                    )
                )
                mainViewModel.setRetrofit(RemoteInstance(mainViewModel.getUserDetails()))
            }
        }
    }

    override fun success() {
        configuration.saveLogin(username.text.toString())
        configuration.savePass(password.text.toString())
        findNavController().navigate(R.id.accountFragment)
    }

    override fun error(e: String) {
        Log.d("Login", "error: $e")
        Toast.makeText(context, "Invalid password or username", Toast.LENGTH_SHORT).show()
    }

}