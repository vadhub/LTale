package com.vad.ltale.presentation.registration

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.vad.ltale.R
import com.vad.ltale.data.User
import com.vad.ltale.data.UserDetails
import com.vad.ltale.data.remote.RetrofitInstance
import com.vad.ltale.data.repository.UserRepository
import com.vad.ltale.domain.SaveDataPref
import com.vad.ltale.domain.Supplier
import com.vad.ltale.presentation.MainViewModel
import com.vad.ltale.presentation.UserViewModel
import com.vad.ltale.presentation.UserViewModelFactory
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class RegistrationFragment : Fragment() {

    private lateinit var nikName: EditText
    private lateinit var password: EditText
    private lateinit var email: EditText
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

        nikName = view.findViewById(R.id.nikEditText)
        email = view.findViewById(R.id.emailEditText)
        password = view.findViewById(R.id.passwordEditText)

        val factory = UserViewModelFactory(UserRepository(RetrofitInstance(UserDetails("", ""))))
        val userViewModel = ViewModelProvider(this, factory).get(UserViewModel::class.java)

        buttonRegistration.setOnClickListener {
            userViewModel.createUser(User(nikName.text.toString(), email.text.toString(), password.text.toString()))
            view.findNavController().navigate(R.id.accountFragment)
        }
        buttonLogin.setOnClickListener { view.findNavController().navigate(R.id.action_registrationFragment_to_loginFragment) }
    }
}