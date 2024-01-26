package com.vad.ltale

import android.content.Context
import android.content.pm.ActivityInfo
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.vad.ltale.data.local.SaveConfiguration
import com.vad.ltale.data.remote.RemoteInstance
import com.vad.ltale.model.pojo.User

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

        if (isAccessInternet(this)) {
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

                Log.d(
                    "$$$",
                    "${configuration.getPass()} ${configuration.getIdUser()} ${configuration.getLogin()} "
                )
                RemoteInstance.setPicasso(this)
                navController.navigate(R.id.action_registrationFragment_to_accountFragment)
            }
        } else {
            Snackbar.make(
                bottomMenu,
                resources.getString(R.string.internet_available),
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    fun isAccessInternet(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!.isConnected
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