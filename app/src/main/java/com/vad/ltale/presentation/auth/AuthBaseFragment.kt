package com.vad.ltale.presentation.auth

import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.vad.ltale.R
import com.vad.ltale.data.remote.HandleResponse
import com.vad.ltale.data.remote.RemoteInstance
import com.vad.ltale.data.remote.exception.UnauthorizedException
import com.vad.ltale.data.remote.exception.UserAlreadyExistException
import com.vad.ltale.data.remote.exception.UserNotFoundException
import com.vad.ltale.data.repository.UserRepository
import com.vad.ltale.model.pojo.User
import com.vad.ltale.presentation.AuthViewModel
import com.vad.ltale.presentation.AuthViewModelFactory
import com.vad.ltale.presentation.BaseFragment

open class AuthBaseFragment : BaseFragment(), HandleResponse<User> {

    protected var qwrt = ""

    protected val authViewModel: AuthViewModel by activityViewModels {
        AuthViewModelFactory(UserRepository(RemoteInstance), this)
    }

    override fun error(e: Exception) {

        if (e is UserAlreadyExistException) {
            Toast.makeText(
                thisContext,
                getString(R.string.user_with_this_nik_already_exist), Toast.LENGTH_SHORT
            ).show()
        }
        if (e is UserNotFoundException) {
            Toast.makeText(
                thisContext,
                getString(R.string.invalid_password_or_username), Toast.LENGTH_SHORT
            ).show()
        }

        if (e is UnauthorizedException) {
            Toast.makeText(
                thisContext,
                getString(R.string.invalid_password_or_username), Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun success(t: User) {

        configuration.saveIdUser(t.userId)
        configuration.saveLogin(t.username)
        configuration.savePass(qwrt)
        configuration.saveFirstStart(true)

        RemoteInstance.setUser(User(t.userId, t.username, t.email, qwrt))
        RemoteInstance.setPicasso(thisContext)

        findNavController().navigate(R.id.action_registrationFragment_to_accountFragment)

    }

}