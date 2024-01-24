package com.vad.ltale

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.vad.ltale.data.local.SaveConfiguration
import com.vad.ltale.data.remote.HandleResponse
import com.vad.ltale.data.remote.RemoteInstance
import com.vad.ltale.data.remote.exception.UnauthorizedException
import com.vad.ltale.data.repository.UserRepository
import com.vad.ltale.model.pojo.User
import com.vad.ltale.presentation.AuthViewModel
import com.vad.ltale.presentation.AuthViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val configuration = SaveConfiguration(this)

    lateinit var bottomMenu: BottomNavigationView
        private set

    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_main)
        bottomMenu = findViewById(R.id.bottom_menu)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(setOf(R.id.accountFragment, R.id.registrationFragment))
        setupActionBarWithNavController(navController, appBarConfiguration)
        bottomMenu.setupWithNavController(navController)

        if (!configuration.getFirstStart()) {
            configuration.saveFirstStart(true)
        } else {
            bottomMenu.visibility = View.VISIBLE
            RemoteInstance.setUser(
                User(
                    configuration.getIdUser(),
                    configuration.getLogin(),
                    "",
                    configuration.getPass()
                )
            )
            RemoteInstance.setPicasso(this)
            navController.navigate(R.id.action_registrationFragment_to_accountFragment)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    fun setActionBarTitle(title: String) {
        toolbar.title = title
    }

}