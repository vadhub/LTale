package com.vad.ltale.presentation.registration

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.vad.ltale.R
import com.vad.ltale.data.User
import com.vad.ltale.data.remote.RetrofitInstance
import com.vad.ltale.data.repository.UserRepository
import com.vad.ltale.domain.CheckEmptyText
import com.vad.ltale.domain.Supplier
import com.vad.ltale.presentation.MainViewModel
import com.vad.ltale.presentation.UserViewModel
import com.vad.ltale.presentation.UserViewModelFactory


class RegistrationFragment : Fragment() {

    private lateinit var mainViewModel: MainViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainViewModel = (requireActivity() as Supplier<*>).get() as MainViewModel
    }

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



        val factory = UserViewModelFactory(UserRepository(RetrofitInstance(User(0,"", "", ""))))
        val userViewModel = ViewModelProvider(this, factory).get(UserViewModel::class.java)

        buttonRegistration.setOnClickListener {
            CheckEmptyText.check(username, email, password) {
                userViewModel.createUser(
                    User(0,
                        username.text.toString(),
                        email.text.toString(),
                        password.text.toString()
                    )
                )
                view.findNavController().navigate(R.id.accountFragment)
            }
        }
        buttonLogin.setOnClickListener {
            view.findNavController().navigate(R.id.action_registrationFragment_to_loginFragment)
        }
    }
}