package com.vad.ltale.presentation.login

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.vad.ltale.R
import com.vad.ltale.data.User
import com.vad.ltale.data.remote.RetrofitInstance
import com.vad.ltale.data.repository.UserRepository
import com.vad.ltale.domain.CheckEmptyText
import com.vad.ltale.domain.HandleResponse
import com.vad.ltale.domain.Supplier
import com.vad.ltale.presentation.*

class LoginFragment : Fragment(), HandleResponse {

    private lateinit var mainViewModel: MainViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainViewModel = (requireActivity() as Supplier<*>).get() as MainViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val buttonLogin: Button = view.findViewById(R.id.loginButton)
        val username = view.findViewById(R.id.usernameLoginEditText) as TextInputEditText
        val password = view.findViewById(R.id.passwordLoginEditText) as TextInputEditText

        val factory = UserViewModelFactory(UserRepository(mainViewModel.getRetrofit()), this)

        val viewModel: UserViewModel = ViewModelProvider(this, factory).get(UserViewModel::class.java)

        buttonLogin.setOnClickListener {
            CheckEmptyText.check(username, password) {
                viewModel.getUserByUsername(username.text.toString())
                viewModel.userDetails.observe(viewLifecycleOwner) {
                    println(it)
                    mainViewModel.setUserDetails(User(it.userId ,username.text.toString().trim(), "", password.text.toString().trim()))
                    mainViewModel.setRetrofit(RetrofitInstance(mainViewModel.getUserDetails()))
                }
            }
        }
    }

    override fun success() {
        findNavController().navigate(R.id.accountFragment)
    }

    override fun error() {
        Toast.makeText(context, "Invalid password or username", Toast.LENGTH_SHORT).show()
    }

}