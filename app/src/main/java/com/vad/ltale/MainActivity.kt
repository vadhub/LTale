package com.vad.ltale

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.vad.ltale.data.User
import com.vad.ltale.data.remote.RemoteInstance
import com.vad.ltale.data.repository.UserRepository
import com.vad.ltale.domain.HandleResponse
import com.vad.ltale.domain.SaveConfiguration
import com.vad.ltale.presentation.MainViewModel
import com.vad.ltale.domain.Supplier
import com.vad.ltale.presentation.UserViewModel
import com.vad.ltale.presentation.UserViewModelFactory


class MainActivity : AppCompatActivity(), Supplier<MainViewModel>, HandleResponse {

    private lateinit var navController: NavController
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val factory = UserViewModelFactory(UserRepository(mainViewModel.getRetrofit()), this)
        val viewModel: UserViewModel = ViewModelProvider(this, factory).get(UserViewModel::class.java)

        val configuration = SaveConfiguration(this)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        if (configuration.getFirstStart()) {
            configuration.saveFirstStart(true)
            setupActionBarWithNavController(navController)
        } else {
            if (configuration.getLogin() == "") {
                setupActionBarWithNavController(navController)
            } else {
                viewModel.getUserByUsername(configuration.getLogin())
                viewModel.userDetails.observe(this) {
                    println(it)
                    mainViewModel.setUserDetails(User(it.userId ,configuration.getLogin(), "", configuration.getPass()))
                    mainViewModel.setRetrofit(RemoteInstance(mainViewModel.getUserDetails()))
                }
            }
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun get(): MainViewModel = mainViewModel

    override fun error() {

    }

    override fun success() {
        navController.navigate(R.id.accountFragment)
    }

}