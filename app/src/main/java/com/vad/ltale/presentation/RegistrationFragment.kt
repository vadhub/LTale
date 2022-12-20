package com.vad.ltale.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.navigation.findNavController
import com.vad.ltale.R
import com.vad.ltale.data.User
import com.vad.ltale.data.remote.RetrofitInstance
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class RegistrationFragment : Fragment() {

    private lateinit var nikName: EditText
    private lateinit var password: EditText
    private lateinit var email: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_registration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val buttonRegistration: Button = view.findViewById(R.id.enterRegistrationButton)
        val buttonLogin: Button = view.findViewById(R.id.loginRegistrationButton)

        nikName = view.findViewById(R.id.nikEditText)
        email = view.findViewById(R.id.emailEditText)
        password = view.findViewById(R.id.passwordEditText)

        buttonRegistration.setOnClickListener {
            println(password.text.toString())
            postValue(User(nikName.text.toString(), email.text.toString(), password.text.toString()))
            getUser()
            view.findNavController().navigate(R.id.accountFragment)
        }
        buttonLogin.setOnClickListener { view.findNavController().navigate(R.id.action_registrationFragment_to_loginFragment) }
    }

    fun postValue(user: User) = runBlocking { launch {
        println("---------------------------")
        RetrofitInstance().apiUser.postUser(user)
    } }

    fun getUser() = runBlocking { launch {
        println("---------------------------")
        println(RetrofitInstance().apiUser.getUser().body()?.embedded?.users)
    } }
}