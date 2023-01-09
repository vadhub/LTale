package com.vad.ltale.presentation.login

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.vad.ltale.R
import com.vad.ltale.data.User
import com.vad.ltale.data.UserDetails
import com.vad.ltale.data.remote.RetrofitInstance
import com.vad.ltale.data.repository.UserRepository
import com.vad.ltale.domain.Supplier
import com.vad.ltale.presentation.*

class LoginFragment : Fragment() {

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

        username.doOnTextChanged { text, start, before, count ->
            if (text.toString().isEmpty()) {
                username.error = "must not be empty"
            }
        }

        password.doOnTextChanged { text, start, before, count ->
            if (text.toString().isEmpty()) {
                password.error = "must not be empty"
            }
        }

        buttonLogin.setOnClickListener {
            mainViewModel.setRetrofit(RetrofitInstance(UserDetails(username.text.toString().trim(), password.text.toString().trim())))
            val factory = UserViewModelFactory(UserRepository(mainViewModel.getRetrofit()))
            val viewModel: UserViewModel = ViewModelProvider(this, factory).get(UserViewModel::class.java)
            println(username.text.toString())
            viewModel.getUserByUsername(username.text.toString())
            viewModel.users.observe(viewLifecycleOwner) {
                println(it)
            }
            view.findNavController().navigate(R.id.accountFragment)
        }
    }

}