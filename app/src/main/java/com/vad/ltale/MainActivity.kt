package com.vad.ltale

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.vad.ltale.data.User
import com.vad.ltale.data.remote.RemoteInstance
import com.vad.ltale.data.repository.UserRepository
import com.vad.ltale.data.local.SaveConfiguration
import com.vad.ltale.presentation.MainViewModelProvider
import com.vad.ltale.presentation.HandleResponse
import com.vad.ltale.presentation.MainViewModel
import com.vad.ltale.presentation.UserViewModel
import com.vad.ltale.presentation.UserViewModelFactory

class MainActivity : AppCompatActivity(), MainViewModelProvider, HandleResponse {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var bottomMenu: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomMenu = findViewById(R.id.bottom_menu)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val factory = UserViewModelFactory(UserRepository(mainViewModel.getRetrofit()), this)
        val viewModel: UserViewModel =
            ViewModelProvider(this, factory).get(UserViewModel::class.java)

        val configuration = SaveConfiguration(this)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(setOf(R.id.accountFragment, R.id.registrationFragment))

        setupActionBarWithNavController(navController, appBarConfiguration)
        bottomMenu.visibility = View.VISIBLE

        bottomMenu.setupWithNavController(navController)

        if (!configuration.getFirstStart()) {
            configuration.saveFirstStart(true)
        } else {

            viewModel.getUserByUsername(configuration.getLogin())
            viewModel.userDetails.observe(this) {
                println(it)
                mainViewModel.setUserDetails(
                    User(
                        it.userId,
                        configuration.getLogin(),
                        "",
                        configuration.getPass()
                    )
                )
                mainViewModel.setRetrofit(RemoteInstance(mainViewModel.getUserDetails()))

            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun get(): MainViewModel = mainViewModel

    override fun error() {

    }

    override fun success() {
        navController.navigate(R.id.action_registrationFragment_to_accountFragment)
    }

}