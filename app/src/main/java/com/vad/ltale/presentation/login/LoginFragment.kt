package com.vad.ltale.presentation.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.vad.ltale.R
import com.vad.ltale.data.UserDetails
import com.vad.ltale.data.remote.RetrofitInstance
import com.vad.ltale.presentation.FileViewModel
import com.vad.ltale.presentation.LoadViewModelFactory

class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val buttonLogin: Button = view.findViewById(R.id.loginButton)

        val username = view.findViewById(R.id.usernameLoginEditText) as TextView
        val password = view.findViewById(R.id.passwordLoginEditText) as TextView
        val factory = LoadViewModelFactory(RetrofitInstance(UserDetails(username.text.toString(), password.text.toString())))
        val load: FileViewModel = ViewModelProvider(this, factory).get(FileViewModel::class.java)

        buttonLogin.setOnClickListener { view.findNavController().navigate(R.id.accountFragment) }
    }

}